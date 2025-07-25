package com.techzen.techsale.entity;

import com.techzen.techsale.entity.id.RoleUserId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@IdClass(RoleUserId.class)
public class RoleUserEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "role_id")
    private int roleId;
}
