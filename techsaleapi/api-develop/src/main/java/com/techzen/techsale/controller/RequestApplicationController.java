package com.techzen.techsale.controller;

import com.techzen.techsale.dto.RejectReasonDTO;
import com.techzen.techsale.dto.RequestPurchasedDTO;
import com.techzen.techsale.dto.UpdateApprovalDTO;
import com.techzen.techsale.dto.request.InformationRequest;
import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.dto.response.RequestApplicationForListResponse;
import com.techzen.techsale.service.RequestApplicationService;
import com.techzen.techsale.validator.FileValidator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestApplicationController extends AbstractController {

    private final RequestApplicationService requestApplicationService;

    private final FileValidator fileValidator;

    @GetMapping("/request-application/{id}")
    @PreAuthorize("hasPrivilege('read')")
    public ResponseEntity<RequestApplicationDetailResponse> getRequestApplicationDetail(
        @PathVariable int id) throws IOException {
        return ok(requestApplicationService.getRequestApplicationDetail(id));
    }

    @GetMapping("/request-application")
    @PreAuthorize("hasPrivilege('read')")
    public ResponseEntity<RequestApplicationForListResponse> getRequestList(
        @RequestParam boolean isAdmin, SearchRequest search) {
        return ok(requestApplicationService.getRequestList(search, isAdmin));
    }

    @PostMapping("/request-application")
    @PreAuthorize("hasPrivilege('create')")
    public ResponseEntity<String> createRequest(
        @Valid @ModelAttribute InformationRequest createRequest, Errors errors) throws IOException {
        validateRequest(createRequest, errors);
        requestApplicationService.createRequest(createRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/request-application/{id}/buyer-completion")
    @PreAuthorize("hasAuthority('NGUOI_MUA') || hasPrivilege('buy')")
    public ResponseEntity<String> buyerCompleteRequest(@Valid @ModelAttribute RequestPurchasedDTO requestPurchasedDTO,
                                                       @PathVariable int id, Errors errors) throws IOException {
        MultipartFile buyerAttachment = requestPurchasedDTO.getBuyerAttachment();
        if (buyerAttachment != null && StringUtils.hasText(buyerAttachment.getOriginalFilename())) {
            fileValidator.validate(buyerAttachment, errors);
        }
        requestApplicationService.createBuyerRequestPurchased(requestPurchasedDTO,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/request-application/{id}/buyer-rejection")
    @PreAuthorize("hasAuthority('NGUOI_MUA') || hasPrivilege('buy')")
    public ResponseEntity<String> buyerRejectRequest(@Valid @RequestBody RejectReasonDTO rejectReasonDTO, @PathVariable int id){
        requestApplicationService.buyerRejectRequest(rejectReasonDTO,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/request-application/{requestId}")
    @PreAuthorize("hasPrivilege('update')")
    public ResponseEntity<Void> updateRequest(
        @Valid @ModelAttribute InformationRequest updateRequest, @PathVariable int requestId,
        Errors errors) throws IOException {
        validateRequest(updateRequest, errors);
        requestApplicationService.updateRequest(updateRequest, requestId);
        return noContent();
    }

    @PutMapping("/request-application/{id}/approval")
    @PreAuthorize("hasAuthority('QUAN_LY') || hasPrivilege('approve')")
    public ResponseEntity<String> approverAcceptRequest(@Valid @RequestBody UpdateApprovalDTO updateApprovalDTO, @PathVariable int id){
        requestApplicationService.approverAcceptRequest(updateApprovalDTO,id);
         return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/request-application/{id}/rejection")
    @PreAuthorize("hasAuthority('QUAN_LY') || hasPrivilege('approve')")
    public ResponseEntity<String> approverRejectRequest(@Valid @RequestBody RejectReasonDTO rejectReasonDTO, @PathVariable int id){
        requestApplicationService.approverRejectRequest(rejectReasonDTO,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("request-application/{id}/delete")
    @PreAuthorize("hasPrivilege('delete')")
    public ResponseEntity<Void> deleteRequest(@PathVariable int id){
        requestApplicationService.deleteRequest(id);
        return noContent();
    }

    @PutMapping("/request-application/{id}/order")
    @PreAuthorize("hasAuthority('NGUOI_MUA') || hasPrivilege('buy')")
    public ResponseEntity<Void> buyerOrderRequest(@PathVariable int id,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updateAt) {
        requestApplicationService.buyerOrderRequest(updateAt, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateRequest(InformationRequest informationRequest, Errors errors) {

        MultipartFile requestUserAttachment = informationRequest.getRequestUserAttachment();
        if (requestUserAttachment != null && StringUtils.hasText(requestUserAttachment.getOriginalFilename())) {
            fileValidator.validate(requestUserAttachment, errors);
        }
    }



    @PutMapping("create-privilege/{userId}")
    public ResponseEntity<Void> createPrivilegeUserID(@PathVariable String userId,  @RequestParam String privilege) {
            requestApplicationService.createPrivilegeUserID(userId, privilege);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("delete-approve-and-buyer/{userId}")
    public ResponseEntity<Void> deleteApproveAndBuyerUserID(@PathVariable String userId) {
        requestApplicationService.deleteApproveAndBuyerUserID(userId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("delete-approve/{userId}")
    public ResponseEntity<Void> deleteApproveUserID(@PathVariable String userId) {
        requestApplicationService.deleteApproveUserID(userId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("delete-buyer/{userId}")
    public ResponseEntity<Void> deleteBuyerUserID(@PathVariable String userId) {
        requestApplicationService.deleteBuyerUserID(userId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/privilege-user-list")
    public List<Object[]> getAllApproveList(@RequestParam String privileges) {
        return requestApplicationService.getAllApproveList(privileges);
    }
    @GetMapping("/privileges-list")
    public List<String> getPrivileges() {
        return requestApplicationService.getPrivileges();
    }

}
