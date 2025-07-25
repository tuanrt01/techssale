package com.techzen.techsale.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectReasonDTO {
   @NotBlank(message = "Not be empty")
   private String rejectReason;

   private LocalDateTime updatedAt;
}
