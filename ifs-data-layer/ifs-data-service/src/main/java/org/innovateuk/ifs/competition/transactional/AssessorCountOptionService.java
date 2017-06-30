package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;

import java.util.List;

/**
 * Service for operations around the Assessor options for the competition type.
 */
public interface AssessorCountOptionService {

    @NotSecured(value = "Not secured here, because this is more like a reference data for the competition type.", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AssessorCountOptionResource>> findAllByCompetitionType(Long competitionTypeId);
}
