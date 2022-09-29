package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

public interface CompetitionEoiEvidenceConfigRestService {

    RestResult<List<Long>> getValidFileTypeIdsForEoiEvidence(long competitionEoiEvidenceConfigId);
}
