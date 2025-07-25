package com.techzen.techsale.dto;

import com.techzen.techsale.entity.ReasonEntity;
import com.techzen.techsale.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReasonDTO {
    private Integer id;
    private String reason;
    private Integer parent;
    private String mainBuyer;
    private Boolean isRemove;
    private String createdAt;
    private UserEntity createdBy;
    private List<ReasonDTO> children;
} 