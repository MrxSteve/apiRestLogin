package com.login.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
@Data
public class JwtUtils {
    // Obtener la clave secreta de las variables de entorno
    @Value("${JWT_SECRET")
    private String SECRET_KEY;

    private final long TOKEN_VALIDITY = 1000 * 60 * 60 * 10; // 10 horas

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extraer el nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Validar el token
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    // Comprobar si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Obtener la fecha de expiracion del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // extraer un dato del token
    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // extraer todos los datos del token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
