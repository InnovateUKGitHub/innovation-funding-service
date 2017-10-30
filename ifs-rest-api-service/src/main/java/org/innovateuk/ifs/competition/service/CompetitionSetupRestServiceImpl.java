package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSetupSectionStatusMap;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSetupSubsectionStatusMap;

/**
 * Implements {@link CompetitionSetupRestService}
 */
@Service
public class CompetitionSetupRestServiceImpl extends BaseRestService implements CompetitionSetupRestService {

    private String competitionSetupRestURL = "/competition/setup";

    @Override
    public RestResult<CompetitionResource> create() {
        return postWithRestResult(competitionSetupRestURL + "", CompetitionResource.class);
    }

    @Override
    public RestResult<Void> update(CompetitionResource competition) {
        return putWithRestResult(competitionSetupRestURL + "/" + competition.getId(), competition, Void.class);
    }

    @Override
    public RestResult<Void> updateCompetitionInitialDetails(CompetitionResource competition) {
        return putWithRestResult(competitionSetupRestURL + "/" + competition.getId() + "/update-competition-initial-details", competition, Void.class);
    }

    @Override
    public RestResult<Void> markSectionComplete(long competitionId, CompetitionSetupSection section) {
        return getWithRestResult(String.format("%s/sectionStatus/complete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class);
    }

    @Override
    public RestResult<Void> markSectionInComplete(long competitionId, CompetitionSetupSection section) {
        return getWithRestResult(String.format("%s/sectionStatus/incomplete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class);
    }

    @Override
    public RestResult<Void> markSubSectionComplete(long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        return getWithRestResult(String.format("%s/subsectionStatus/complete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class);
    }

    @Override
    public RestResult<Void> markSubSectionInComplete(long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        return getWithRestResult(String.format("%s/subsectionStatus/incomplete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class);
    }

    @Override
    public RestResult<String> generateCompetitionCode(long competitionId, ZonedDateTime openingDate) {
        return postWithRestResult(String.format("%s/generateCompetitionCode/%s", competitionSetupRestURL, competitionId), openingDate, String.class);
    }

    @Override
    public RestResult<Void> initApplicationForm(long competitionId, long competitionTypeId) {
        return postWithRestResult(String.format("%s/%s/initialise-form/%s", competitionSetupRestURL, competitionId, competitionTypeId), Void.class);
    }

    @Override
    public RestResult<Void> markAsSetup(long competitionId) {
        return postWithRestResult(String.format("%s/%s/mark-as-setup", competitionSetupRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<Void> returnToSetup(long competitionId) {
        return postWithRestResult(String.format("%s/%s/return-to-setup", competitionSetupRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<CompetitionResource> createNonIfs() {
        return postWithRestResult(competitionSetupRestURL + "/non-ifs", CompetitionResource.class);
    }

    @Override
    public RestResult<Map<CompetitionSetupSection, Optional<Boolean>>> getSectionStatuses(long competitionId) {
        return getWithRestResult(String.format("%s/sectionStatus/%s", competitionSetupRestURL, competitionId), competitionSetupSectionStatusMap());
    }

    @Override
    public RestResult<Map<CompetitionSetupSubsection, Optional<Boolean>>> getSubsectionStatuses(long competitionId) {
        return getWithRestResult(String.format("%s/subsectionStatus/%s", competitionSetupRestURL, competitionId), competitionSetupSubsectionStatusMap());
    }
}
