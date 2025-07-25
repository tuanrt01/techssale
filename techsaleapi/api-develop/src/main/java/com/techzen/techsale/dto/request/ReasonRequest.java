package com.techzen.techsale.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReasonRequest {
    private Integer id;
    private String reason;
    private Integer parent;
    private String mainBuyer;
    private List<String> buyer;
} 