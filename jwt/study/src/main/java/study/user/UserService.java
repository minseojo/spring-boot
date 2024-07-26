package study.user;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(email, hashedPassword);
        userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }
}
