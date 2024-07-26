package study.jwt;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import study.user.User;

import java.util.Base64;
import java.util.Date;

/**
 * 토큰 생성(generateToken): 사용자의 이메일을 주제로 하여 10시간 동안 유효한 JWT를 생성합니다.
 * 이메일 추출(extractEmail): JWT에서 사용자의 이메일을 추출합니다.
 * 토큰 검증(validateToken): JWT가 유효한지, 즉 사용자의 이메일과 일치하며 만료되지 않았는지 확인합니다.
 * 토큰 만료 확인(isTokenExpired): JWT의 만료 시간을 확인하여 현재 시간과 비교합니다
 */

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "mySecretKey";
    private static final byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);

    // 로그인한 사용자의 토큰을 생성한다.
    // signWith 메서드와 함께 사용되어 특정 알고리즘(예: HS256)과 비밀 키를 기반으로 서명을 생성합니다.
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // 토큰의 주제(Subject)를 사용자의 이메일로 설정
                .claim("name", "minseojo")  // "name" 클레임 설정
                .claim("admin", true)  // "admin" 클레임 설정
                .setIssuer("api.auth.myApp.com") // 토큰 발급자(Issuer)를 설정 (서버)
                .setAudience("myApp.com") // 토큰의 대상자(Audience)를 설정 (클라이언트)
                .setIssuedAt(new Date()) // 토큰이 발행된 시간을 현재 시간으로 설정
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 토큰의 만료 시간을 현재 시간으로부터 10시간 후로 설정
                .signWith(SignatureAlgorithm.HS256, keyBytes) // 서명 알고리즘을 HS256으로 설정하고, 비밀 키를 사용하여 서명
                .compact(); // 토큰을 생성하여 문자열로 변환
    }

    //  입력받은 JWT에서 이메일을 추출합니다.
    public String extractEmail(String token) {
        return Jwts.parser() // parser 메서드를 통해 JWT를 파싱하고, 비밀 키를 설정합니다.
                .setSigningKey(keyBytes)  // 비밀 키를 설정하여 서명을 검증
                .parseClaimsJws(token) // JWT를 파싱하고 서명을 검증
                .getBody() // JWT의 본문(Claims)을 가져옴
                .getSubject(); // 주제->이메일을 추출하여 반환
    }

    // 입력받은 JWT가 유효한지 검사합니다
    public boolean validateToken(String token, User user) {
        final String email = extractEmail(token); // JWT에서 추출한 이메일과 입력받은 사용자의 이메일을 비교합니다
        return (email.equals(user.getEmail()) && !isTokenExpired(token)); // 이메일이 일치하고 토큰이 만료되지 않았다면 유효성 검증 성공
    }

    // 입력받은 JWT가 만료되었는지 확인합니다.
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(keyBytes)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration() // JWT에서 만료 시간을 추출하여 현재 시간과 비교합니다.
                .before(new Date()); // 만료 시간이 현재 시간보다 이전이면 검증 성공
    }
}

//JWT의 구조
//JWT는 세 부분으로 구성됩니다:
//
//헤더(Header): 토큰의 타입과 서명 알고리즘 정보를 포함합니다.
//페이로드(Payload): 클레임(claims)이라고 불리는 사용자에 대한 정보가 포함됩니다.
//서명(Signature): 헤더와 페이로드를 비밀 키를 사용하여 암호화한 것입니다.

//setSigningKey 메서드의 역할
//JWT 생성 시: JWT를 생성할 때 서명을 만드는데 사용됩니다. signWith 메서드와 함께 사용되어 특정 알고리즘(예: HS256)과 비밀 키를 기반으로 서명을 생성합니다.
//JWT 검증 시: JWT를 검증할 때 사용됩니다. JWT의 서명을 확인하여 토큰이 변경되지 않았음을 검증합니다. 이는 비밀 키가 동일할 때만 검증이 성공하도록 합니다.