package dev.harshit.bookmyshow.services;

import dev.harshit.bookmyshow.models.User;
import dev.harshit.bookmyshow.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User signUp(String email, String password) {

        // Check if user already exists in DB
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            throw new RuntimeException();    // TODO: UserAlreadyExistsException
        }

        User user = new User();
        user.setEmail(email);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setPassword(encoder.encode(password));

        /* To match the user password during login
        *  if (encoder.matches(password, userRepository.______)) {
        *
        * }
        * */

        return userRepository.save(user);
    }
}
