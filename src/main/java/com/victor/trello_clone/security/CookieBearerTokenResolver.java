package com.victor.trello_clone.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    @Override
    public String resolve(HttpServletRequest request) {
        var cookies = request.getCookies();

        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if("access_token".equals(cookie.getName()))
                return cookie.getValue();
        }

        return null;
    }
}
