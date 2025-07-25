package com.techzen.techsale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {

    private String id;
    private String name;
    private String mattermostName;
    private String email;
    private String avatarUrl;
    private String password;
    private String roleCode;
    private String roleName;
    private String privileges;
    private boolean isDeleted;
}
