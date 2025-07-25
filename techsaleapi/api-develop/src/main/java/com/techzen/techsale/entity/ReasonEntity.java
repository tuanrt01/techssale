package com.techzen.techsale.entity;

import lombok.*;

import java.util.List;

import javax.persistence.*;


@Entity
@Table(name = "ts_reason")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReasonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "is_remove")
    private Boolean isRemove;

    @Column(name = "create_at")
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    private UserEntity createdBy;

    @Column(name = "parent_id")
    private Integer parent;

    @ManyToOne
    @JoinColumn(name = "buyer_user_id", referencedColumnName = "id")
    private UserEntity buyerUserId;

    @Transient
    private List<ReasonEntity> children;
}
