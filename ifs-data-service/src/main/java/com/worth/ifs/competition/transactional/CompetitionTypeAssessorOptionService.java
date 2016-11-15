package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;

import java.util.List;

/**
 * Service for operations around the Assessor options for the competition type.
 */
public interface CompetitionTypeAssessorOptionService {

    @NotSecured(value = "Not secured here, because this is more like a reference data for the competition type.", mustBeSecuredByOtherServices = false)
    ServiceResult<List<CompetitionTypeAssessorOptionResource>> findAllByCompetitionType(Long competitionTypeId);
}
