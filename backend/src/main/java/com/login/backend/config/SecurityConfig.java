package com.login.backend.config;

import com.login.backend.services.security.CustomUserDetailService;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@Data
public class SecurityConfig {
    private final CustomUserDetailService customUserDetailService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/password/**").permitAll() // Permitir las rutas de recuperación de contraseña
                        .requestMatchers("/api/auth/**").permitAll() // Registro y login
                        .requestMatchers("/api/oauth2/**").permitAll() // Autenticación con OAuth2
                        .requestMatchers("/oauth2/authorization/**").permitAll() // Punto de entrada de OAuth2
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Rutas protegidas para ADMIN
                        .anyRequest().authenticated() // Requiere autenticación para todas las demás rutas
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization")) // Configurar punto de entrada de OAuth2
                        .successHandler((request, response, authentication) -> {
                            // Redirigir la solicitud al controlador /api/oauth2/login después de un inicio de sesión exitoso
                            request.getRequestDispatcher("/api/oauth2/login").forward(request, response);
                        })
                        .failureUrl("/api/oauth2/login-failure") // En caso de fallo
                )
                .logout(logout -> logout
                        .logoutUrl("/api/oauth2/logout")
                        .logoutSuccessUrl("/api/oauth2/logout-success")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
