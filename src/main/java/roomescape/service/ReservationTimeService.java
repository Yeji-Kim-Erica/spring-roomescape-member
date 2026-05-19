package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.ReservationTime;
import roomescape.exception.ErrorCode;
import roomescape.exception.ReservationTimeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.dto.request.ReservationTimeCreateRequest;
import roomescape.service.dto.response.ReservationTimeResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public List<ReservationTimeResponse> getTimes() {
        return reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeService::mapDomainToDto)
                .toList();
    }

    public ReservationTimeResponse create(ReservationTimeCreateRequest data) {
        final ReservationTime reservationTime = ReservationTime.create(
                data.startAt(),
                data.endAt()
        );

        final ReservationTime savedTime = reservationTimeRepository.save(reservationTime);

        return mapDomainToDto(savedTime);
    }

    public void delete(final Long timeId) {
        final boolean hasAnyOngoingReservation = reservationRepository.existsByTimeId(timeId);
        if (hasAnyOngoingReservation) {
            throw new ReservationTimeException(ErrorCode.TIME_HAS_RESERVATION);
        }

        final boolean deleted = reservationTimeRepository.delete(timeId);

        if (!deleted) {
            throw new ReservationTimeException(ErrorCode.TIME_NOT_FOUND);
        }
    }

    private static ReservationTimeResponse mapDomainToDto(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt(),
                reservationTime.getEndAt()
        );
    }
}
