package eu.accesa.onlinestore.configuration.security;

import eu.accesa.onlinestore.model.entity.UserEntity;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class JwtTokenUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateAccessToken(UserEntity user) {
        LocalDateTime currentTime = LocalDateTime.now();
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getUsername()))
                .setIssuer(jwtIssuer)
                .setIssuedAt(Timestamp.valueOf(currentTime))
                .setExpiration(Timestamp.valueOf(currentTime.plusWeeks(1))) // 1 week
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true; // successful validation
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty - {}", ex.getMessage());
        }

        return false;
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject().split(",")[1];
    }
    public String generatePasswordToken(UserEntity user){
        LocalDateTime currentTime = LocalDateTime.now();
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getUsername()))
                .setIssuer(jwtIssuer)
                .setIssuedAt(Timestamp.valueOf(currentTime))
                .setExpiration(Timestamp.valueOf(currentTime.plusHours(1))) //valid for 1 Hour
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
