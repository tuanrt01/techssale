package com.techzen.techsale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestApplicationForListDTO {
    private int id;

    private RequestStatusEnum status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String expectReceiveDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String orderingDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String deliveryDate;

    private String requestReason;

    private String productName;

    private UserDTO userRequest;

    private UserDTO userAssign;

    private UserDTO buyer;
}
