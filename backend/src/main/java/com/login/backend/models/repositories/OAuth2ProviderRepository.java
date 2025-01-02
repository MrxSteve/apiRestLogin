package com.login.backend.models.repositories;

import com.login.backend.models.entities.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2ProviderRepository extends JpaRepository<OAuth2Provider, Long> {
    Optional<OAuth2Provider> findByProviderAndProviderUserId(String provider, String providerUserId);
}
