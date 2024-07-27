# JWT

## JWT의 구조와 역할

JWT(JSON Web Token)는 세 가지 부분으로 구성된 토큰입니다: 헤더(Header), 페이로드(Payload), 그리고 서명(Signature). JWT는 사용자 인증 및 정보 교환을 위한 안전한 방법을 제공합니다.

### 1. 헤더(Header)

헤더는 JWT의 메타데이터를 포함하는 부분으로, 주로 토큰의 타입과 서명 알고리즘 정보를 담고 있습니다. 헤더는 JSON 객체로 표현되며, 이를 Base64Url로 인코딩하여 JWT의 첫 번째 부분을 구성합니다.
- <a href="https://github.com/minseojo/spring/issues/13">base64Url 이란?</a>

#### 헤더 예시
```json
{
  "typ": "JWT",
  "alg": "HS256"
}
```

- `typ`: 토큰의 타입을 나타냅니다. JWT의 경우 "JWT"로 설정됩니다.
- `alg`: 서명에 사용된 해싱 알고리즘을 나타냅니다. 예를 들어, HMAC SHA256을 사용할 경우 "HS256"이 됩니다.

헤더는 매우 간단합니다. 토큰이 어떤 알고리즘을 사용해서 서명되었는지, 그리고 이 토큰이 JWT라는 것을 나타내기 위해 최소한의 정보를 담고 있습니다.

### 2. 페이로드(Payload)

페이로드는 클레임(claims)이라고 불리는 사용자 정보나 기타 데이터를 포함하는 부분입니다. 클레임은 JWT의 두 번째 부분으로, 사용자의 식별 정보나 추가 데이터를 포함합니다. 페이로드도 JSON 객체로 표현되며, 이를 Base64Url로 인코딩하여 JWT의 두 번째 부분을 구성합니다.

#### 클레임의 종류

- **등록된 클레임(Registered Claims)**: JWT 표준에 정의된 클레임으로, 특정한 의미와 목적을 가집니다.
    - `iss` (issuer): 토큰 발급자
    - `sub` (subject): 토큰 주제(일반적으로 사용자 ID)
    - `aud` (audience): 토큰의 대상
    - `exp` (expiration): 토큰의 만료 시간
    - `nbf` (not before): 토큰이 유효하지 않은 시간
    - `iat` (issued at): 토큰이 발급된 시간
    - `jti` (JWT ID): JWT의 고유 식별자

- **공개 클레임(Public Claims)**: 사용자가 정의할 수 있는 클레임으로, 중복되지 않도록 주의해야 합니다.

- **비공개 클레임(Private Claims)**: 발급자와 수신자 간에 비공개로 사용되는 클레임입니다.

#### 페이로드 예시
```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true,
  "iat": 1516239022
}
```

- `sub`: 주체(Subject)로서 사용자 ID를 나타냅니다.
- `name`: 사용자의 이름을 나타냅니다.
- `admin`: 사용자가 관리자 권한을 가지고 있는지 여부를 나타냅니다.
- `iat`: 토큰이 발급된 시간을 나타냅니다.

페이로드에는 사용자의 다양한 정보를 포함할 수 있으며, 이러한 정보는 토큰이 유효한 동안 클라이언트와 서버 간에 안전하게 교환될 수 있습니다.

### 3. 서명(Signature)

서명은 JWT의 무결성과 진위를 보장하는 중요한 부분입니다. 서명은 헤더와 페이로드를 결합한 후 비밀 키와 서명 알고리즘을 사용하여 생성됩니다. 이를 통해 JWT가 생성된 이후에 변경되지 않았음을 보장할 수 있습니다.

#### 서명 생성 과정

1. **헤더와 페이로드 결합**: 헤더와 페이로드를 Base64Url로 인코딩한 후, 이들을 결합하여 하나의 문자열로 만듭니다.
   ```
   encodedHeader + "." + encodedPayload
   ```

2. **해시 생성**: 결합된 문자열을 비밀 키와 서명 알고리즘(e.g., HS256)을 사용하여 해싱합니다.

3. **서명 추가**: 해싱된 결과를 JWT의 마지막 부분으로 추가합니다.

