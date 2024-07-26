package study.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.jwt.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;

    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> user) {
        try {
            userService.registerUser(user.get("email"), user.get("password"));
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> user) {
        try {
            User authenticatedUser = userService.authenticateUser(user.get("email"), user.get("password"));
            String token = jwtUtil.generateToken(authenticatedUser);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("email", authenticatedUser.getEmail());
                put("password", authenticatedUser.getPassword());
                put("token", token);
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}