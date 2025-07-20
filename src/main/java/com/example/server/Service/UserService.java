package com.example.server.Service;

import com.example.server.Model.User;
import com.example.server.Repository.UserRepository;
import com.example.server.dto.UserDTO;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return "Email already exists";
        }

        User user = new User();
        user.setName(userDTO.getFullName());
        user.setStudentId(userDTO.getStudentId());
        user.setEmail(userDTO.getEmail());
        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword); // 🚨 In real apps, hash this!
        
        userRepository.save(user);
        return "Signup successful";
    }

    public boolean loginUser(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        System.out.println("Data from db to check: " + optionalUser);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return BCrypt.checkpw(userDTO.getPassword(), user.getPassword()); // compare plaintext vs hashed
        }
        return false; // email not found
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> searchUser(String search) {
        return userRepository.findByEmail(search);
    }
    
    public void resetPassword(String email,String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        userRepository.save(user);
    }






}
