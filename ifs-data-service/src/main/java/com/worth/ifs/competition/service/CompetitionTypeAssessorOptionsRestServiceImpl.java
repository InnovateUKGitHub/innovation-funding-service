package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.competition.resource.CompetitionTypeAssessorOptionResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionTypeAssessorOptionResourceListType;

/**
 * CompetitionsTypeAssessorOptionsRestServiceImpl is a utility for CRUD operations on {@link com.worth.ifs.competition.domain.CompetitionTypeAssessorOption}.
 */
@Service
public class CompetitionTypeAssessorOptionsRestServiceImpl extends BaseRestService implements CompetitionTypeAssessorOptionsRestService {

    @Override
    public RestResult<List<CompetitionTypeAssessorOptionResource>> findAllByCompetitionType(Long competitionTypeId) {
        return getWithRestResult("/competition-type-assessor-options/" + competitionTypeId, competitionTypeAssessorOptionResourceListType());
    }
}