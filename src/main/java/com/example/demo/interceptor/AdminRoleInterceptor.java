package com.example.demo.interceptor;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.Authentication;
import com.example.demo.entity.Role;
import com.example.demo.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws UnauthorizedException {

        // 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "연결된 세션이 없습니다.");
        }

        // 인증 정보 확인
        Authentication authentication = (Authentication) session.getAttribute(GlobalConstants.USER_AUTH);
        if (authentication == null) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        // 권한 확인
        Role role = authentication.getRole();
        if (role != Role.ADMIN) {
            throw new UnauthorizedException(HttpStatus.FORBIDDEN, "ADMIN 권한이 필요합니다.");
        }

        // 결과 반환
        return true;
    }
}