package com.worldline.mts.idm.scimctl.config.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;

public class FakeToken {

  static final String ISSUER = "https://example.com/issuer";

  static final String SIGN_KEY_LOCATION = "classpath:privateKey.pem";

  public static String generate(String uid, String userName, String... roles) {
    var resource_access = Map.of(
        "client_name_1", Map.of(
            "roles", List.of(roles)));
    var expiresAt = Instant.now().plus(5, ChronoUnit.SECONDS);
    return Jwt.issuer(ISSUER)
        .claim("resource_access", resource_access)
        .claim(Claims.preferred_username.name(), userName)
        .claim(Claims.sub.name(), uid)
        .issuedAt(Instant.now())
        .expiresAt(expiresAt)
        .sign(SIGN_KEY_LOCATION);

  }
}
