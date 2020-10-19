package com.sm.client.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.api.client.util.Base64;
import com.sm.client.model.JwtTokenRequest;
import com.sm.model.SmException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 20200612233645756L;
    public static final long JWT_TOKEN_VALIDITY = 8 * 3600;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.apple.secret}")
    private String secretApple;

    @Value("${jwt.apple.secret64}")
    private String secretApple64;

    @Value("${jwt.apple.alg:ES256}")
    private String alg;

    @Value("${jwt.apple.kid}")
    private String kid;

    @Value("${jwt.apple.typ:JWT}")
    private String typ;

    @Value("${jwt.apple.iss}")
    private String iss;

    @Value("${jwt.origin:http://localhost:4200}")
    private String origin;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public String generateHS256(JwtTokenRequest jwtTokenRequest, Long expiration) throws SmException {
        try {
            LinkedHashMap<String, Object> header;
            LinkedHashMap<String, Object> payload;
            if (jwtTokenRequest == null || jwtTokenRequest.getHeader() == null) {
                // need to create header and payload
                header = new LinkedHashMap<>();
                header.put("alg", alg);
                header.put("kid", kid);
                header.put("typ", typ);
            } else {
                header = jwtTokenRequest.getHeader();
            }
            if (jwtTokenRequest == null || jwtTokenRequest.getPayload() == null) {
                payload = new LinkedHashMap<>();
                payload.put("iss", iss);
                payload.put("iat", System.currentTimeMillis() / 1000);
                payload.put("origin", origin);
                if (expiration == null) {
                    payload.put("exp", ((double) System.currentTimeMillis() + 3600_000D) / 1000D);
                } else {
                    payload.put("exp", ((double) System.currentTimeMillis() + (double) expiration * 1000) / 1000D);
                }
            } else {
                payload = jwtTokenRequest.getPayload();
            }
            //fix format "exp"
            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                if ("exp".equals(entry.getKey()) && entry.getValue() != null && entry.getValue() instanceof Double) {
                    entry.setValue(new BigDecimal((Double) entry.getValue()).round(new MathContext(13)));
                }
            }
            payload.put("origin", origin);
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.valueOf(alg);
            if (signatureAlgorithm.isEllipticCurve()) {
               return Jwts.builder().setClaims(payload).setHeader(header)
                        .signWith(signatureAlgorithm,
                                KeyFactory.getInstance("EC").generatePrivate(
                                        new PKCS8EncodedKeySpec(Base64.decodeBase64(secretApple64))) ).compact();
            }
            return Jwts.builder().setClaims(payload).setHeader(header)
                    .signWith(signatureAlgorithm, secretApple).compact();
        } catch (Exception ex) {
            throw new SmException(ex.getMessage(), 500);
        }
    }

}