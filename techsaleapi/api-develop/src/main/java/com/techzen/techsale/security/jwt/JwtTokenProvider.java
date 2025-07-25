package com.techzen.techsale.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techzen.techsale.repository.UserTokenRepository;
import com.techzen.techsale.security.UserPrincipal;
import com.techzen.techsale.service.impl.FileService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class JwtTokenProvider {
    private final FileService fileService;
    @Value("${techsale.jwt-secret:offshore_api_jwt_key-199@$!!}")
    private String jwtSecret;

//    @Value("${techsale.jwtExpiration:1800000}")
//    private int jwtExpiration;

    private final UserTokenRepository userTokenRepository;

    public String generateToken(Authentication authentication) {
        log.info("Start generate token with authentication: [{}]", authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> claims = mapper.convertValue(userPrincipal, Map.class);
        claims.replace("authorities", userPrincipal.getAuthorities().stream().map(
            GrantedAuthority::getAuthority).collect(Collectors.toList()));

        return Jwts.builder()
            .setSubject(userPrincipal.getEmail())
            .setClaims(claims)
            .setIssuedAt(new Date())
//            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        return (String) claims.get("email");
    }

    public Integer getExpiresFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        return (Integer) claims.get("exp");
    }

    public boolean isValidToken(String authToken) throws SignatureException {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return userTokenRepository.existsByTokenAndDeletedIsFalse(authToken);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        log.info("Start get jwt from request!");
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
