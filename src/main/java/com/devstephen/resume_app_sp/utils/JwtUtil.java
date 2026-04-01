package com.devstephen.resume_app_sp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Date;

@Configuration
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;


    public String generateToken (String userId){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .signWith(getSignInKey())
                .expiration(expiration)
                .compact();

    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }


// fix: migrate JWT parsing from deprecated JJWT 0.11 API to 0.12+ — replace setSigningKey/parseClaimsJws/getBody with verifyWith/parseSignedClaims/getPayload"
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
