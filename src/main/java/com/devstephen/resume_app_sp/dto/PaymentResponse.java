package com.devstephen.resume_app_sp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
  private String userId;
  private String authorizationUrl;
  private String paystackAccessCode;
  private String receipt;
  private Integer amount;
  private String currency;
  private String planType;
  private String status;

  @CreatedDate
  private LocalDateTime createdAt;

  private String paystackOrderId;
}
