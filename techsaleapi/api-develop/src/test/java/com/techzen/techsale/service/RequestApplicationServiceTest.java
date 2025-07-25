package com.techzen.techsale.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.techzen.techsale.common.SendNotifyMattermost;
import com.techzen.techsale.dto.AttachmentDTO;
import com.techzen.techsale.dto.RejectReasonDTO;
import com.techzen.techsale.dto.RequestApplicationForListDTO;
import com.techzen.techsale.dto.RequestApplicationForListDetailDTO;
import com.techzen.techsale.dto.RequestPurchasedDTO;
import com.techzen.techsale.dto.UpdateApprovalDTO;
import com.techzen.techsale.dto.UserDTO;
import com.techzen.techsale.dto.request.InformationRequest;
import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.dto.response.RequestApplicationForListResponse;
import com.techzen.techsale.entity.AttachmentsEntity;
import com.techzen.techsale.entity.RequestApplicationEntity;
import com.techzen.techsale.entity.RequestApplicationHistoryEntity;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import com.techzen.techsale.exception.BadRequestException;
import com.techzen.techsale.exception.ConflictException;
import com.techzen.techsale.exception.NotFoundException;
import com.techzen.techsale.mapper.AttachmentMapper;
import com.techzen.techsale.mapper.RequestApplicationMapper;
import com.techzen.techsale.repository.AttachmentsRepository;
import com.techzen.techsale.repository.IRequestApplicationRepository;
import com.techzen.techsale.repository.RequestApplicationHistoryRepository;
import com.techzen.techsale.repository.RequestApplicationRepository;
import com.techzen.techsale.repository.UserRepository;
import com.techzen.techsale.security.UserPrincipal;
import com.techzen.techsale.service.impl.RequestApplicationServiceImpl;
import com.techzen.techsale.utils.SecurityUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RequestApplicationServiceTest {

    @Mock
    private IRequestApplicationRepository iRequestApplicationRepository;

    @Mock
    private RequestApplicationMapper requestApplicationMapper;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private RequestApplicationRepository requestApplicationRepository;

    @Mock
    private RequestApplicationHistoryRepository requestApplicationHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SendNotifyMattermost sendNotifyMattermost;

    @Mock
    private AttachmentsRepository attachmentsRepository;

    @Mock
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private RequestApplicationServiceImpl requestApplicationService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(requestApplicationService, "linkAdmin",
            "http://techsale.techzen.vn/admin");
        ReflectionTestUtils.setField(requestApplicationService, "linkUser",
            "http://techsale.techzen.vn");
        ReflectionTestUtils.setField(requestApplicationService, "createSuccessMsg",
            "@%s đã gửi yêu cầu mua sản phẩm **%s** \n @%s Truy cập đường dẫn sau để xét duyệt \n %s \n_________________________________________");
        ReflectionTestUtils.setField(requestApplicationService, "updateSuccessMsg",
            "@%s đã **cập nhật** yêu cầu mua sản phẩm! \n @%s Truy cập đường dẫn sau để xét duyệt \n %s \n_________________________________________");
        ReflectionTestUtils.setField(requestApplicationService, "approverAcceptMsg",
            "@%s Yêu cầu mua sản phẩm của bạn đã được duyệt \n Truy cập đường dẫn sau để xem chi tiết \n %s \n @%s Có yêu cầu mua hàng mới, truy cập đường dẫn sau để xem chi tiết \n %s \n_________________________________________");
        ReflectionTestUtils.setField(requestApplicationService, "approverRejectMsg",
            "@%s Yêu cầu mua sản phẩm của bạn đã bị từ chối vì lý do **%s** \n Truy cập đường dẫn sau để xem chi tiết \n %s \n_________________________________________");
        ReflectionTestUtils.setField(requestApplicationService, "buyerAcceptMsg",
            "@%s Yêu cầu mua sản phẩm của bạn đã được thực hiện thành công \n Truy cập đường dẫn sau để xem chi tiết \n %s \n @%s truy cập đường dẫn sau để xem chi tiết \n %s \n_________________________________________");
        ReflectionTestUtils.setField(requestApplicationService, "buyerRejectMsg",
            "@%s Yêu cầu mua sản phẩm của bạn đã bị từ chối vì lý do **%s** \n Truy cập đường dẫn sau để xem chi tiết \n %s \n @%s truy cập đường dẫn sau để xem chi tiết \n %s \n_________________________________________");
        ReflectionTestUtils.setField(requestApplicationService, "deleteMsg",
            "@%s vừa xóa sản phẩm **%s** \n @%s Truy cập đường dẫn sau để xem chi tiết \n %s");
        ReflectionTestUtils.setField(requestApplicationService, "attachmentsPath","\\img%s_%s.%s");
        ReflectionTestUtils.setField(requestApplicationService, "buyerOrderMsg","@%s đang tiến hành đặt mua sản phẩm **%s** \n @%s hãy vào đường dẫn sau để xem chi tiết %s \n @%s hãy vào đường dẫn sau để xem chi tiết %s \n_________________________________________");
    }

    @Test
    void getRequestApplicationDetail_OK() {
        RequestApplicationDetailResponse expect = mockRequestApplication();
        when(iRequestApplicationRepository.fetchRequestDetailById(anyInt())).thenReturn(of(expect));
        when(securityUtils.isAdmin()).thenReturn(false);
        when(securityUtils.isBuyer()).thenReturn(false);
        when(securityUtils.getCurrentUserInfo()).thenReturn(mockUserPrincipal());
        when(attachmentsRepository.findFirstByRequestIdAndActionInOrderByCreateAtDesc(anyInt(), anyList())).thenReturn(mockAttachment());
        when(attachmentsRepository.findFirstByRequestIdAndActionInOrderByCreateAtDesc(anyInt(), anyList())).thenReturn(mockAttachment());
        when(attachmentMapper.toAttachmentDTO(any(AttachmentsEntity.class))).thenReturn(new AttachmentDTO());
        RequestApplicationDetailResponse actual = requestApplicationService.getRequestApplicationDetail(
            1);

        assertEquals(expect, actual);
    }

    @Test
    void getRequestApplicationDetail_NotFound() {
        when(iRequestApplicationRepository.fetchRequestDetailById(anyInt())).thenReturn(empty());
        assertThrows(NotFoundException.class,
            () -> requestApplicationService.getRequestApplicationDetail(1));
    }

    private RequestApplicationDetailResponse mockRequestApplication() {
        return RequestApplicationDetailResponse.builder()
            .id(1)
            .requestUserId("1")
            .requestProductName("Request thêm màn hình cho team dev")
            .requestReason("1")
            .expectReceiveDate(LocalDateTime.now())
            .approverUserId("1")
            .buyerUserId("1")
            .createAt(LocalDateTime.now()).build();
    }

