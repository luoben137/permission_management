package com.permission.aspect;

import com.permission.entity.User;
import com.permission.security.CustomUserDetails;
import com.permission.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @AfterReturning(pointcut = "execution(* com.permission.service.UserService.create(..)) || " +
                               "execution(* com.permission.service.UserService.update(..)) || " +
                               "execution(* com.permission.service.UserService.delete(..)) || " +
                               "execution(* com.permission.service.RoleService.create(..)) || " +
                               "execution(* com.permission.service.RoleService.update(..)) || " +
                               "execution(* com.permission.service.RoleService.delete(..)) || " +
                               "execution(* com.permission.service.PermissionService.create(..)) || " +
                               "execution(* com.permission.service.PermissionService.update(..)) || " +
                               "execution(* com.permission.service.PermissionService.delete(..))",
                   returning = "result")
    public void logServiceOperation(JoinPoint joinPoint, Object result) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return;
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String action = getAction(methodName);
            String resourceType = getResourceType(className);

            Long resourceId = null;
            if (result != null) {
                try {
                    resourceId = (Long) result.getClass().getMethod("getId").invoke(result);
                } catch (Exception e) {
                    // Ignore
                }
            }

            String details = String.format("%s.%s", className, methodName);
            if (joinPoint.getArgs().length > 0) {
                details += " - Args: " + Objects.toString(joinPoint.getArgs()[0]);
            }

            String ip = getClientIp();

            auditLogService.log(
                user.getId(),
                user.getUsername(),
                action,
                resourceType,
                resourceId,
                details,
                ip
            );
        } catch (Exception e) {
            // Log error but don't break the main flow
            e.printStackTrace();
        }
    }

    private String getAction(String methodName) {
        if (methodName.startsWith("create")) {
            return "CREATE";
        } else if (methodName.startsWith("update")) {
            return "UPDATE";
        } else if (methodName.startsWith("delete")) {
            return "DELETE";
        }
        return "UNKNOWN";
    }

    private String getResourceType(String className) {
        if (className.contains("User")) {
            return "USER";
        } else if (className.contains("Role")) {
            return "ROLE";
        } else if (className.contains("Permission")) {
            return "PERMISSION";
        }
        return "UNKNOWN";
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // Ignore
        }
        return "unknown";
    }
}

