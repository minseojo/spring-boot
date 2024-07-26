package study.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.user.User;
import study.user.UserService;
import study.user.annotation.Authenticated;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> user) {
        try {
            userService.registerUser(user.get("email"), user.get("password"));
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Authenticated
    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(User user) {
        Optional<User> foundUser = userService.findUserByEmail(user.getEmail());
        return ResponseEntity.ok("User Page successfully: " + foundUser.get());
    }

    @Authenticated
    @PutMapping("/change-email")
    public ResponseEntity<?> changeEmail(User user) {
        // ... 이메일 변경 로직
        return ResponseEntity.ok("change email successfully");
    }

    @Authenticated
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(User user) {
        // ... 비밀번호 변경 로직
        return ResponseEntity.ok("change password successfully");
    }
}
