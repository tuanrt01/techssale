package com.techzen.techsale.config;

import com.techzen.techsale.utils.SecurityUtils;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final SecurityUtils securityUtils;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(securityUtils.getCurrentUserInfo().getMattermostName());
    }
}
