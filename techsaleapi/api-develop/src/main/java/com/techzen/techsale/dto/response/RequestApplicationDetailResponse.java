package com.techzen.techsale.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techzen.techsale.dto.AttachmentDTO;
import com.techzen.techsale.enumeration.RequestStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestApplicationDetailResponse {

    private int id;
    private String requestUserId;
    private String requestUsername;
    private String requestProductName;
    private Integer amount;
    private BigDecimal estimatePrice;
    private String requestReason;
    private String requestReasonDetail;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectReceiveDate;
    private String description;
    private String approverUserId;
    private String approverUsername;
    private String buyerUserId;
    private String buyerUsername;
    private String approverRejectReason;
    private String buyerRejectReason;
    private String boughtProductName;
    private BigDecimal boughtPrice;
    private String boughtPlace;
    private Integer boughtAmount;
    private RequestStatusEnum status;
    private AttachmentDTO userAttachment;
    private AttachmentDTO buyerAttachment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private String productLink;
    private String personnelUsedId;
    private String personnelUsedName;
    private Long organizationUsedId;
    private String organizationUsedName;
    private RequestStatusEnum statusReject;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderingDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryDate;


    public RequestApplicationDetailResponse(int id, String requestUserId,
        String requestUsername, String requestProductName, Integer amount,
        BigDecimal estimatePrice, String requestReason, String requestReasonDetail,
        LocalDateTime expectReceiveDate, String description, String approverUserId,
        String approverUsername, String buyerUserId, String buyerUsername,
        String approverRejectReason, String buyerRejectReason, String boughtProductName,
        BigDecimal boughtPrice, String boughtPlace, Integer boughtAmount,
        RequestStatusEnum status, LocalDateTime createAt, LocalDateTime approvedAt,
        LocalDateTime updatedAt, String productLink, String personnelUsedId, String personnelUsedName, 
        Long organizationUsedId, String organizationUsedName, RequestStatusEnum statusReject,
        LocalDateTime orderingDate, LocalDateTime deliveryDate) {
        
        this.id = id;
        this.requestUserId = requestUserId;
        this.requestUsername = requestUsername;
        this.requestProductName = requestProductName;
        this.amount = amount;
        this.estimatePrice = estimatePrice;
        this.requestReason = requestReason;
        this.requestReasonDetail = requestReasonDetail;
        this.expectReceiveDate = expectReceiveDate;
        this.description = description;
        this.approverUserId = approverUserId;
        this.approverUsername = approverUsername;
        this.buyerUserId = buyerUserId;
        this.buyerUsername = buyerUsername;
        this.approverRejectReason = approverRejectReason;
        this.buyerRejectReason = buyerRejectReason;
        this.boughtProductName = boughtProductName;
        this.boughtPrice = boughtPrice;
        this.boughtPlace = boughtPlace;
        this.boughtAmount = boughtAmount;
        this.status = status;
        this.createAt = createAt;
        this.approvedAt = approvedAt;
        this.updatedAt = updatedAt;
        this.productLink = productLink;
        this.personnelUsedId = personnelUsedId;
        this.personnelUsedName = personnelUsedName;
        this.organizationUsedId = organizationUsedId;
        this.organizationUsedName = organizationUsedName;
        this.statusReject = statusReject;
        this.orderingDate = orderingDate;
        this.deliveryDate = deliveryDate;
    }
}