//    @Test
//    void getRequestApplicationList_OK() {
//        RequestApplicationForListResponse expect = mockRequestApplicationListResponse();
//        SearchRequest mockSearchRequest = mockSearchRequest();
//        RequestApplicationForListResponse actual = requestApplicationService.getRequestList(
//            mockSearchRequest, true);
//        actual.setTotalElements(1L);
//        actual.setTotalPages(1L);
//        actual.setSize(1);
//        actual.setPage(0);
//        assertEquals(expect, actual);
//    }

//    private RequestApplicationForListResponse mockRequestApplicationListResponse() {
//        List<RequestApplicationForListDetailDTO> requestDetailDTOList = mockRequestApplicationForListDetailDTO();
//        when(requestApplicationRepository.getRequestApplicationPage(any(SearchRequest.class),
//            any(Boolean.class))).thenReturn(requestDetailDTOList);
//        List<RequestApplicationForListDTO> content = new ArrayList<>();
//        RequestApplicationForListDTO dto = mockRequestApplicationListDTO();
//        when(requestApplicationMapper.mapToRequestDetailForList(requestDetailDTOList.get(0))).thenReturn(
//            dto);
//        content.add(dto);
//        RequestApplicationForListResponse response = new RequestApplicationForListResponse();
//        response.setContent(content);
//        response.setTotalPages(1L);
//        response.setTotalElements(1L);
//        response.setPage(0);
//        response.setSize(1);
//        return response;
//    }

    private List<RequestApplicationForListDetailDTO> mockRequestApplicationForListDetailDTO() {
        RequestApplicationForListDetailDTO detailDTO = new RequestApplicationForListDetailDTO();
        detailDTO.setProductName("Xúc xích");
        detailDTO.setId(4);
        detailDTO.setRequestReason("1");
        detailDTO.setRequestUserId("2386974331516223488");
        detailDTO.setStatus(RequestStatusEnum.WAITING);
        detailDTO.setCreatedAt(LocalDateTime.parse("2023-08-22T00:00:00"));
        detailDTO.setExpectReceiveDate(LocalDateTime.parse("2023-08-24T00:00:00"));
        List<RequestApplicationForListDetailDTO> requestDetailDTOList = new ArrayList<>();
        requestDetailDTOList.add(detailDTO);
        return requestDetailDTOList;
    }

    private SearchRequest mockSearchRequest() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setName("Lê Đức Phúc");
        searchRequest.setCreatedAtBegin(LocalDate.parse("2023-08-02"));
        searchRequest.setCreatedAtEnd(LocalDate.parse("2023-08-07"));
        searchRequest.setExpectReceiveDateBegin(LocalDate.parse("2023-08-07"));
        searchRequest.setExpectReceiveDateEnd(LocalDate.parse("2023-08-11"));
        RequestStatusEnum[] statusArr = {RequestStatusEnum.WAITING, RequestStatusEnum.REJECTED};
        List<RequestStatusEnum> statusList = Arrays.asList(statusArr);
        searchRequest.setStatusList(statusList);
        searchRequest.setRequestReason("1");
        return searchRequest;
    }

    private RequestApplicationForListDTO mockRequestApplicationListDTO() {
        UserDTO userRequestDetail = new UserDTO("2386974331516223488",
            "Lê Đức Phúc",
            "https://s3.ap-southeast-1.amazonaws.com/techzai/1689834162950-leducphuc.jpg");
        UserDTO userAssignDetail = new UserDTO(
            "2386973436921511936",
            "Trương Thị Phụng",
            "https://s3.ap-southeast-1.amazonaws.com/techzai/1689834162950-leducphuc.jpg");
        return RequestApplicationForListDTO.builder()
            .id(4)
            .status(RequestStatusEnum.WAITING)
            .expectReceiveDate("2023-08-24T00:00:00")
            .createdAt("2023-08-22T00:00:00")
            .productName("Xúc xích")
            .requestReason("BIRTHDAY")
            .userRequest(userRequestDetail)
            .userAssign(userAssignDetail)
            .build();
    }

    private void sendNotifyAfterCreated() {
        String requestUserMattermostName = "tunt";
        String approverMattermostName = "admin";

        when(userRepository.getUserMattermostName(anyString())).thenReturn(approverMattermostName);
        String createSuccessMsg = "@%s đã gửi yêu cầu mua sản phẩm **%s** \n @%s Truy cập đường dẫn sau để xét duyệt \n %s \n_________________________________________";
        String requestProductName = "Sticker";
        String linkAdmin = "http://techsale.techzen.vn/admin";
        String notifyMsg = String.format(createSuccessMsg, requestUserMattermostName, requestProductName,
            approverMattermostName, linkAdmin);

        sendNotifyMattermost.sendNotify(notifyMsg);
    }

    private void createRequestHis(RequestApplicationEntity request) {
        RequestApplicationHistoryEntity historyRequest = mockRequestApplicationHistoryEntity();
        when(requestApplicationMapper.mapToRequestHistory(request)).thenReturn(historyRequest);
        when(requestApplicationHistoryRepository.save(historyRequest)).thenReturn(new RequestApplicationHistoryEntity());
    }

    private InformationRequest mockCreatRequest() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        return InformationRequest.builder()
            .requestProductName("Sticker")
            .requestReason("1")
            .expectReceiveDate(LocalDateTime.parse("2023-08-22T12:00:00"))
            .approverUserId("2282489122897526784")
            .estimatePrice(BigDecimal.valueOf(0))
            .requestReasonDetail("Thích mua")
            .description("")
            .requestUserAttachment(mockFile)
            .build();
    }

    private RequestApplicationHistoryEntity mockRequestApplicationHistoryEntity() {
        return RequestApplicationHistoryEntity.builder()
            .id(1)
            .requestId(20)
            .requestUserId("2478100586214457344")
            .requestProductName("Sticker")
            .requestReason("1")
            .requestReasonDetail("abc")
            .expectReceiveDate(LocalDateTime.parse("2023-08-21T11:00:00"))
            .approverUserId("2282489122897526784")
            .status(RequestStatusEnum.WAITING)
            .createAt(LocalDateTime.parse("2023-08-21T10:58:47"))
            .build();
    }

    @Test
    void createBuyerRequestPurchased_Success() {
        RequestPurchasedDTO requestPurchasedDTO = mockCreateRequestPurchased();
        RequestApplicationEntity requestApplicationEntity = mockBuyerRequestComplete();
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(requestApplicationEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenReturn(requestApplicationEntity);
        assertDoesNotThrow(() -> requestApplicationService.createBuyerRequestPurchased(requestPurchasedDTO, 5));
    }

    @Test
    void createBuyerRejectReason_Success(){
        RejectReasonDTO rejectReasonDTO = mockRejectReason();
        RequestApplicationEntity requestApplicationEntity = mockBuyerRequestComplete();
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(requestApplicationEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenReturn(mockRequestAppEntity());
        when(securityUtils.getCurrentUserInfo()).thenReturn(mockUserPrincipal());
        assertDoesNotThrow(() -> requestApplicationService.buyerRejectRequest(rejectReasonDTO, 5));
    }

    @Test
    void createApproverRejectReason_Success(){
        RejectReasonDTO rejectReasonDTO = mockRejectReason();
        RequestApplicationEntity requestApplicationEntity = mockApprovalRequest();
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId("2505832772459823104");
        when(securityUtils.getCurrentUserInfo()).thenReturn(userPrincipal);
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(requestApplicationEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenReturn(mockRequestAppEntity());
        when(securityUtils.getCurrentUserInfo()).thenReturn(mockUserPrincipal());
        assertDoesNotThrow(() -> requestApplicationService.approverRejectRequest(rejectReasonDTO, 5));
    }

    private RequestApplicationEntity mockRequestAppEntity() {
        return RequestApplicationEntity.builder()
            .id(1)
            .requestProductName("Name")
            .build();
    }

    @Test
    void createApproverRejectReason_BadRequest() {
        RejectReasonDTO rejectReasonDTO = mockRejectReason();
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(null);
        assertThrows(NullPointerException.class,
            () -> requestApplicationService.approverRejectRequest(rejectReasonDTO,5));
    }

    @Test
    void updateApproverRequestStatus_Success(){
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId("2505832772459823104");
        when(securityUtils.getCurrentUserInfo()).thenReturn(userPrincipal);
        RequestApplicationEntity requestApplicationEntity = mockApprovalRequest();
        UpdateApprovalDTO updateApprovalDTO = mockUpdateApprovalDTO();
        List<UserDTO> buyerList = mockBuyerList();
        when(userRepository.findAllBuyer()).thenReturn(buyerList);
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(requestApplicationEntity));
        when(iRequestApplicationRepository.save(any(RequestApplicationEntity.class))).thenReturn(mockRequestAppEntity());
        when(securityUtils.getCurrentUserInfo()).thenReturn(mockUserPrincipal());
        assertDoesNotThrow(() -> requestApplicationService.approverAcceptRequest( updateApprovalDTO,5));
    }

    @Test
    void updateApproverRequestStatus_BadRequest() {
        UpdateApprovalDTO updateApprovalDTO = mockUpdateApprovalDTO();
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(null);
        assertThrows(NullPointerException.class,
            () -> requestApplicationService.approverAcceptRequest(updateApprovalDTO,5));
    }

    @Test
    void deleteRequest_BadRequest(){
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(null);
        assertThrows(NullPointerException.class,
            () -> requestApplicationService.deleteRequest(5));
    }

    @Test
    void deleteRequest_Success(){
        RequestApplicationEntity requestApplicationEntity = mockDeleteRequestSuccess();
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId("2505832772459823104");
        when(securityUtils.getCurrentUserInfo()).thenReturn(userPrincipal);
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(
            of(requestApplicationEntity));
        assertDoesNotThrow(
            () -> requestApplicationService.deleteRequest(5));
    }

    private List<UserDTO> mockBuyerList(){
        List<UserDTO> buyerList = new ArrayList<>();
        UserDTO buyerSelectOptionDTO = new UserDTO("2388216239160819712","Nguyễn Thanh Tú","");
        buyerList.add(buyerSelectOptionDTO);
        return buyerList;
    }

    private RejectReasonDTO mockRejectReason(){
        return RejectReasonDTO
            .builder()
            .rejectReason("Không được chấp nhận")
            .updatedAt(LocalDateTime.of(2025, Month.APRIL, 1, 10, 10))
            .build();
    }

    private UpdateApprovalDTO mockUpdateApprovalDTO(){
        return UpdateApprovalDTO
            .builder()
            .buyerUserId("2388216239160819712")
            .updateAt(LocalDateTime.of(2025, Month.APRIL, 1, 10, 10))
            .build();
    }

    private RequestApplicationEntity mockBuyerRequestComplete(){
        return RequestApplicationEntity
            .builder()
            .id(5)
            .status(RequestStatusEnum.ORDERING)
            .boughtProductName("")
            .boughtPlace("")
            .boughtAmount(0)
            .boughtPrice(BigDecimal.valueOf(0))
            .updatedAt(LocalDateTime.of(2025, Month.APRIL, 1, 10, 10))
            .build();
    }

    private RequestApplicationEntity mockApprovalRequest(){
        return RequestApplicationEntity
            .builder()
            .id(5)
            .status(RequestStatusEnum.WAITING)
            .boughtProductName("")
            .boughtPlace("")
            .boughtAmount(0)
            .boughtPrice(BigDecimal.valueOf(0))
            .updatedAt(LocalDateTime.of(2025, Month.APRIL, 1, 10, 10))
            .build();
    }

    private RequestApplicationEntity mockDeleteRequestSuccess(){
        return RequestApplicationEntity
            .builder()
            .id(5)
            .requestUserId("2505832772459823104")
            .requestProductName("Sticker")
            .requestReason("1")
            .requestReasonDetail("abc")
            .expectReceiveDate(LocalDateTime.parse("2023-08-21T11:00:00"))
            .approverUserId("2282489122897526784")
            .status(RequestStatusEnum.WAITING)
            .createAt(LocalDateTime.parse("2023-08-21T10:58:47"))
            .updatedAt(LocalDateTime.parse("2023-08-21T10:58:47"))
            .build();
    }

    private RequestPurchasedDTO mockCreateRequestPurchased(){
        return RequestPurchasedDTO
            .builder()
            .boughtProductName("Màn hình PC")
            .boughtPlace("Điện Máy Xanh")
            .boughtAmount(1)
            .boughtPrice(BigDecimal.valueOf(5000000))
            .updatedAt(LocalDateTime.of(2025, Month.APRIL, 1, 10, 10))
            .build();
    }

    private UserPrincipal mockUserPrincipal() {
        return UserPrincipal.builder()
            .id("1")
            .mattermostName("hoangnbv")
            .authorities(Collections.singletonList("NGUOI_DUNG"))
            .name("HoangNBV")
            .isEnable(true)
            .build();
    }

    private AttachmentsEntity mockAttachment() {
        return AttachmentsEntity.builder()
            .requestId(1)
            .fileName("fileName")
            .fileLength(100L)
            .build();
    }

    @Test
    void updateBuyerOrder_Ok(){
        UserPrincipal userPrincipal = new UserPrincipal();
        when(securityUtils.getCurrentUserInfo()).thenReturn(userPrincipal);
        userPrincipal.setId("2478100586214457344");

        RequestApplicationEntity entity = mockRequestApplicationEntity(userPrincipal.getId());
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(entity));

        RequestApplicationEntity savedEntity = mockSavedRequestApplicationEntity();
        when(iRequestApplicationRepository.save(entity)).thenReturn(savedEntity);
        createRequestHis(savedEntity);

        requestApplicationService.buyerOrderRequest(LocalDateTime.parse("2023-09-07T09:04:43"), 23);

        verify(iRequestApplicationRepository, times(1)).save(entity);
    }

    RequestApplicationEntity mockRequestApplicationEntity(String id){
        return RequestApplicationEntity.builder()
                .id(23)
                .status(RequestStatusEnum.PROCESSING)
                .buyerUserId(id)
                .updatedAt(LocalDateTime.parse("2023-09-07T09:04:43"))
                .isDeleted(false)
                .build();
    }

    RequestApplicationEntity mockSavedRequestApplicationEntity(){
        return RequestApplicationEntity.builder()
                .id(23)
                .requestUserId("2388215482609041408")
                .approverUserId("2386973436921511936")
                .requestProductName("Cồn lau bảng real")
                .isDeleted(false)
                .build();
    }

    @Test
    void updateBuyerOrder_NotFound(){
        assertThrows(NotFoundException.class, () -> requestApplicationService.buyerOrderRequest(LocalDateTime.parse("2023-09-01T09:04:43"), 99));
    }

    @Test
    void updateBuyerOrder_BadRequest(){
        RequestApplicationEntity entity = new RequestApplicationEntity();
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(entity));
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId("2478100586214457344");
        entity.setStatus(RequestStatusEnum.WAITING);
        assertThrows(BadRequestException.class, () -> requestApplicationService.buyerOrderRequest(LocalDateTime.parse("2023-09-07T09:04:43"), 23));
    }

    @Test
    void updateBuyerOrder_Conflict(){
        RequestApplicationEntity entity = new RequestApplicationEntity();
        when(iRequestApplicationRepository.findByIdAndIsDeletedIsFalse(anyInt())).thenReturn(of(entity));
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId("2478100586214457344");
        entity.setStatus(RequestStatusEnum.PROCESSING);
        entity.setBuyerUserId(userPrincipal.getId());
        entity.setUpdatedAt(LocalDateTime.parse("2023-09-12T09:04:43"));
        assertThrows(ConflictException.class, () -> requestApplicationService.buyerOrderRequest(LocalDateTime.parse("2023-09-07T09:04:43"), 23));
    }
}

