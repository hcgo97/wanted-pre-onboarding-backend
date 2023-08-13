package com.example.wpob.util;

import com.example.wpob.exception.ApiResultStatus;
import com.example.wpob.exception.TokenException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtUtil {

    @Value("${api.jwt.secret}")
    private String SECRET_KEY;

    @Value("${api.jwt.expire}")
    private long VALIDITY_IN_HOURS;

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        VALIDITY_IN_HOURS = VALIDITY_IN_HOURS * 1000;
    }

    public String createToken(Long id, String email) {
        Claims claims = Jwts.claims();

        Date now = new Date();
        Date validity = null;

        try {
            claims.put("userId", id);
            claims.put("email", email);
            validity = new Date(now.getTime() + VALIDITY_IN_HOURS);

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact();

        } catch (Exception e) { // 토큰 생성 중 에러
            throw new TokenException(ApiResultStatus.TOKEN_CREATED_FAILED, e.getMessage());
        }
    }

    /**
     * 토큰 파싱
     */
    public Object getBodyValue(String token, String field) {
        if (this.validateJwtToken(token)) {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .get(field)
                    .toString();

        } else { // 파싱 중 에러
            throw new TokenException(ApiResultStatus.TOKEN_INVALID);
        }
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateJwtToken(String authToken) throws JwtException {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;

        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message: {}", e.getMessage());
            throw new TokenException(ApiResultStatus.TOKEN_DATE_EXPIRED);

        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Invalid JWT signature -> Message: {} ", e.getMessage());
            throw new TokenException(ApiResultStatus.TOKEN_INVALID);
        }
    }
}
