package com.techzen.techsale.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationRequest {

    @NotBlank
    private String approverUserId;

    private String requestReason;


    private String requestReasonDetail;

    @NotBlank
    private String requestProductName;

    @NotBlank
    private String productLink;

    private String personnelUsed;

    private Long organizationUsed;

    @NotNull
    @FutureOrPresent
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime expectReceiveDate;

    @Positive
    @Max(value = 9999)
    @NotNull
    private Integer requestAmount;

    @PositiveOrZero
    @Max(value = 100000000)
    private BigDecimal estimatePrice;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    private MultipartFile requestUserAttachment;
}
