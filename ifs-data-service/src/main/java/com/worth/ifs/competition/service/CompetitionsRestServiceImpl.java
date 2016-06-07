package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupCompletedSectionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * CompetitionsRestServiceImpl is a utility for CRUD operations on {@link Competition}.
 * This class connects to the {@link com.worth.ifs.competition.controller.CompetitionController}
 * through a REST call.
 */
@Service
public class CompetitionsRestServiceImpl extends BaseRestService implements CompetitionsRestService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(CompetitionsRestServiceImpl.class);
    private String competitionsRestURL = "/competition";

    @Override
    public RestResult<List<CompetitionResource>> getAll() {
        return getWithRestResult(competitionsRestURL + "/findAll", competitionResourceListType());
    }

    @Override
    public RestResult<CompetitionResource> getCompetitionById(Long competitionId) {
        return getWithRestResult(competitionsRestURL + "/" + competitionId, CompetitionResource.class);
    }

    @Override
    public RestResult<List<CompetitionSetupSectionResource>> getSetupSections() {
        return getWithRestResult(competitionsRestURL + "/sections/getAll", competitionSetupSectionResourceListType());
    }

    @Override
    public RestResult<List<CompetitionSetupCompletedSectionResource>> getCompletedSetupSections(Long competitionId) {
        return getWithRestResult(String.format("%s/sectionStatus/find/%d", competitionsRestURL, competitionId), competitionSetupCompletedSectionResourceListType());
    }

    @Override
    public RestResult<CompetitionResource> create() {
        return postWithRestResult(competitionsRestURL + "", CompetitionResource.class);
    }


    @Override
    public RestResult<List<CompetitionSetupCompletedSectionResource>> markSectionComplete(Long competitionId, Long sectionId) {
        return getWithRestResult(String.format("%s/sectionStatus/complete/%s/%s", competitionsRestURL, competitionId, sectionId), competitionSetupCompletedSectionResourceListType());
    }

    @Override
    public RestResult<List<CompetitionSetupCompletedSectionResource>> markSectionInComplete(Long competitionId, Long sectionId) {
        return getWithRestResult(String.format("%s/sectionStatus/incomplete/%s/%s", competitionsRestURL, competitionId, sectionId), competitionSetupCompletedSectionResourceListType());
    }

}