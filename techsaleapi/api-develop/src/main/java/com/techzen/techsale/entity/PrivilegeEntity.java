package com.techzen.techsale.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "privilege")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "business_function")
    private String businessFunction;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "privileges")
    private String privileges;
}
