package com.login.backend.services.oauth;

import com.login.backend.models.dtos.AuthResponse;
import com.login.backend.models.entities.RefreshToken;
import com.login.backend.models.entities.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface IOAuth2AuthenticationService {
    AuthResponse authenticateWithOAuth2(OAuth2User oAuth2User, String provider);
    User findOrCreateUser(String provider, String providerUserId, String email, String username);
    RefreshToken generateRefreshToken(User user);
}
