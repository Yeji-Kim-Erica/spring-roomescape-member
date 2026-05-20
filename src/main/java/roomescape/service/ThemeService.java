package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.Theme;
import roomescape.exception.ErrorCode;
import roomescape.exception.ThemeException;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ThemeCreateRequest;
import roomescape.service.dto.response.ThemeResponse;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    private static final int DATA_RANGE = 7;

    public ThemeResponse create(final ThemeCreateRequest request) {
        final Theme themeWithoutId = Theme.create(
                request.name(),
                request.description(),
                request.thumbnailUrl()
        );

        Theme theme = themeRepository.save(themeWithoutId);

        return mapDomainToDto(theme);
    }

    public void delete(final Long themeId) {
        boolean deleted = themeRepository.deleteById(themeId);

        if (!deleted) {
            throw new ThemeException(ErrorCode.THEME_NOT_FOUND);
        }
    }

    public List<ThemeResponse> getPopularThemes() {
        final LocalDate today = LocalDate.now();
        final LocalDate startDate = today.minusDays(DATA_RANGE);

        return themeRepository.findPopularThemes(startDate, today)
                .stream()
                .map(ThemeService::mapDomainToDto)
                .toList();
    }

    public List<ThemeResponse> getThemes() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeService::mapDomainToDto)
                .toList();
    }

    private static ThemeResponse mapDomainToDto(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnailUrl()
        );
    }
}
