package com.login.backend.services.oauth;

import com.login.backend.models.entities.OAuth2Provider;

import java.util.Optional;

public interface IOAuth2ProviderService {
    OAuth2Provider save(OAuth2Provider oAuth2Provider);
    Optional<OAuth2Provider> findByProviderAndProviderUserId(String provider, String providerUserId);
}
