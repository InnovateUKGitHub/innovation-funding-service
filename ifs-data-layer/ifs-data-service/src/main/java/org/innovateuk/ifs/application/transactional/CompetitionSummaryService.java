package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionSummaryService {
	@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource','VIEW_COMPETITION_SUMMARY')")
	ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(final Long competitionId);
}
