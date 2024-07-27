package study.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import study.user.User;

import java.io.IOException;
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


    @DisplayName("JWT 헤더에서 alg 값을 추출")
    @Test
    public void testExtractAlg() throws IOException {
        String token = jwtUtil.generateToken(testUser);
        String[] tokenParts = token.split("\\.");
        String alg = jwtUtil.extractAlg(tokenParts[0]);
        assertEquals("HS256", alg);
    }

    @DisplayName("토큰을 이용해서 페이로드 변경, 서버에서 시그니처 검증을 안하면 해킹 성공")
    @Test
    public void test() throws IOException {
        String token = jwtUtil.generateToken(testUser);
        String[] tokenParts = token.split("\\.");
        String userEmail = jwtUtil.extractEmail(token);

        // 토큰 해킹
        String hackedHeader = tokenParts[0];
        String hackedPayload = new String(Base64.getUrlEncoder().encode(
                "{\"sub\":\"hacked@example.com\",\"name\":\"Hacked User\",\"admin\":false}".getBytes()));
        String hackedJwtSignatureAlgorithm = jwtUtil.extractAlg(hackedHeader);

        // 시크릿 키 해킹
        byte[] hackedSecretKey = Base64.getDecoder().decode(SECRET_KEY);

        // 헤더와 페이로드를 사용하여 새로운 서명을 생성합니다.
        String hackedSignature = jwtUtil.generateSignature(hackedHeader, hackedPayload, hackedJwtSignatureAlgorithm, hackedSecretKey);

        // 새로운 서명을 사용하여 해킹된 토큰을 생성합니다.
        String hackedToken = hackedHeader + "." + hackedPayload + "." + hackedSignature;

        // 원래 토큰과 해킹된 토큰이 동일하지 않음을 확인합니다.
        assertNotEquals(token, hackedToken);

        // 서명 검증을 하지 않는 서버로부터 검증 성공
        boolean isValid = jwtUtil.validateTokenWithoutSignature(hackedToken);
        assertTrue(isValid);

        // email 은 jwt 토큰의 subject
        String hackedEmail = jwtUtil.extractEmailWithoutSignature(hackedToken);
        // 해킹 성공, 페이로드 subject 변경
        assertNotEquals(userEmail, hackedEmail);
        assertEquals(userEmail, "liging12@naver.com");
        assertEquals(hackedEmail, "hacked@example.com");
    }
}
