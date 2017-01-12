package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessorCountOptionResourceListType;

/**
 * AssessorCountOptionsRestServiceImpl is a utility for CRUD operations on {@link AssessorCountOptionResource}.
 */
@Service
public class AssessorCountOptionsRestServiceImpl extends BaseRestService implements AssessorCountOptionsRestService {

    @Override
    public RestResult<List<AssessorCountOptionResource>> findAllByCompetitionType(Long competitionTypeId) {
        return getWithRestResult("/assessor-count-options/" + competitionTypeId, assessorCountOptionResourceListType());
    }
}
