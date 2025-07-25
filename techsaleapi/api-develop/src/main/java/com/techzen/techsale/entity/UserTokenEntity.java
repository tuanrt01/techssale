package com.techzen.techsale.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_token")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserTokenEntity {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "expires")
    private int expires;

    @Column(name = "request_from", length = 20)
    private String requestFrom;

    @Column(name = "del_flag")
    private boolean deleted;
}
