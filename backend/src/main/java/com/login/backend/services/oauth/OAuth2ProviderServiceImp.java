package com.login.backend.services.oauth;

import com.login.backend.models.entities.OAuth2Provider;
import com.login.backend.models.repositories.OAuth2ProviderRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class OAuth2ProviderServiceImp implements IOAuth2ProviderService {
    private final OAuth2ProviderRepository oAuth2ProviderRepository;

    @Override
    public OAuth2Provider save(OAuth2Provider oAuth2Provider) {
        return oAuth2ProviderRepository.save(oAuth2Provider);
    }

    @Override
    public Optional<OAuth2Provider> findByProviderAndProviderUserId(String provider, String providerUserId) {
        return oAuth2ProviderRepository.findByProviderAndProviderUserId(provider, providerUserId);
    }
}
