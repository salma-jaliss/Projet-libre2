package ma.cabinet.auth.service;

import ma.cabinet.auth.client.CabinetServiceClient;
import ma.cabinet.auth.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CabinetServiceClient cabinetServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO;
        try {
            userDTO = cabinetServiceClient.getUserByLogin(username);
        } catch (Exception e) {
            // Fallback for testing since Cabinet Service might not be ready
            if ("admin".equals(username)) {
                userDTO = UserDTO.builder()
                        .login("admin")
                        .pwd("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7QA8q.Kbw") // password: "password"
                        .role("ADMINISTRATEUR")
                        .cabinetId(1L)
                        .build();
            } else {
                 throw new UsernameNotFoundException("User not found: " + username);
            }
        }

        if (userDTO == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
                .username(userDTO.getLogin())
                .password(userDTO.getPwd())
                .roles(userDTO.getRole())
                .build();
    }
}
