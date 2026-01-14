package ma.cabinet.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;  // Changed from accessToken to match sequence diagram
    private String refreshToken;
    private UserDTO user;
}
