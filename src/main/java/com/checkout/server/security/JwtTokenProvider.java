package com.checkout.server.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    @Value("${jwt.expiration-millis}")
    private Long expirationMillis;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String userName) {
        return createToken(userName);
    }

    private String createToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        long issuedAtTimestamp = System.currentTimeMillis();
        return Jwts.builder().setClaims(claims).setSubject(userName)
                .setIssuedAt(new Date(issuedAtTimestamp))
                .setExpiration(new Date(issuedAtTimestamp + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey).compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}