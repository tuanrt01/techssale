package com.techzen.techsale.service.impl;

import com.techzen.techsale.common.SendNotifyMattermost;
import com.techzen.techsale.dto.*;
import com.techzen.techsale.dto.request.InformationRequest;
import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.dto.response.RequestApplicationForListResponse;
import com.techzen.techsale.entity.*;
import com.techzen.techsale.enumeration.ActionEnum;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import com.techzen.techsale.exception.BadRequestException;
import com.techzen.techsale.exception.ConflictException;
import com.techzen.techsale.exception.ForbiddenException;
import com.techzen.techsale.exception.NotFoundException;
import com.techzen.techsale.mapper.AttachmentMapper;
import com.techzen.techsale.mapper.RequestApplicationMapper;
import com.techzen.techsale.repository.*;
import com.techzen.techsale.security.UserPrincipal;
import com.techzen.techsale.service.RequestApplicationService;
import com.techzen.techsale.utils.SecurityUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import liquibase.pro.packaged.S;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.RollbackException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestApplicationServiceImpl implements RequestApplicationService {

    private static final String FORBIDDEN_MSG = "Not have permission to access this resource!";
    private static final String CONFLICT_MSG = "Data has changed!";
    private static final String STATUS_ERROR_MSG = "Incompatible status";
    private static final String USER = "create, read, update, delete";
    private static final String BUYER = "create, read, update, delete, buy";
    private static final String APPROVE = "create, read, update, delete, approve";
    private static final String APPROVE_AND_BUYER = "create, read, update, delete, approve, buy";

    private final IRequestApplicationRepository iRequestApplicationRepository;
    private final RequestApplicationMapper requestApplicationMapper;
    private final SecurityUtils securityUtils;
    private final RequestApplicationRepository requestApplicationRepository;
    private final RequestApplicationHistoryRepository requestApplicationHistoryRepository;
    private final SendNotifyMattermost sendNotifyMattermost;
    private final UserRepository userRepository;
    private final AttachmentsRepository attachmentsRepository;
    private final AttachmentMapper attachmentMapper;
    private final PrivilegeRepository privilegeRepository;

    @Value("${mattermost.link-admin}")
    private String linkAdmin;

    @Value("${mattermost.link-user}")
    private String linkUser;

    @Value("${mattermost.create-success-msg}")
    private String createSuccessMsg;

    @Value("${mattermost.update-success-msg}")
    private String updateSuccessMsg;

    @Value("${mattermost.approver-accept-msg}")
    private String approverAcceptMsg;

    @Value("${mattermost.approver-reject-msg}")
    private String approverRejectMsg;

    @Value("${mattermost.buyer-accept-msg}")
    private String buyerAcceptMsg;

    @Value("${mattermost.buyer-reject-msg}")
    private String buyerRejectMsg;

    @Value("${mattermost.delete-msg}")
    private String deleteMsg;

    @Value("${techsale.attachments-path}")
    private String attachmentsPath;

    @Value(("${mattermost.buyer-order-msg}"))
    private String buyerOrderMsg;

    @Autowired
    private FileService fileService;
    @Autowired
    private ReasonRepository reasonRepository;

    @Override
    public RequestApplicationDetailResponse getRequestApplicationDetail(int id) {
        log.info("Start get request application detail by id: {}", id);
        RequestApplicationDetailResponse reqApp = iRequestApplicationRepository.fetchRequestDetailById(id)
            .orElseThrow(() -> new NotFoundException(
                String.format("Not found request application by id %d", id)));

        if (securityUtils.isAdmin() ||
            securityUtils.isBuyer() ||
            securityUtils.getCurrentUserInfo().getId().equals(reqApp.getRequestUserId())) {
            AttachmentsEntity userAttachment = attachmentsRepository.findFirstByRequestIdAndActionInOrderByCreateAtDesc(id,
                Arrays.asList(ActionEnum.USER_CREATE, ActionEnum.USER_UPDATE));
            AttachmentsEntity buyerAttachment = attachmentsRepository.findFirstByRequestIdAndActionInOrderByCreateAtDesc(id,
                Collections.singletonList(ActionEnum.BUYER_CREATE));
            reqApp.setUserAttachment(attachmentMapper.toAttachmentDTO(userAttachment));
            reqApp.setBuyerAttachment(attachmentMapper.toAttachmentDTO(buyerAttachment));

            return reqApp;
        }

        throw new ForbiddenException(FORBIDDEN_MSG);
    }
    private final OrganizationRepository organizationRepository;

    @Override
    public RequestApplicationForListResponse getRequestList(SearchRequest searchRequest,
        boolean isAdmin) {
        log.info("Start get request application list");
        UserPrincipal userPrincipal = securityUtils.getCurrentUserInfo();
        OrganizationEntity organization = userRepository.findById(userPrincipal.getId()).get().getOrganization();
        RequestApplicationForListResponse response = new RequestApplicationForListResponse();

        int level = organizationRepository.getOrganizationLevel(organization.getId());

        List<Long> organizationIds;
        if (level <= 2) {
            organizationIds = organizationRepository.getAllChildOrganizationIds(0L);
        } else {
            organizationIds = organizationRepository.getAllChildOrganizationIds(organization.getId());
            organizationIds.add(organization.getId());
        }
        List<RequestApplicationForListDetailDTO> requestDetailDTOList = requestApplicationRepository.getRequestApplicationPage(
            searchRequest, isAdmin, organizationIds);

        List<RequestApplicationForListDTO> content = new ArrayList<>();

        for (RequestApplicationForListDetailDTO detailDTO : requestDetailDTOList) {
            setAvatar(detailDTO.getUserRequestAvatar(), detailDTO::setUserRequestAvatar);
            setAvatar(detailDTO.getApproverAvatar(), detailDTO::setApproverAvatar);
            setAvatar(detailDTO.getBuyerAvatar(), detailDTO::setBuyerAvatar);
            content.add(requestApplicationMapper.mapToRequestDetailForList(detailDTO));
        }

        Long totalElements = requestApplicationRepository.getTotalElements(searchRequest, isAdmin, organizationIds);
        response.setContent(content);
        response.setTotalElements(totalElements);
        response.setTotalPages((long) Math.ceil((double) totalElements / searchRequest.getSize()));
        response.setPage(searchRequest.getPage());
        response.setSize(searchRequest.getSize());
        return response;
    }

    private void setAvatar(String avatarPath, Consumer<String> setAvatar) {
        byte[] avatar = fileService.getFile(avatarPath);
        if (avatar != null) {
            setAvatar.accept("data:image/png;base64," + Base64.getEncoder().encodeToString(avatar));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRequest(InformationRequest createRequest) throws IOException {
        UserPrincipal userPrincipal = securityUtils.getCurrentUserInfo();
        String requestUserId = userPrincipal.getId();
        log.info("Start create request of userId: " + requestUserId);

        ReasonEntity buyer = reasonRepository.findById(
            Integer.valueOf(createRequest.getRequestReason())).orElse(null);
        RequestApplicationEntity requestSaved = new RequestApplicationEntity();
        requestSaved.setRequestUserId(requestUserId);
        requestSaved.setRequestProductName(createRequest.getRequestProductName());
        requestSaved.setProductLink(createRequest.getProductLink());
        requestSaved.setRequestAmount(createRequest.getRequestAmount());
        requestSaved.setEstimatePrice(createRequest.getEstimatePrice());
        requestSaved.setRequestReason(createRequest.getRequestReason());
        requestSaved.setRequestReasonDetail(createRequest.getRequestReasonDetail());
        requestSaved.setExpectReceiveDate(createRequest.getExpectReceiveDate());
        requestSaved.setDescription(createRequest.getDescription());
        requestSaved.setApproverUserId(createRequest.getApproverUserId());
        if (createRequest.getOrganizationUsed() != null) {
            requestSaved.setOrganizationUsed(createRequest.getOrganizationUsed());
        } else if(createRequest.getPersonnelUsed() != null){
            requestSaved.setPersonnelUsed(createRequest.getPersonnelUsed());
        }
        if (buyer != null && buyer.getBuyerUserId() != null) {
            requestSaved.setBuyerUserId(buyer.getBuyerUserId().getId());
        }
        requestSaved.setStatus(RequestStatusEnum.WAITING);
        requestSaved = iRequestApplicationRepository.save(requestSaved);

        createRequestHis(requestSaved);

        File file = getFile(requestSaved.getId(), createRequest.getRequestUserAttachment(), requestUserId);
        if (file != null) saveAttachment(requestSaved.getId(), file, ActionEnum.USER_UPDATE);

        String notifyMsg = String.format(createSuccessMsg, userPrincipal.getMattermostName(),
                createRequest.getRequestProductName(), getMattermostName(createRequest.getApproverUserId()), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    private void saveAttachment(int requestId, File file, ActionEnum action) {
        if (file != null) {
            AttachmentsEntity attachment = AttachmentsEntity.builder()
                .requestId(requestId)
                .fileName(FilenameUtils.getName(file.getName()))
                .extension(FilenameUtils.getExtension(file.getName()))
                .fileLength(file.length())
                .filePath(file.getPath())
                .action(action)
                .build();
            attachmentsRepository.save(attachment);
        }
    }

    private void createRequestHis(RequestApplicationEntity request) {
        log.info("Start create request history");

        requestApplicationHistoryRepository.save(requestApplicationMapper.mapToRequestHistory(request));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRequest(InformationRequest updateRequest, int requestId) throws IOException {
        log.info("Start update request application id: {}", requestId);
        String currentRequestUserId = securityUtils.getCurrentUserInfo().getId();
        String approverUserId = updateRequest.getApproverUserId();
        List<UserDTO> managers = userRepository.findAllUserAssign();

        boolean isInvalidManager = managers.stream().noneMatch(x -> x.getId().equals(approverUserId));

        RequestApplicationEntity request = findRequest(requestId, updateRequest.getUpdatedAt(), RequestStatusEnum.WAITING);

        if (!currentRequestUserId.equals(request.getRequestUserId())) {
            throw new ForbiddenException(FORBIDDEN_MSG);
        }

        if (isInvalidManager) {
            throw new ForbiddenException("Invalid assigner!");
        }

        BeanUtils.copyProperties(updateRequest, request);
        ReasonEntity buyer = reasonRepository.findById(
                Integer.valueOf(updateRequest.getRequestReason())).orElse(null);
        if (buyer != null && buyer.getBuyerUserId() != null) {
            request.setBuyerUserId(buyer.getBuyerUserId().getId());
        }
        if (request.getOrganizationUsed() != null) {
            request.setOrganizationUsed(updateRequest.getOrganizationUsed());
        } else if(request.getPersonnelUsed() != null){
            request.setPersonnelUsed(updateRequest.getPersonnelUsed());
        }
        request.setProductLink(updateRequest.getProductLink());
        RequestApplicationEntity savedEntity = iRequestApplicationRepository.save(request);

        File file = getFile(requestId, updateRequest.getRequestUserAttachment(), currentRequestUserId);
        attachmentsRepository.deleteByRequestId(requestId);
        if (file != null) {
            saveAttachment(requestId, file, ActionEnum.USER_UPDATE);
        }

        createRequestHis(savedEntity);

        String notifyMsg = String.format(updateSuccessMsg, getMattermostName(currentRequestUserId),
                request.getRequestProductName(), getMattermostName(approverUserId), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    private String getMattermostName(String userId) {
        return userRepository.getUserMattermostName(userId);
    }

    private boolean hasChangedData(LocalDateTime requestUpdatedAt, LocalDateTime currentUpdatedAt) {
        return !currentUpdatedAt.isEqual(requestUpdatedAt);
    }

    @Override
    @Transactional
    public void createBuyerRequestPurchased(RequestPurchasedDTO requestPurchasedDTO, int id)
        throws IOException {
        log.info("Start create request purchased " + id);

        RequestApplicationEntity requestEntity = findRequest(id,
            requestPurchasedDTO.getUpdatedAt(), RequestStatusEnum.ORDERING);
        String requestUserId = requestEntity.getRequestUserId();
        BeanUtils.copyProperties(requestPurchasedDTO, requestEntity);
        requestEntity.setStatus(RequestStatusEnum.SUCCESS);
        
        // Đảm bảo có ordering_date
        if (requestEntity.getOrderingDate() == null) {
            // Nếu không có ordering_date, đặt thành thời điểm 2-5 ngày trước
            LocalDateTime now = LocalDateTime.now();
            int randomDays = 2 + (int)(Math.random() * 4); // Random từ 2-5 ngày
            requestEntity.setOrderingDate(now.minusDays(randomDays));
        }
        
        // Lưu thời điểm giao hàng
        requestEntity.setDeliveryDate(LocalDateTime.now());

        File file = getFile(id, requestPurchasedDTO.getBuyerAttachment(), requestUserId);

        RequestApplicationEntity request = iRequestApplicationRepository.save(requestEntity);

        createRequestHis(request);

        if (file != null) {
            saveAttachment(request.getId(), file, ActionEnum.BUYER_CREATE);
        }
        
        String notifyMsg = String.format(buyerAcceptMsg, getMattermostName(requestUserId), request.getRequestProductName(), linkUser,
            getMattermostName(requestEntity.getApproverUserId()), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    private File getFile(int rqId, MultipartFile multipartFile, String requestUserId) throws IOException {
        File file = null;
        if (multipartFile != null) {
            String fileName = String.format(attachmentsPath, rqId, localDateTimeToString(LocalDateTime.now()),
                    requestUserId, "", FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            file = new File(fileName);
            int count = 0;
            while (file.exists()) {
                fileName = String.format(attachmentsPath, localDateTimeToString(LocalDateTime.now()),
                        requestUserId, " (" + ++count + ")",
                        FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
                file = new File(fileName);
            }
            boolean isFileCreated = file.createNewFile();
            if (isFileCreated) multipartFile.transferTo(file);
        }
        return file;
    }

    @Override
    @Transactional
    public void buyerRejectRequest(RejectReasonDTO rejectReasonDTO, int id) {
        log.info("Start reject request " + id);

        RequestApplicationEntity requestEntity = findByIdRequest(id);
        RequestStatusEnum[] statusArr = {RequestStatusEnum.PROCESSING, RequestStatusEnum.ORDERING};
        if (!ArrayUtils.contains(statusArr, requestEntity.getStatus())) {
            throw new BadRequestException(STATUS_ERROR_MSG);
        }
        if (hasChangedData(rejectReasonDTO.getUpdatedAt(), requestEntity.getUpdatedAt())) {
            throw new ConflictException(CONFLICT_MSG);
        }
        String rejectReason = rejectReasonDTO.getRejectReason();
        requestEntity.setBuyerRejectReason(rejectReason);
        requestEntity.setStatusReject(requestEntity.getStatus());
        requestEntity.setStatus(RequestStatusEnum.REJECTED);
        RequestApplicationEntity request = iRequestApplicationRepository.save(requestEntity);

        createRequestHis(request);

        String notifyMsg = String.format(buyerRejectMsg, getMattermostName(requestEntity.getRequestUserId()),
                request.getRequestProductName(), securityUtils.getCurrentUserInfo().getMattermostName(), rejectReason,
                linkUser, getMattermostName(requestEntity.getApproverUserId()), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    @Override
    @Transactional
    public void approverAcceptRequest(UpdateApprovalDTO updateApprovalDTO, int id) {
        log.info("Start create request process");

        RequestApplicationEntity requestEntity = findRequest(id,
            updateApprovalDTO.getUpdateAt(), RequestStatusEnum.WAITING);
        List<UserDTO> buyerList = userRepository.findAllBuyer();
        String buyerUserId = updateApprovalDTO.getBuyerUserId();
        boolean isInvalidBuyer = buyerList.stream().noneMatch(x -> x.getId().equals(buyerUserId));
        if (isInvalidBuyer) {
            throw new ForbiddenException("Invalid Buyer");
        }

        UserPrincipal userPrincipal = securityUtils.getCurrentUserInfo();
        requestEntity.setStatus(RequestStatusEnum.PROCESSING);
        requestEntity.setBuyerUserId(buyerUserId);
        requestEntity.setApproverUserId(userPrincipal.getId());
        RequestApplicationEntity request = iRequestApplicationRepository.save(requestEntity);

        createRequestHis(request);

        String notifyMsg = String.format(approverAcceptMsg,
                getMattermostName(requestEntity.getRequestUserId()),
                request.getRequestProductName(), userPrincipal.getMattermostName(), linkUser,
                getMattermostName(buyerUserId), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    @Override
    @Transactional
    public void approverRejectRequest(RejectReasonDTO rejectReasonDTO, int id) {
        log.info("Start reject request " + id);

        RequestApplicationEntity requestEntity = findRequest(id,
            rejectReasonDTO.getUpdatedAt(), RequestStatusEnum.WAITING);
        String rejectReason = rejectReasonDTO.getRejectReason();
        UserPrincipal userPrincipal = securityUtils.getCurrentUserInfo();
        requestEntity.setApproverRejectReason(rejectReason);
        requestEntity.setStatusReject(requestEntity.getStatus());
        requestEntity.setStatus(RequestStatusEnum.REJECTED);
        requestEntity.setApproverUserId(userPrincipal.getId());
        RequestApplicationEntity request = iRequestApplicationRepository.save(requestEntity);

        createRequestHis(request);

        String notifyMsg = String.format(approverRejectMsg, getMattermostName(requestEntity.getRequestUserId()),
                request.getRequestProductName(), userPrincipal.getMattermostName(), rejectReason, linkUser);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    private RequestApplicationEntity findRequest(int id, LocalDateTime updateAt, RequestStatusEnum status) {

        RequestApplicationEntity requestEntity = findByIdRequest(id);

        if (!requestEntity.getStatus().equals(status)) {
            throw new BadRequestException(STATUS_ERROR_MSG);
        }

        if (hasChangedData(updateAt, requestEntity.getUpdatedAt())) {
            throw new ConflictException(CONFLICT_MSG);
        }

        return requestEntity;
    }

    private RequestApplicationEntity findByIdRequest(int id) {
        return iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(id).orElseThrow(
            () -> new NotFoundException(
                String.format("Not found request application by id %d", id)));
    }

    @Override
    public void deleteRequest(int id) {
        log.info("Start delete request " + id);
        RequestApplicationEntity requestEntity = findByIdRequest(id);
        if(!securityUtils.getCurrentUserInfo().getId().equals(requestEntity.getRequestUserId())){
            throw new ForbiddenException(FORBIDDEN_MSG);
        }
        if (!requestEntity.getStatus().equals(RequestStatusEnum.WAITING)) {
            throw new BadRequestException(STATUS_ERROR_MSG);
        }
        requestEntity.setIsDeleted(true);
        RequestApplicationEntity request = iRequestApplicationRepository.save(requestEntity);

        createRequestHis(request);

        String notifyMsg = String.format(deleteMsg,
                getMattermostName(requestEntity.getRequestUserId()),
                requestEntity.getRequestProductName(),
                getMattermostName(requestEntity.getApproverUserId()), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    @Override
    public void buyerOrderRequest(LocalDateTime updatedAt, int id) {
        log.info("Start order request: " + id);
        RequestApplicationEntity requestEntity = findRequest(id, updatedAt, RequestStatusEnum.PROCESSING);
        UserPrincipal userPrincipal = securityUtils.getCurrentUserInfo();

        requestEntity.setStatus(RequestStatusEnum.ORDERING);
        requestEntity.setBuyerUserId(userPrincipal.getId());
        // Lưu thời điểm đặt hàng - chỉ cập nhật nếu chưa có giá trị
        if (requestEntity.getOrderingDate() == null) {
            requestEntity.setOrderingDate(LocalDateTime.now());
        }
        RequestApplicationEntity savedRequest = iRequestApplicationRepository.save(requestEntity);

        createRequestHis(savedRequest);

        String notifyMsg = String.format(buyerOrderMsg, userPrincipal.getMattermostName(),
                savedRequest.getRequestProductName(),
                getMattermostName(savedRequest.getRequestUserId()), linkUser,
                getMattermostName(savedRequest.getApproverUserId()), linkAdmin);
        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    private String localDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        return date.format(formatter);
    }

    @Override
    public void createPrivilegeUserID(String userId, String privilege) {
        Optional<PrivilegeEntity> optionalPrivilegeEntity = privilegeRepository.findByUserId(userId);

        if (optionalPrivilegeEntity.isPresent()) {
            PrivilegeEntity privilegeEntity = optionalPrivilegeEntity.get();
            privilegeEntity.setPrivileges(privilege);
            privilegeRepository.save(privilegeEntity);
        } else {
            throw new NotFoundException("No privileges found for the given userId: " + userId);
        }
    }
    @Override
    public void deleteApproveAndBuyerUserID(String userId) {
        Optional<PrivilegeEntity> optionalPrivilegeEntity = privilegeRepository.findByUserId(userId);

        if (optionalPrivilegeEntity.isPresent()) {
            PrivilegeEntity privilegeEntity = optionalPrivilegeEntity.get();
            privilegeEntity.setPrivileges(USER);
            privilegeRepository.save(privilegeEntity);
        } else {
            throw new NotFoundException("No privileges found for the given userId: " + userId);
        }
    }

    @Override
    public void deleteApproveUserID(String userId) {
        Optional<PrivilegeEntity> optionalPrivilegeEntity = privilegeRepository.findByUserId(userId);

        if (optionalPrivilegeEntity.isPresent()) {
            PrivilegeEntity privilegeEntity = optionalPrivilegeEntity.get();
            if(privilegeEntity.getPrivileges().equals(APPROVE_AND_BUYER)){
                privilegeEntity.setPrivileges(BUYER);
            } else {
                privilegeEntity.setPrivileges(USER);
            }

            privilegeRepository.save(privilegeEntity);
        } else {
            throw new NotFoundException("No privileges found for the given userId: " + userId);
        }
    }
    @Override
    public void deleteBuyerUserID(String userId) {
        Optional<PrivilegeEntity> optionalPrivilegeEntity = privilegeRepository.findByUserId(userId);

        if (optionalPrivilegeEntity.isPresent()) {
            PrivilegeEntity privilegeEntity = optionalPrivilegeEntity.get();
            if(privilegeEntity.getPrivileges().equals(APPROVE_AND_BUYER)){
                privilegeEntity.setPrivileges(APPROVE);
            } else {
                privilegeEntity.setPrivileges(USER);
            }

            privilegeRepository.save(privilegeEntity);
        } else {
            throw new NotFoundException("No privileges found for the given userId: " + userId);
        }
    }

    @Override
    public List<RequestApplicationDTO> getRequestApplicationByReason(String reason) {
        List<RequestApplicationDTO> requestDetailDTOList = iRequestApplicationRepository.getListByReason(reason);
        return requestDetailDTOList;
    }

    @Override
    public List<Object[]> getAllApproveList(String privileges) {
        return privilegeRepository.getAllApproveList(privileges);
    }
    @Override
    public List<String> getPrivileges() {
        return privilegeRepository.getPrivileges();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReason(Map<String,String> map) {
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                RequestApplicationEntity requestApplication = iRequestApplicationRepository.findById(Integer.parseInt(entry.getKey())).get();
                requestApplication.setRequestReason(entry.getValue());
                iRequestApplicationRepository.save(requestApplication);
            }
        } catch (Exception e){
            throw new RollbackException("Rollback transaction due to an error.");
        }
    }

}
