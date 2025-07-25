package com.techzen.techsale.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "code_name", length = 20)
    private String codeName;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "create_by", length = 64)
    private String createBy;

    @Column(name = "del_flag")
    private int delFlag;

    @OneToOne(optional = false)
    @JoinColumn(name = "app_id", referencedColumnName = "id")
    private AppEntity app;
}
