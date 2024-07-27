package study.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import study.user.User;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static study.jwt.JwtUtil.SECRET_KEY;

@SpringBootTest
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        testUser = new User("liging12@naver.com", "minseo12");
    }

    @DisplayName("JWT 토큰 생성")
    @Test
    public void testGenerateToken() {
        String token = jwtUtil.generateToken(testUser);
        assertNotNull(token);
        System.out.println("Generated Token: " + token);
    }

    @DisplayName("JWT 토큰에서 이메일 추출")
    @Test
    public void testExtractEmail() {
        String token = jwtUtil.generateToken(testUser);
        String email = jwtUtil.extractEmail(token);
        assertEquals(testUser.getEmail(), email);
    }

    @DisplayName("JWT 토큰 유효성 검증 -> 서명 검증 및 Subject 검증 및 유효기간 검증")
    @Test
    public void testValidateToken() {
        String token = jwtUtil.generateToken(testUser);
        boolean isValid = jwtUtil.validateToken(token, testUser);
        assertTrue(isValid);
    }

    @DisplayName("JWT 토큰 유효기간 검증")
    @Test
    public void testIsTokenExpired() {
        String token = jwtUtil.generateToken(testUser);
        boolean isExpired = jwtUtil.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @DisplayName("헤더와 페이로드가 다른 경우 서명 실패 -> SignatureException 예외 발생")
    @Test
    public void testTamperedToken() {
        String token = jwtUtil.generateToken(testUser);
        String[] tokenParts = token.split("\\.");
        String tamperedPayload = tokenParts[1].replace('a', 'b'); // Simple tampering example
        String tamperedToken = tokenParts[0] + "." + tamperedPayload + "." + tokenParts[2];

        assertThrows(io.jsonwebtoken.SignatureException.class, () -> {
            jwtUtil.extractEmail(tamperedToken);
        });
    }

    @DisplayName("토큰 유효기간 지났을 경우 ExpiredJwtException 발생")
    @Test
    public void testExpiredToken() {
        // Expired token generation for testing
        String expiredToken = Jwts.builder()
                .setSubject(testUser.getEmail())
                .claim("name", "minseojo")
                .claim("admin", true)
                .setIssuer("api.auth.myApp.com")
                .setAudience("myApp.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10)) // 10 hour ago
                .signWith(SignatureAlgorithm.HS256, Base64.getDecoder().decode(SECRET_KEY))
                .compact();

        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.extractEmail(expiredToken);
        });
    }
}
