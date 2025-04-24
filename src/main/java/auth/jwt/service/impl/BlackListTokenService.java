package auth.jwt.service.impl;

import auth.jwt.entity.BlackListToken;
import auth.jwt.repository.BlackListTokenRepo;
import auth.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {
    private final BlackListTokenRepo blackListTokenRepo;
    private final JwtService jwtService;
    public void blackListAToken(String token) {
        if (!blackListTokenRepo.existsByToken(token)) {
            LocalDateTime expiry= jwtService.extractExpiration(token).
                    toInstant().
                    atZone(ZoneId.systemDefault()).
                    toLocalDateTime();
            blackListTokenRepo.save(new BlackListToken(token, expiry));
        }
    }
    public boolean isBlackListed(String token) {
        return blackListTokenRepo.existsByToken(token);
    }
}