#### 서명 생성 예시
```java
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### JWT의 전체 구조

JWT는 다음과 같은 형식을 가집니다:
```
header.payload.signature
```

예를 들어, 다음은 실제 JWT의 예시입니다:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### 서명의 역할

서명은 JWT의 무결성과 진위를 확인하는 데 중요한 역할을 합니다:

1. **무결성 보장**: JWT가 생성된 이후에 변경되지 않았음을 보장합니다. 클라이언트가 받은 JWT가 발급자가 의도한 내용과 동일하다는 것을 확인할 수 있습니다.

2. **진위 확인**: JWT가 신뢰할 수 있는 발급자에 의해 생성되었음을 확인합니다. 비밀 키를 알고 있는 발급자만이 올바른 서명을 생성할 수 있기 때문입니다.

### 서명 검증 과정

JWT를 수신한 측에서는 다음 과정을 통해 서명을 검증합니다:

1. **헤더와 페이로드 추출**: JWT의 헤더와 페이로드 부분을 추출합니다.

2. **재서명 생성**: 수신자가 알고 있는 비밀 키와 서명 알고리즘을 사용하여 헤더와 페이로드 부분을 다시 서명합니다.

3. **서명 비교**: 생성된 서명과 JWT에 포함된 서명을 비교합니다. 두 서명이 일치하면 JWT는 무결하며 신뢰할 수 있는 것으로 간주됩니다.

### 코드 예제에서 서명의 사용

#### 서명 생성
```java
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
```
여기서 `signWith` 메서드는 HS256 알고리즘과 비밀 키(`keyBytes`)를 사용하여 서명을 생성합니다.

또한 Jwts.builder()는 기본적으로 typ를 "JWT", alg를 "HS256"으로 설정합니다. 따라서, 별도로 설정하지 않아도 기본 헤더가 자동으로 설정됩니다. 그러나, 필요에 따라 `setHeaderParam()` 를 이용해 헤더를 명시적으로 설정할 수 있습니다.

#### 서명 검증
```java
public String extractEmail(String token) {
    return Jwts.parser() // parser 메서드를 통해 JWT를 파싱하고, 비밀 키를 설정합니다.
            .setSigningKey(keyBytes)  // 비밀 키를 설정하여 서명을 검증
            .parseClaimsJws(token) // JWT를 파싱하고 서명을 검증
            .getBody() // JWT의 본문(Claims)을 가져옴
            .getSubject(); // 주제->이메일을 추출하여 반환
}
```
여기서 `setSigningKey` 메서드는 비밀 키를 설정하여, `parseClaimsJws` 메서드를 통해 JWT의 서명을 검증합니다.

### 요약

- **서명(Signature)** 은 JWT의 무결성과 진위를 확인하는 암호화된 문자열입니다.
- 서명은 JWT가 변경되지 않았음을 보장하며, 신뢰할 수 있는 발급자에 의해 생성되었음을 확인합니다.
- 서명은 헤더와 페이로드를 결합한 후 비밀 키와 해시 알고리즘을 사용하여 생성됩니다.
- JWT를 수신한 측에서는 동일한 비밀 키를 사용하여 서명을 검증함으로써 JWT의 무결성과 진위를 확인할 수 있습니다.

JWT는 이러한 구조와 메커니즘을 통해 안전하고 신뢰할 수 있는 데이터 교환을 가능하게 합니다. 이로써 사용자는 안전하게 인증되고, 서버는 사용자의 신원을 신뢰할 수 있습니다.

---

## JWT Attack
JWT Attack은 JWT 수정하여 서버에 전송하는 것이다. 일반적으로 이미 인증된 사용자로 가장하여 액세스 제어를 무시하는 것이다. 해커가 임의의 값으로 유효한 토큰을 생성할 수 있는 경우 자신의 권한을 확대하거나 다른 사용자로 사칭하여 계정을 완전히 제어할 수 있다.

JWT 취약점은 일반적으로 애플리케이션 자체 내에서 결함이 있는 JWT 처리로 인해 발생한다. JWT의 처리는 설계상 상대적으로 유연하므로 개발자가 많은 구현 세부 사항을 스스로 결정할 수 있다. 그래서 이러한 취약점은 구현 결함으로 인하여 signature가 제대로 확인되지 않다는 것을 의미한다.

기본적으로 서버는 JWT에 대한 정보를 저장하지 않는다. 대산 각 토큰은 완전히 독립적인 엔티티이다,

이러한 방식은 몇 가지 장점이 있지만 근본적인 문제도 존재한다. 서버는 실제로 토큰의 내용이나 원래 signature가 무엇인지 모르기 때문에 제대로 확인하지 않으면 해커가 임의로 토큰을 변경할 수 있다.

