package com.techzen.techsale.entity;

import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "image_url")
    private String avatarUrl;

    @Column(name = "mattermost_name")
    private String mattermostName;

    @Column(name = "del_flag")
    private boolean deleted;

    @Column(name = "create_date")
    private Date createdAt;

    @Column(name = "update_date")
    private Date updatedAt;

    @Column(name = "is_remove")
    private Boolean isRemove;

    @Column(name = "is_guest")
    private Boolean isGuest;

    @ManyToOne
    @JoinColumn(name = "personnel_type")
    @JsonManagedReference
    private PersonnelTypeEntity personnelType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    private OrganizationEntity organization;
}
