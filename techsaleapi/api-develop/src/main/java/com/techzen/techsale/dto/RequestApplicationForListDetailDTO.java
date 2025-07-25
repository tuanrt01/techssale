package com.techzen.techsale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestApplicationForListDetailDTO {
    int id;

    String requestReason;

    RequestStatusEnum status;

    String productName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime expectReceiveDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    String requestUserId;

    String approverId;

    String userRequestedName;

    String userRequestAvatar;

    String approverAvatar;

    String approverName;

    String buyerId;

    String buyerName;

    String buyerAvatar;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime orderingDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime deliveryDate;
}
