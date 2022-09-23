package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

@Service
public class CompetitionEoiEvidenceConfigRestServiceImpl extends BaseRestService implements CompetitionEoiEvidenceConfigRestService {

    @Override
    public RestResult<List<Long>> getValidFileTypesIdsForEoiEvidence(long competitionEoiEvidenceConfigId){
        return getWithRestResultAnonymous("/competition-valid-file-type-ids/" + competitionEoiEvidenceConfigId, longsListType());
    }

}
