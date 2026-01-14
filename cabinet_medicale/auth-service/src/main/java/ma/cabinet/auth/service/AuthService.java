package ma.cabinet.auth.service;

import ma.cabinet.auth.client.CabinetServiceClient;
import ma.cabinet.auth.model.AuthRequest;
import ma.cabinet.auth.model.AuthResponse;
import ma.cabinet.auth.model.UserDTO;
import ma.cabinet.security.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final CabinetServiceClient cabinetServiceClient;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, CabinetServiceClient cabinetServiceClient) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.cabinetServiceClient = cabinetServiceClient;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
        UserDTO userDTO = null;
        try {
            userDTO = cabinetServiceClient.getUserByLogin(request.getLogin());
        } catch (Exception ignored) {
        }
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities());
        if (userDTO != null) {
            extraClaims.put("userId", userDTO.getId());
            extraClaims.put("role", userDTO.getRole());
            extraClaims.put("cabinetId", userDTO.getCabinetId());
        }
        
        String accessToken = jwtUtil.generateToken(extraClaims, userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .build();
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserDTO userDTO = null;
        try {
            userDTO = cabinetServiceClient.getUserByLogin(username);
        } catch (Exception ignored) {
        }
        
        if (jwtUtil.isTokenValid(refreshToken, userDetails)) {
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", userDetails.getAuthorities());
            if (userDTO != null) {
                extraClaims.put("userId", userDTO.getId());
                extraClaims.put("role", userDTO.getRole());
                extraClaims.put("cabinetId", userDTO.getCabinetId());
            }
            
            String accessToken = jwtUtil.generateToken(extraClaims, userDetails);
            return AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .user(userDTO)
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }
    
    public boolean validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtUtil.isTokenValid(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}
