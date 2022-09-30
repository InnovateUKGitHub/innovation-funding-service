package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

@Service
public class CompetitionEoiEvidenceConfigRestServiceImpl extends BaseRestService implements CompetitionEoiEvidenceConfigRestService {

    @Override
    public RestResult<CompetitionEoiEvidenceConfigResource> findByCompetitionId(long competitionId) {
        return getWithRestResult("/competition/" +  competitionId + "/eoi-evidence-config" , CompetitionEoiEvidenceConfigResource.class);
    }

    @Override
    public RestResult<List<Long>> getValidFileTypeIdsForEoiEvidence(long competitionEoiEvidenceConfigId){
        return getWithRestResultAnonymous("/competition-valid-file-type-ids/" + competitionEoiEvidenceConfigId, longsListType());
    }
}
