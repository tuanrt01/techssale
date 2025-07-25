package com.techzen.techsale.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPurchasedDTO {
    @NotBlank(message = "Not be empty")
    private String boughtProductName;

    @NotBlank(message = "Not be empty")
    private String boughtPlace;

    @Positive(message = "Not be empty and Amount must be positive")
    @Max(value = 1000000, message = "Amount is not more than 1 million")
    private int boughtAmount;

    @Positive(message = "Not be empty and Price must be positive")
    @Max(value = 1000000000, message = "Price is not more than 1 billion")
    private BigDecimal boughtPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    private MultipartFile buyerAttachment;
}
