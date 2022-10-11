package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;

import java.util.List;

public interface CompetitionEoiEvidenceConfigRestService {

    RestResult<CompetitionEoiEvidenceConfigResource> findByCompetitionId(long competitionId);

    RestResult<List<Long>> getValidFileTypeIdsForEoiEvidence(long competitionEoiEvidenceConfigId);
}
