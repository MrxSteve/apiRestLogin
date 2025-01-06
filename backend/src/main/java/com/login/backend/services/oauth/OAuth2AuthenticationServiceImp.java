package com.login.backend.services.oauth;

import com.login.backend.models.dtos.AuthResponse;
import com.login.backend.models.entities.OAuth2Provider;
import com.login.backend.models.entities.RefreshToken;
import com.login.backend.models.entities.Role;
import com.login.backend.models.entities.User;
import com.login.backend.services.refresh.IRefreshTokenService;
import com.login.backend.services.user.IUserService;
import com.login.backend.utils.JwtUtils;
import lombok.Data;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Data
public class OAuth2AuthenticationServiceImp implements IOAuth2AuthenticationService{
    private final IUserService userService;
    private final IOAuth2ProviderService oAuth2ProviderService;
    private final IRefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse authenticateWithOAuth2(OAuth2User oAuth2User, String provider) {
        String providerUserId;

        // Identificar el atributo correcto según el proveedor
        switch (provider.toLowerCase()) {
            case "google":
                providerUserId = oAuth2User.getAttribute("sub");
                break;
            case "facebook":
            case "github":
                providerUserId = oAuth2User.getAttribute("id");
                break;
            default:
                throw new RuntimeException("Proveedor no soportado: " + provider);
        }

        String email = oAuth2User.getAttribute("email");
        User user = findOrCreateUser(provider, providerUserId, email, oAuth2User.getAttribute("name"));

        String accessToken = jwtUtils.generateToken(user.getUsername());
        RefreshToken refreshToken = generateRefreshToken(user);

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getUsername(), roles);
    }

    @Override
    public User findOrCreateUser(String provider, String providerUserId, String email, String username) {
        return oAuth2ProviderService.findByProviderAndProviderUserId(provider, providerUserId)
                .map(OAuth2Provider::getUser)
                .orElseGet(() -> {
                    User newUser = userService.findByEmail(email).orElseGet(() -> {
                        User user = new User();
                        user.setEmail(email);
                        user.setUsername(username);
                        // Llamar al nuevo metodo para registrar usuarios de OAuth2
                        return userService.registerOAuth2User(user);
                    });

                    OAuth2Provider oAuth2Provider = new OAuth2Provider();
                    oAuth2Provider.setProvider(provider);
                    oAuth2Provider.setProviderUserId(providerUserId);
                    oAuth2Provider.setUser(newUser);
                    oAuth2ProviderService.save(oAuth2Provider);

                    return newUser;
                });
    }

    @Override
    public RefreshToken generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpirationDate(LocalDateTime.now().plusDays(7));
        refreshTokenService.save(refreshToken);
        return refreshToken;
    }
}
