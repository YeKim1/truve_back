package org.truve.platform.ticketing.service.benchmark.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.truve.platform.ticketing.service.benchmark.service.BenchmarkTicketingService;
import org.truve.platform.ticketing.service.ticketing.dto.TicketingResponse;

import com.truve.platform.common.response.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticketing/benchmark")
@Tag(name = "Benchmark", description = "부하 테스트 및 성능 측정용 벤치마크 API")
public class BenchmarkTicketingController {

	private static final String USER_ID_HEADER = "X-User-Id";
	private static final String SESSION_HEADER = "X-Session-Ticket";

	private final BenchmarkTicketingService benchmarkTicketingService;

	@Operation(summary = "[As-Is] 공연 회차 정보 조회 (동기 HTTP Feign 통신)", description = "Musical 서버로 동기 HTTP 요청을 보내 공연 정보를 조회합니다.")
	@GetMapping("/as-is/shows/{showScheduleId}/seats")
	public ApiResult<TicketingResponse.Show> getAsIsShow(
		@RequestHeader(value = USER_ID_HEADER, required = false) UUID userId,
		@RequestHeader(value = SESSION_HEADER, required = false) String sessionToken,
		@PathVariable Long showScheduleId
	) {
		var response = benchmarkTicketingService.getAsIsShow(userId, showScheduleId, sessionToken);
		return ApiResult.ok(response);
	}

	@Operation(summary = "[To-Be] 공연 회차 정보 조회 (역정규화 DB 조회)", description = "사전 역정규화된 Ticketing DB에서 직접 공연 정보를 조회합니다.")
	@GetMapping("/to-be/shows/{showScheduleId}/seats")
	public ApiResult<TicketingResponse.Show> getToBeShow(
		@RequestHeader(value = USER_ID_HEADER, required = false) UUID userId,
		@RequestHeader(value = SESSION_HEADER, required = false) String sessionToken,
		@PathVariable Long showScheduleId
	) {
		var response = benchmarkTicketingService.getToBeShow(userId, showScheduleId, sessionToken);
		return ApiResult.ok(response);
	}
}
