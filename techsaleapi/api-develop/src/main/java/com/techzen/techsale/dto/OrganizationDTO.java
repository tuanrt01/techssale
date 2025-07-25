package com.techzen.techsale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.techzen.techsale.enumeration.RequestStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private Long id;
    private String organizationName;
    private Long totalEmployees;
    private Long totalMainEmployees;
    private Long totalConcurrentlyEmployees;
    private Integer employeesLimit;
    private String validityStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    private String leader;
    private Long parent;
    private Boolean addUser;
    private List<OrganizationDTO> children = new ArrayList<>();

    public OrganizationDTO(Long id, String organizationName, Long totalEmployees,
                           Long totalMainEmployees,
                           Long totalConcurrentlyEmployees,
                           Integer employeesLimit, String validityStartDate,
                           LocalDateTime createDate, LocalDateTime updateDate, String leader, Long parent, Boolean addUser) {
        this.id = id;
        this.organizationName = organizationName;
        this.totalEmployees = totalEmployees;
        this.totalMainEmployees = totalMainEmployees;
        this.totalConcurrentlyEmployees = totalConcurrentlyEmployees;
        this.employeesLimit = employeesLimit;
        this.validityStartDate = validityStartDate;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.leader = leader;
        this.parent = parent;
        this.addUser = addUser;
        this.children = new ArrayList<>();
    }
}
