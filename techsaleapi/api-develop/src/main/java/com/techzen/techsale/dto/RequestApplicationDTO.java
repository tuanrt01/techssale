package com.techzen.techsale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestApplicationDTO {
    int id;
    String requestReason;
    String productName;
    String createBy;
}
