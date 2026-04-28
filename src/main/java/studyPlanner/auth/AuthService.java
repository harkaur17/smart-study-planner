package studyPlanner.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import studyPlanner.model.User;
import studyPlanner.repository.UserRepository;
import studyPlanner.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user
    public String register(String name, String email, String password) {
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(password);

        // Create and save the user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Return a token so they're logged in immediately after registering
        return jwtUtil.generateToken(email);
    }

    // Login an existing user
    public String login(String email, String password) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if password matches the hashed one in the database
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Return a token
        return jwtUtil.generateToken(email);
    }
}