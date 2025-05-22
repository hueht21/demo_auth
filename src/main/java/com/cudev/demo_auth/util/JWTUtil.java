package com.cudev.demo_auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTUtil {
    public String generateToken(String username, List<String> roles, List<String> menus, Set<String> listApps) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("menus", menus);
        claims.put("apps", listApps);

        try {
            String privateKeys = null;
            privateKeys = RSAUtil.getPrivateKey();
            PrivateKey privateKey = RSAUtil.decodePrivateKey(privateKeys);

            return Jwts.builder().claims().add(claims).subject(username).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000)).and().signWith(privateKey, SignatureAlgorithm.RS256).compact();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getKeyPublic() {

        String publicKey = null;
        try {
            publicKey = RSAUtil.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return publicKey;
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        try {
            PublicKey key = RSAUtil.decodePublicKey(getKeyPublic());

            return Jwts.parser().setSigningKey(
                            key)
                    .build().
                    parseSignedClaims(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
