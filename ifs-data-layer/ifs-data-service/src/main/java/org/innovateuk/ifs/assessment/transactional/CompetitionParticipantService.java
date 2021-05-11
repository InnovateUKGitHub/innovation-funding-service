package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

/**
 * Service for managing {@link CompetitionParticipant}s.
 */
public interface CompetitionParticipantService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionParticipantResource>> getCompetitionAssessors(long assessorId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CompetitionParticipantResource>> getCompetitionAssessorsWithAssessmentPeriod(long assessorId);
}
