package study.user.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import study.user.User;
import study.user.exception.UnauthorizedException;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    @Before("@annotation(study.user.annotation.Authenticated)")
    public void authenticateUser() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
    }
}
