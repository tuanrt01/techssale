package com.techzen.techsale.service;

import com.techzen.techsale.dto.RejectReasonDTO;
import com.techzen.techsale.dto.RequestApplicationDTO;
import com.techzen.techsale.dto.RequestPurchasedDTO;
import com.techzen.techsale.dto.UpdateApprovalDTO;
import com.techzen.techsale.dto.request.InformationRequest;
import com.techzen.techsale.dto.request.SearchRequest;
import com.techzen.techsale.dto.response.RequestApplicationDetailResponse;
import com.techzen.techsale.dto.response.RequestApplicationForListResponse;
import com.techzen.techsale.entity.RequestApplicationEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RequestApplicationService {

    RequestApplicationDetailResponse getRequestApplicationDetail(int id) throws IOException;

    RequestApplicationForListResponse getRequestList(SearchRequest searchRequest, boolean isAdmin);

    void createRequest(InformationRequest informationRequest) throws IOException;
    
    void createBuyerRequestPurchased(RequestPurchasedDTO requestPurchasedDTO, int id)
        throws IOException;

    void buyerRejectRequest(RejectReasonDTO rejectReasonDTO, int id);

    void updateRequest(InformationRequest updateRequest, int requestId) throws IOException;

    void approverAcceptRequest(UpdateApprovalDTO updateApprovalDTO, int id);

    void approverRejectRequest(RejectReasonDTO rejectReasonDTO, int id);

    void deleteRequest(int id);

    void buyerOrderRequest(LocalDateTime updatedAt, int id);

    void createPrivilegeUserID( String userId, String privilege);
    List<Object[]> getAllApproveList(String privileges);
    List<String> getPrivileges();
    void deleteApproveAndBuyerUserID(String userId);
    void deleteApproveUserID(String userId);
    void deleteBuyerUserID(String userId);

    List<RequestApplicationDTO> getRequestApplicationByReason(String reason);
    void updateReason(Map<String,String> map);
}
