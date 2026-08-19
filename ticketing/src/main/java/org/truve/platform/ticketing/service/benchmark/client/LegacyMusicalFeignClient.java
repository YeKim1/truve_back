package org.truve.platform.ticketing.service.benchmark.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.truve.platform.ticketing.service.benchmark.dto.LegacyShowResponse;

import com.truve.platform.common.response.ApiResult;

@FeignClient(name = "musical-service", url = "${spring.cloud.openfeign.client.config.musical-service.url:http://localhost:8085}")
public interface LegacyMusicalFeignClient {

	@GetMapping("/api/musical/shows/{showId}")
	ApiResult<LegacyShowResponse.Detail> getShowDetail(
		@PathVariable("showId") Long showId,
		@RequestHeader(name = "X-User-Id", required = false) UUID userId
	);
}
