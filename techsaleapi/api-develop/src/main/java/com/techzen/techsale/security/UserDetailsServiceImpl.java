package com.techzen.techsale.security;

import com.techzen.techsale.dto.UserDetailDTO;
import com.techzen.techsale.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${techsale.business-function:TECHSALE_FUNCTION}")
    private String businessFunction;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername: {} and app id = {}", email, 2);
        Optional<UserDetailDTO> userEntity = userRepository.getUserInfo(email, businessFunction);
        if (!userEntity.isPresent()) {
            throw new UsernameNotFoundException(String.format("Not found user: %s", email));
        }
        UserDetailDTO user = userEntity.get();
        return UserPrincipal.builder()
            .id(user.getId())
            .name(user.getName())
            .mattermostName(user.getMattermostName())
            .email(user.getEmail())
            .avtUrl(user.getAvatarUrl())
            .isEnable(!user.isDeleted())
            .password(user.getPassword())
            .authorities(Collections.singletonList(user.getRoleCode()))
            .privileges(Arrays.stream(user.getPrivileges().split(",")).map(String::trim).distinct().collect(Collectors.toList()))
            .build();
    }
}
