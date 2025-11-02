package kr.adapterz.community.common.security.jwt;

import static kr.adapterz.community.common.message.ErrorCode.TOKEN_EXPIRED;
import static kr.adapterz.community.common.message.ErrorCode.TOKEN_INVALID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import kr.adapterz.community.common.exception.dto.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey key;

    /**
     * Secret key로 사용할 key 필드를 할당합니다.
     * <p>
     * '@Value' 주입 시기는 생성자 호출 시기 보다 빠르므로 '@PostConstruct'에서 처리합니다.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access token과 Refresh token을 갖고 있는 DTO를 생성합니다.
     *
     * @param userId
     * @return
     */
    public JwtDto generateTokens(Long userId) {
        String accessToken = generateAccessToken(userId);
        String refreshToken = generateRefreshToken();
        return new JwtDto(accessToken, refreshToken);
    }

    /**
     * Access token을 생성합니다. payload에 userId를 포함합니다.
     *
     * @param userId
     * @return
     */
    public String generateAccessToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Refresh token을 생성합니다. 세션처럼 관리만 하면 되기 때문에 단순 문자열인 UUID를 사용합니다.
     * <p>
     * 이후 실제 JWT로 발급하여 관리할지, DB에서 관리할지, Redis에서 관리할지 고민입니다.
     * <p>
     * JWT로 관리하면 만료일자를 서버에서 직접 관리하지 않아도 되는 장점이 있습니다.
     * <p>
     * UUID로 관리하면 refresh token에 페이로드가 없기 때문에 단순하게 관리할 수 있고 유효성 검사가 비교적 간단하지만, 만료 일자를 쿠키에 넣어도 웹
     * 브라우저에서 이를 변경 할 수 있기 때문에 안전하지 않습니다.
     *
     * @return UUID
     */
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 토큰의 유효성 검사를 진행합니다.
     *
     * @param token
     * @return
     * @Exception IllegalArgumentException은 parseSignedClaims에서 token이 null이나 비어있거나 white space 하나만
     * 있을 때 발생합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(TOKEN_INVALID);
        }
    }

    /**
     * Access token에서 userId를 추출합니다.
     *
     * @param token
     * @return
     * @Exception IllegalArgumentException은 parseSignedClaims에서 token이 null이나 비어있거나 white space 하나만
     * 있을 때 발생합니다.
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException(TOKEN_INVALID);
        }
    }
}
