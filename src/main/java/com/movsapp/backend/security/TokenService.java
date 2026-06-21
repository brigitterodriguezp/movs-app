package com.movsapp.backend.security;

import com.movsapp.backend.exception.NoAutorizadoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
public class TokenService {
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final byte[] secret;
    private final Duration ttl;

    public TokenService(@Value("${app.security.jwt-secret:dev-only-change-this-secret}") String secret,
                        @Value("${app.security.jwt-ttl-minutes:30}") long ttlMinutes) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public TokenData issue(Long usuarioId, String correo, String rol, Long sesionId) {
        Instant expiresAt = Instant.now().plus(ttl);
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("uid=" + usuarioId + ";email=" + escape(correo) + ";role=" + escape(rol)
            + ";sid=" + sesionId + ";exp=" + expiresAt.getEpochSecond());
        String signature = sign(header + "." + payload);
        return new TokenData(header + "." + payload + "." + signature, expiresAt);
    }

    public Claims verify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new NoAutorizadoException("Token inválido.");
        String expected = sign(parts[0] + "." + parts[1]);
        if (!MessageDigestSafe.equals(expected, parts[2])) throw new NoAutorizadoException("Token inválido.");
        Map<String, String> claims = parse(new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8));
        long exp = Long.parseLong(claims.getOrDefault("exp", "0"));
        if (Instant.now().isAfter(Instant.ofEpochSecond(exp))) throw new NoAutorizadoException("Token expirado.");
        return new Claims(
            Long.valueOf(claims.get("uid")),
            claims.get("email"),
            claims.get("role"),
            Long.valueOf(claims.get("sid")),
            Instant.ofEpochSecond(exp)
        );
    }

    private String encode(String value) {
        return URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token.", ex);
        }
    }

    private Map<String, String> parse(String payload) {
        Map<String, String> claims = new HashMap<>();
        for (String item : payload.split(";")) {
            String[] pair = item.split("=", 2);
            if (pair.length == 2) claims.put(pair[0], unescape(pair[1]));
        }
        return claims;
    }

    private String escape(String value) {
        return value.replace("%", "%25").replace(";", "%3B").replace("=", "%3D");
    }

    private String unescape(String value) {
        return value.replace("%3D", "=").replace("%3B", ";").replace("%25", "%");
    }

    public record TokenData(String token, Instant expiresAt) {}
    public record Claims(Long usuarioId, String correo, String rol, Long sesionId, Instant expiresAt) {}

    private static final class MessageDigestSafe {
        static boolean equals(String left, String right) {
            return java.security.MessageDigest.isEqual(
                left.getBytes(StandardCharsets.UTF_8),
                right.getBytes(StandardCharsets.UTF_8)
            );
        }
    }
}
