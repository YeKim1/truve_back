package com.truve.platform.apigateway.authentication;

import java.util.UUID;
import javax.crypto.SecretKey;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory {

	private static final String DEFAULT_USER_ID = "00000000-0000-0000-0000-000000000001";
	private static final String DEFAULT_ROLE = "USER";

	private final AccessTokenBlacklistService blacklistService;
	private final JwtProperties jwtProperties;

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			String token = exchange.getRequest()
				.getHeaders()
				.getFirst("Authorization");

			String userId = DEFAULT_USER_ID;
			String role = DEFAULT_ROLE;
			String accessToken = "test-token";

			// 1. 클라이언트가 X-User-Id를 직접 전달한 경우 우선 채택
			String headerUserId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
			if (StringUtils.hasText(headerUserId)) {
				userId = headerUserId;
			}

			// 2. JWT 토큰이 있으면 파싱 시도 (실패해도 401 반환하지 않고 기본/전달된 ID로 통과)
			if (token != null && token.startsWith("Bearer ")) {
				try {
					accessToken = token.substring(7);
					SecretKey secretKey = jwtProperties.getSecretKey();
					Claims claims = Jwts.parser()
						.verifyWith(secretKey)
						.build()
						.parseSignedClaims(accessToken)
						.getPayload();

					String jti = claims.getId();
					if (jti != null && blacklistService.isExist(jti)) {
						log.warn("[JwtAuthFilter] Blacklisted token detected, but bypassing for testing.");
					}

					String parsedUserId = claims.get("user_public_id", String.class);
					String parsedRole = claims.get("role", String.class);

					if (StringUtils.hasText(parsedUserId)) {
						userId = parsedUserId;
					}
					if (StringUtils.hasText(parsedRole)) {
						role = parsedRole;
					}
				} catch (Exception e) {
					log.debug("[JwtAuthFilter] JWT parsing failed, using fallback userId: {}", userId);
				}
			}

			final String finalUserId = userId;
			final String finalRole = role;
			final String finalToken = accessToken;

			exchange.getAttributes().put("userId", finalUserId);

			return chain.filter(
				exchange.mutate()
					.request(
						exchange.getRequest()
							.mutate()
							.headers(headers -> {
								headers.set("X-User-Id", finalUserId);
								headers.set("X-User-Role", finalRole);
								headers.set("X-Token", finalToken);
							})
							.build()
					)
					.build()
			);
		};
	}
}
