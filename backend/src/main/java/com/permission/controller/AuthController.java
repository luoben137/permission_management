package com.permission.controller;

import com.permission.dto.ApiResponse;
import com.permission.dto.LoginRequest;
import com.permission.dto.UserInfo;
import com.permission.entity.Permission;
import com.permission.entity.Role;
import com.permission.entity.User;
import com.permission.security.CustomUserDetails;
import com.permission.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    private AuditLogService auditLogService;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserInfo>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpRequest.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setEnabled(user.getEnabled());
            userInfo.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
            userInfo.setPermissions(user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getCode)
                    .collect(Collectors.toSet()));

            // Log login
            String ip = getClientIp(httpRequest);
            auditLogService.log(user.getId(), user.getUsername(), "LOGIN", "AUTH", null, "用户登录", ip);

            return ResponseEntity.ok(ApiResponse.success("登录成功", userInfo));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("用户名或密码错误"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            String ip = getClientIp(request);
            auditLogService.log(user.getId(), user.getUsername(), "LOGOUT", "AUTH", null, "用户登出", ip);
        }
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<UserInfo>> check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.ok(ApiResponse.error("未登录"));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEnabled(user.getEnabled());
        userInfo.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        userInfo.setPermissions(user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet()));

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

