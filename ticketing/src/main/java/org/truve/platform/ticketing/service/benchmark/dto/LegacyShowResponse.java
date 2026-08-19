package org.truve.platform.ticketing.service.benchmark.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LegacyShowResponse {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Detail {
		private Long showId;
		private String title;
		private String description;
		private Integer runtimeMin;
		private Integer ageLimit;
		private String posterUrl;
		private List<String> noticeImgs;
		private List<String> detailImgs;
		private String date;
		private LocalDateTime startTime;
		private LocalDateTime endTime;
		private Venue venue;
		private List<Casting> castings;
		private List<SimpleSchedule> schedules;
		private List<SeatGrade> seatGrades;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Venue {
		private Long venueId;
		private String name;
		private String address;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SimpleSchedule {
		private Long scheduleId;
		private LocalDateTime showTime;
		private String status;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Casting {
		private Long showCastId;
		private Long artistId;
		private String artistName;
		private String profileImageUrl;
		private String roleName;
		private Integer order;
		private Boolean isLiked;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SeatGrade {
		private Long showSeatGradeId;
		private String gradeName;
		private String colorCode;
		private Long price;
	}
}
