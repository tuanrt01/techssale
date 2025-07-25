package com.techzen.techsale.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum RoleEnum {
    USER("NGUOI_DUNG"),
    BUYER("NGUOI_MUA"),
    MANAGER("QUAN_LY");
    String code;
}
