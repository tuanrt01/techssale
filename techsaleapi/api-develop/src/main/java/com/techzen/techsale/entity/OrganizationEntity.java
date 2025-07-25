package com.techzen.techsale.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrganizationEntity {

	@Id
	private Long id;
	
	@Column(name = "organization_name")
	private String organizationName;
	
	@Column(name = "del_flag", insertable = false)
	private int delFlag;

	@Column(name = "total_user")
	private int totalEmployees;

	@Column(name = "total_capacity")
	private int employeesLimit;

	@Column(name = "validity_start_date")
	private String validityStartDate;

	@Column(name = "create_by", insertable = false)
	private LocalDateTime createDate;

	@Column(name = "update_date", insertable = false)
	private LocalDateTime updateDate;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "leader_id", referencedColumnName = "id")
	@JsonIgnore
	private UserEntity leader;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "approver_id", referencedColumnName = "id")
	@JsonIgnore
	private UserEntity approver;

	@Column(name = "is_partner")
	private boolean isPartner;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private OrganizationEntity parent;

	@Column(name = "add_user")
	private Boolean addUser;
}
