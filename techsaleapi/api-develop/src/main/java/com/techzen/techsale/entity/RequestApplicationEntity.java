package com.techzen.techsale.entity;

import com.techzen.techsale.enumeration.RequestStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ts_request_application")
public class RequestApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "request_user_id", length = 64)
    private String requestUserId;

    @Column(name = "request_product_name", length = 200)
    private String requestProductName;

    @Column(name = "product_link", length = 500)
    private String productLink;

    @Column(name = "request_amount")
    private Integer requestAmount;

    @Column(name = "estimate_price", length = 19, precision = 2)
    private BigDecimal estimatePrice;

    @Column(name = "reason_request", length = 64)
    private String requestReason;

    @Column(name = "request_reason_detail", columnDefinition = "TEXT")
    private String requestReasonDetail;

    @Column(name = "expect_receive_date")
    private LocalDateTime expectReceiveDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "approver_user_id", length = 64)
    private String approverUserId;

    @Column(name = "buyer_user_id", length = 64)
    private String buyerUserId;

    @Column(name = "approver_reject_reason", columnDefinition = "TEXT")
    private String approverRejectReason;

    @Column(name = "buyer_reject_reason", columnDefinition = "TEXT")
    private String buyerRejectReason;

    @Column(name = "bought_product_name", length = 200)
    private String boughtProductName;

    @Column(name = "bought_price", length = 19, precision = 2)
    private BigDecimal boughtPrice;

    @Column(name = "bought_place", columnDefinition = "TEXT")
    private String boughtPlace;

    @Column(name = "bought_amount")
    private Integer boughtAmount;

    @Generated(GenerationTime.INSERT)
    @Column(name = "is_deleted", insertable = false)
    private Boolean isDeleted;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatusEnum status;

    @Column(name = "status_reject")
    @Enumerated(EnumType.STRING)
    private RequestStatusEnum statusReject;

    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "personnel_used", length = 64)
    private String personnelUsed;

    @Column(name = "organization_used")
    private Long organizationUsed;
    
    @Column(name = "ordering_date")
    private LocalDateTime orderingDate;
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
}
