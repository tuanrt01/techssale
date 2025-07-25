package com.techzen.techsale.utils;

import com.techzen.techsale.enumeration.RoleEnum;
import com.techzen.techsale.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityUtils {

    public UserPrincipal getCurrentUserInfo() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isAdmin() {
        boolean hasPrivilegeApprove = getCurrentUserInfo().getPrivileges().stream().anyMatch("approve"::equals);
        boolean hasRoleAdmin = getCurrentUserInfo().getAuthorities().stream().anyMatch(x -> RoleEnum.MANAGER.getCode().equals(x.getAuthority()));
        return hasRoleAdmin || hasPrivilegeApprove;
    }

    public boolean isBuyer() {
        boolean hasPrivilegeBuy = getCurrentUserInfo().getPrivileges().stream().anyMatch("buy"::equals);
        boolean hasRoleBuyer = getCurrentUserInfo().getAuthorities().stream().anyMatch(x -> RoleEnum.BUYER.getCode().equals(x.getAuthority()));
        return hasRoleBuyer || hasPrivilegeBuy;
    }
}
