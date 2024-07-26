package study.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(HttpServletRequest request) {
        try {
            // 요청 속성에서 사용자 정보를 가져옴
            User user = (User) request.getAttribute("user");

            if (user == null) {
                return ResponseEntity.status(401).body("User not authenticated");
            }

            Optional<User> foundUser = userService.findUserByEmail(user.getEmail());
            return ResponseEntity.ok("User Page successfully: " + foundUser.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
