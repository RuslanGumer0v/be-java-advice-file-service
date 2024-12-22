package com.itm.advice.fileservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.isAuthenticated();
    }

    public Collection<? extends GrantedAuthority> getCurrentUserRoles() {

        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public String getCurrentUserRolesAsString() {
        Collection<? extends GrantedAuthority> authorities = getCurrentUserRoles();

        if (authorities != null && !authorities.isEmpty()) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }

        return null;
    }

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        String userId = authentication.getName();
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный UUID для пользователя: " + userId, e);
        }
    }

}
