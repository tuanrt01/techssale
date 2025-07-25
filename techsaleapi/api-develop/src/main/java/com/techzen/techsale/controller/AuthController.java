package com.techzen.techsale.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.techzen.techsale.dto.request.LoginRequest;
import com.techzen.techsale.entity.UserTokenEntity;
import com.techzen.techsale.exception.InternalServerErrorException;
import com.techzen.techsale.repository.UserTokenRepository;
import com.techzen.techsale.security.UserPrincipal;
import com.techzen.techsale.security.jwt.JwtTokenProvider;
import com.techzen.techsale.service.impl.FileService;
import com.techzen.techsale.utils.SecurityUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserTokenRepository userTokenRepository;
    private final SecurityUtils securityUtils;
    private final FileService fileService;
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            boolean isRemoved = userTokenRepository.isUserRemoved(loginRequest.getUsername());
            if (isRemoved) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "This account is removed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication != null) {
                String jwt = jwtTokenProvider.generateToken(authentication);
                UserPrincipal userInfo = securityUtils.getCurrentUserInfo();
                byte[] file = fileService.getFile(userInfo.getAvtUrl());
                UserTokenEntity userToken = new UserTokenEntity();
                userToken.setToken(jwt);
                userToken.setUserId(userInfo.getId());
//            userToken.setExpires(jwtTokenProvider.getExpiresFromToken(jwt));
                userToken.setRequestFrom(getClientIp(request));
                userTokenRepository.save(userToken);
                Map<String, Object> response = new HashMap<>();
                response.put("jwt", jwt);
                if (file != null) {
                    response.put("file", Base64.getEncoder().encodeToString(file));
                } else {
                    response.put("file", null);
                }
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException("Login error!");
        }

        return null;
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.getJwtFromRequest(request);
        Optional<UserTokenEntity> userToken = userTokenRepository.findFirstByToken(token);
        userToken.ifPresent(userTokenRepository::delete);

        return ResponseEntity.noContent().build();
    }

    private static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (StringUtils.hasText(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
