// package com.shopping.mall.common;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;
// import jakarta.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
//
// import javax.crypto.SecretKey;
// import java.util.Date;
//
// @Component
// public class JwtUtil {
//
//	private final long validityInMs = 1000 * 60 * 60; // 1 hour
//	private SecretKey key;
//
//	@Value("${jwt.secret}")
//	private String secret;
//
//	@PostConstruct
//	public void init() {
//		key = Keys.hmacShaKeyFor(secret.getBytes());
//	}
//
//	public String createToken(String email) {
//		Date now = new Date();
//		Date validity = new Date(now.getTime() + validityInMs);
//
//		return Jwts.builder()
//				.subject(email)
//				.issuedAt(now)
//				.expiration(validity)
//				.signWith(key)  // No need to specify algorithm
//				.compact();
//	}
//
//	public String createRefreshToken(String email) {
//		Date now = new Date();
//		Date expiry = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 7); // 7 days
//
//		return Jwts.builder()
//				.subject(email)
//				.issuedAt(now)
//				.expiration(expiry)
//				.signWith(key)
//				.compact();
//	}
//
//	public String getSubject(String token) {
//		return Jwts.parser()
//				.verifyWith(key)
//				.build()
//				.parseSignedClaims(token)
//				.getPayload()
//				.getSubject();
//	}
//
//	public boolean validateToken(String token) {
//		try {
//			Jwts.parser()
//					.verifyWith(key)
//					.build()
//					.parseSignedClaims(token);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	public Claims getClaims(String token) {
//		return Jwts.parser()
//				.verifyWith(key)
//				.build()
//				.parseSignedClaims(token)
//				.getPayload();
//	}
// }
