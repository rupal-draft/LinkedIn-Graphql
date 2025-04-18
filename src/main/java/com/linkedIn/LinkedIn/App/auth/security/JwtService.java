package com.linkedIn.LinkedIn.App.auth.security;

import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 characters long for HS256:"+secretKey.length()+secretKey);
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getAccessJwtToken(User user){
        try{
            return Jwts
                    .builder()
                    .subject(String.valueOf(user.getId()))
                    .claim("email",user.getEmail())
                    .claim("role",user.getRole().toString())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis()+1000*60*10))
                    .signWith(getSecretKey())
                    .compact();
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new ServiceUnavailableException("Failed to generate access token");
        }
    }

    public String getRefreshJwtToken(User user){
        try{
            return Jwts
                    .builder()
                    .subject(String.valueOf(user.getId()))
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis()+ 1000L *60*60*24*30*6))
                    .signWith(getSecretKey())
                    .compact();
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new ServiceUnavailableException("Failed to generate refresh token");
        }
    }

    public Long getUserId(String token){
        try{
            Claims claims = Jwts
                    .parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.valueOf(claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new ServiceUnavailableException("Token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new ServiceUnavailableException("Invalid token");
        }
    }
}
