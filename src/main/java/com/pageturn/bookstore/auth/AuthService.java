package com.pageturn.bookstore.auth;

import com.pageturn.bookstore.auth.dto.AuthResponse;
import com.pageturn.bookstore.auth.dto.LoginRequest;
import com.pageturn.bookstore.auth.dto.RefreshRequest;
import com.pageturn.bookstore.auth.dto.RegisterRequest;
import com.pageturn.bookstore.common.exception.AuthenticationFailedException;
import com.pageturn.bookstore.user.Role;
import com.pageturn.bookstore.user.User;
import com.pageturn.bookstore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use: " + request.email());
        }

        var user = User.builder()
                .email(request.email())
                .pwdHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
        return generateTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password "));

        if (!passwordEncoder.matches(request.password(), user.getPwdHash())) {
            throw new AuthenticationFailedException("Invalid email or password");
        }

        // Revoke existing refresh tokens on users new login
        refreshTokenRepository.revokedAllByUserId(user.getId());
        return generateTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        var refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new AuthenticationFailedException("Invalid refresh token"));

        if (!refreshToken.isUsable()) {
            throw new AuthenticationFailedException("Refresh token expired or has been revoked");
        }

        //Revoke any used refresh token for a "fresh" rotation
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        var user = refreshToken.getUser();
        return generateTokens(user);
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                   refreshTokenRepository.revokedAllByUserId(token.getUser().getId());
                });
    }

    private AuthResponse generateTokens(User user) {
        var accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        var refreshTokenValue = jwtService.generateRefreshToken(user.getEmail());
        var refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(accessToken, refreshTokenValue, jwtService.getRefreshTokenExpiration()/1000);
    }

}
