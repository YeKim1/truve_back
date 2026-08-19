package org.truve.platform.ticketing.service.benchmark.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.truve.platform.ticketing.service.benchmark.client.LegacyMusicalFeignClient;
import org.truve.platform.ticketing.service.benchmark.dto.LegacyShowResponse;
import org.truve.platform.ticketing.service.ticketing.domain.entity.ShowScheduled;
import org.truve.platform.ticketing.service.ticketing.dto.SeatSectionsDto;
import org.truve.platform.ticketing.service.ticketing.dto.TicketingResponse;
import org.truve.platform.ticketing.service.ticketing.repository.ScheduledSeatRepository;
import org.truve.platform.ticketing.service.ticketing.repository.ShowScheduledRepository;
import org.truve.platform.ticketing.service.ticketing.service.TicketingService;

import com.truve.platform.common.exception.CustomException;
import com.truve.platform.common.exception.ErrorCode;
import com.truve.platform.common.response.ApiResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenchmarkTicketingService {

	private static final UUID FALLBACK_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

	private final TicketingService ticketingService;
	private final LegacyMusicalFeignClient legacyMusicalFeignClient;
	private final ShowScheduledRepository showScheduledRepository;

	public TicketingResponse.Show getAsIsShow(UUID userId, Long showScheduleId, String sessionToken) {
		UUID targetUserId = userId != null ? userId : FALLBACK_USER_ID;
		tryHeartbeat(showScheduleId, targetUserId, sessionToken);

		ApiResult<LegacyShowResponse.Detail> result = legacyMusicalFeignClient.getShowDetail(showScheduleId, targetUserId);
		LegacyShowResponse.Detail detail = result.getData();
		if (detail == null) {
			throw new CustomException(ErrorCode.INVALID_SHOW_SCHEDULE);
		}

		String title = detail.getTitle();
		String venueName = detail.getVenue() != null ? detail.getVenue().getName() : "";
		LocalDateTime startAt = detail.getStartTime() != null ? detail.getStartTime() : LocalDateTime.now();

		return TicketingResponse.Show.of(title, venueName, startAt);
	}

	public TicketingResponse.Show getToBeShow(UUID userId, Long showScheduleId, String sessionToken) {
		UUID targetUserId = userId != null ? userId : FALLBACK_USER_ID;
		tryHeartbeat(showScheduleId, targetUserId, sessionToken);

		ShowScheduled schedule = showScheduledRepository.findById(showScheduleId)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_SHOW_SCHEDULE));

		return TicketingResponse.Show.of(schedule.getTitle(), schedule.getVenueName(), schedule.getStartAt());
	}

	private void tryHeartbeat(Long showScheduleId, UUID userId, String sessionToken) {
		if (sessionToken != null && !sessionToken.isBlank()) {
			try {
				ticketingService.heartbeat(showScheduleId, userId, sessionToken);
			} catch (Exception e) {
				log.debug("[Benchmark] Heartbeat ignored for benchmark load test.");
			}
		}
	}
}
