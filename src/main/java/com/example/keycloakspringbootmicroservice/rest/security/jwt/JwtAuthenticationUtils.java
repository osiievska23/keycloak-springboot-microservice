package com.example.keycloakspringbootmicroservice.rest.security.jwt;

import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.BEARER_TOKEN_PREFIX;
import static com.example.keycloakspringbootmicroservice.constants.ExceptionConstants.GENERIC_MESSAGE_EXCEPTION;
import static org.springframework.util.StringUtils.hasText;

import com.example.keycloakspringbootmicroservice.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationUtils {

    public final String PublicKey;

    public JwtAuthenticationUtils(Environment environment) {
        PublicKey = environment.getProperty("api.keycloak.public.key");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }

    public String getUserEmailFromJwt(String jwtToken) {
        return Jwts.parser()
            .setSigningKey(getPublicKey())
            .parseClaimsJws(jwtToken)
            .getBody()
            .get("email")
            .toString();
    }

    public String getJwtHeaderFromRequest(HttpServletRequest request) {
        String bearerToken = getJwtTokenFromHeader(request);

        if (!hasText(bearerToken)) {
            bearerToken = getJwtTokenFromParams(request);
        }

        if (!hasText(bearerToken) && request.getCookies() != null) {
            bearerToken = getJwtTokenFromCookies(request);
        }

        return hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX) ?
            bearerToken.substring(BEARER_TOKEN_PREFIX.length()) : null;
    }

    private String getJwtTokenFromCookies(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
            .filter(c -> c.getName().equals(HttpHeaders.AUTHORIZATION))
            .map(Cookie::getValue)
            .findAny()
            .orElse(null);
    }

    private String getJwtTokenFromParams(HttpServletRequest request) {
        return request.getParameter(HttpHeaders.AUTHORIZATION);
    }

    private String getJwtTokenFromHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private PublicKey getPublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(PublicKey));
            return kf.generatePublic(keySpecX509);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new UnauthorizedException(GENERIC_MESSAGE_EXCEPTION);
        }
    }
}
