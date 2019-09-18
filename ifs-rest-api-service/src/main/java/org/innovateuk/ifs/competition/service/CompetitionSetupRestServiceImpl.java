package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
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
    public RestResult<Void> delete(long competitionId) {
        return deleteWithRestResult(competitionSetupRestURL + "/" + competitionId);
    }

    @Override
    public RestResult<Void> markSectionComplete(long competitionId, CompetitionSetupSection section) {
        return putWithRestResult(format("%s/section-status/complete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class);
    }

    @Override
    public RestResult<Void> markSectionIncomplete(long competitionId, CompetitionSetupSection section) {
        return putWithRestResult(format("%s/section-status/incomplete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class);
    }

    @Override
    public RestResult<Void> markSubSectionComplete(long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        return putWithRestResult(format("%s/subsection-status/complete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class);
    }

    @Override
    public RestResult<Void> markSubSectionIncomplete(long competitionId, CompetitionSetupSection parentSection, CompetitionSetupSubsection subsection) {
        return putWithRestResult(format("%s/subsection-status/incomplete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class);
    }

    @Override
    public RestResult<String> generateCompetitionCode(long competitionId, ZonedDateTime openingDate) {
        return postWithRestResult(format("%s/generate-competition-code/%s", competitionSetupRestURL, competitionId), openingDate, String.class);
    }

    @Override
    public RestResult<Void> initApplicationForm(long competitionId, long competitionTypeId) {
        return postWithRestResult(format("%s/%s/initialise-form/%s", competitionSetupRestURL, competitionId, competitionTypeId), Void.class);
    }

    @Override
    public RestResult<Void> markAsSetup(long competitionId) {
        return postWithRestResult(format("%s/%s/mark-as-setup", competitionSetupRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<Void> returnToSetup(long competitionId) {
        return postWithRestResult(format("%s/%s/return-to-setup", competitionSetupRestURL, competitionId), Void.class);
    }

    @Override
    public RestResult<CompetitionResource> createNonIfs() {
        return postWithRestResult(competitionSetupRestURL + "/non-ifs", CompetitionResource.class);
    }

    @Override
    public RestResult<Map<CompetitionSetupSection, Optional<Boolean>>> getSectionStatuses(long competitionId) {
        return getWithRestResult(format("%s/section-status/%s", competitionSetupRestURL, competitionId), competitionSetupSectionStatusMap());
    }

    @Override
    public RestResult<Map<CompetitionSetupSubsection, Optional<Boolean>>> getSubsectionStatuses(long competitionId) {
        return getWithRestResult(format("%s/subsection-status/%s", competitionSetupRestURL, competitionId), competitionSetupSubsectionStatusMap());
    }

    @Override
    public RestResult<FileEntryResource> uploadCompetitionTerms(long competitionId, String contentType, long contentLength, String originalFilename, byte[] file) {
        return postWithRestResult(
                format("%s/competition-terms?competitionId=%d&filename=%s", competitionSetupRestURL, competitionId, originalFilename),
                file,
                createFileUploadHeader(contentType,  contentLength),
                FileEntryResource.class
        );
    }

    @Override
    public RestResult<Void> deleteCompetitionTerms(long competitionId) {
        return deleteWithRestResult(format("%s/competition-terms?competitionId=%d", competitionSetupRestURL, competitionId));
    }
}