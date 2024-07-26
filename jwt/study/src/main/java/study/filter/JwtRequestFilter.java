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
        final String authorizationHeader = request.getHeader("Authorization");

        logger.info("Authorization Header: {}", authorizationHeader);

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // "Bearaer " 이후 jwt 토큰
            email = jwtUtil.extractEmail(jwt);
            logger.info("JWT: {}", jwt);
            logger.info("Email: {}", email);
        }

        if (email != null && request.getAttribute("user") == null) {
            User user = userService.loadUserByEmail(email);
            if (jwtUtil.validateToken(jwt, user)) {
                request.setAttribute("user", user);
                logger.info("User authenticated: {}", user.getEmail());
            }
        }
        filterChain.doFilter(request, response);
    }
}
