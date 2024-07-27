package study.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import study.jwt.JwtUtil;
import study.user.User;
import study.user.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String email = extractEmailFromRequest(request);
        String jwt = extractJwtFromRequest(request);

        if (email != null && request.getAttribute("user") == null) {
            authenticateUser(request, email, jwt);
        } else {
            throw  new RuntimeException("bad request");
        }
        filterChain.doFilter(request, response);
    }

    private String extractEmailFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        logger.info("Authorization Header: {}", authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7); // "Bearer " 이후 jwt 토큰
            String email = jwtUtil.extractEmail(jwt);
            logger.info("JWT: {}", jwt);
            logger.info("Email: {}", email);
            return email;
        } else {
            throw new RuntimeException("Authorization header is invalid");
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer " 이후 jwt 토큰
        } else {
            throw new RuntimeException("Authorization header is invalid");
        }
    }

    private void authenticateUser(HttpServletRequest request, String email, String jwt) {
        User user = userService.loadUserByEmail(email);
        if (jwtUtil.validateToken(jwt, user)) {
            request.setAttribute("user", user);
            logger.info("User authenticated: {}", user.getEmail());
        } else {
            throw new RuntimeException("User authenticated");
        }
    }
}
