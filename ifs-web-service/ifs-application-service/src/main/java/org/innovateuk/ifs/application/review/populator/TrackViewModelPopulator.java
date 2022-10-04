package org.innovateuk.ifs.application.review.populator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.review.viewmodel.TrackViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.file.service.FileTypeRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.*;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;

@Slf4j
@Component
public class TrackViewModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Autowired
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private FileTypeRestService fileTypeRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Value("${ifs.early.metrics.url}")
    private String earlyMetricsUrl;

    public TrackViewModel populate(long applicationId, boolean canReopenApplication, UserResource userResource) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> processRoleResource = processRoleRestService.findProcessRole(applicationId).getSuccess();

        if (shouldDisplayEoiEvidence(competition, application, processRoleResource, userResource)) {

            Optional<ApplicationEoiEvidenceResponseResource> eoiEvidence = applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId).getSuccess();
            String eoiEvidenceFileName = "";
            if (eoiEvidence.isPresent() && eoiEvidence.get().getFileEntryId() != null) {
                eoiEvidenceFileName = fileEntryRestService.findOne(eoiEvidence.get().getFileEntryId()).getSuccess().getName();
            }
            List<String> getValidEoiEvidenceFileTypes = getValidEoiEvidenceFileTypes(competition.getId());
            List<String> combinedMediaDisplayNameWithExtensions = getMediaDisplayNameWithExtensions(getValidEoiEvidenceFileTypes);

            return new TrackViewModel(
                    competition,
                    application,
                    earlyMetricsUrl,
                    application.getCompletion(),
                    canReopenApplication,
                    getApplicationEoiEvidenceState(applicationId),
                    eoiEvidenceFileName,
                    combinedMediaDisplayNameWithExtensions,
                    competition.getCompetitionEoiEvidenceConfigResource(),
                    userFromLeadOrganisation(applicationId, userResource.getId()),
                    eoiEvidence.isEmpty());

        } else {

            return new TrackViewModel(
                    competition,
                    application,
                    earlyMetricsUrl,
                    application.getCompletion(),
                    canReopenApplication);
        }
    }

    private boolean shouldDisplayEoiEvidence(CompetitionResource competition, ApplicationResource application,
                                             List<ProcessRoleResource> processRoles, UserResource user) {

        return competition.isHorizonEuropeGuarantee()
                && competition.getCompetitionEoiEvidenceConfigResource() != null
                && competition.getCompetitionEoiEvidenceConfigResource().isEvidenceRequired()
                && !isPartner(application, processRoles, user);
    }

    private boolean isPartner(ApplicationResource application, List<ProcessRoleResource> processRoles, UserResource user) {
        return processRoles.stream()
                .anyMatch(pr -> pr.getUser().equals(user.getId())
                        && !pr.getOrganisationId().equals(application.getLeadOrganisationId()));
    }

    private List<String> getValidEoiEvidenceFileTypes(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (competition.getCompetitionEoiEvidenceConfigResource() != null) {
            return competitionEoiEvidenceConfigRestService.getValidFileTypeIdsForEoiEvidence(competition.getCompetitionEoiEvidenceConfigResource().getId())
                    .getSuccess()
                    .stream()
                    .map(validId -> fileTypeRestService.findOne(validId).getSuccess().getName()).collect(Collectors.toList());
        } else {
            return emptyList();
        }
    }

    private List<String> getMediaDisplayNameWithExtensions(List<String> mediaTypes) {
        List<String> validMediaTypes = new ArrayList<>();

        for (String mediaType : mediaTypes) {
            switch (mediaType) {
                case "PDF":
                    validMediaTypes.add(PDF.getDisplayName() + " (.pdf)");
                    break;
                case "Spreadsheets":
                    validMediaTypes.add(SPREADSHEET.getDisplayName() + " (.ods, .xls, .xlsx)");
                    break;
                case "Text":
                    validMediaTypes.add(DOCUMENT.getDisplayName() + " (.odt, .doc, .docx)");
                    break;
                default:
                    // do nothing
            }
        }
        return validMediaTypes;
    }

    private boolean userFromLeadOrganisation(long applicationId, long userId) {
        Long leadOrganisationId = processRoleRestService.findProcessRole(applicationId).getSuccess()
                .stream().filter(processRoleResource -> processRoleResource.getRole() == LEADAPPLICANT).findFirst().get().getOrganisationId();

        ProcessRoleResource userProcessRoleResource = processRoleRestService.findProcessRole(userId, applicationId).getSuccess();
        if (userProcessRoleResource.isLeadApplicant()) {
            return true;
        } else {
            return userProcessRoleResource.getOrganisationId().equals(leadOrganisationId);
        }
    }

    private ApplicationEoiEvidenceState getApplicationEoiEvidenceState(long applicationId) {
        return applicationEoiEvidenceResponseRestService.getApplicationEoiEvidenceState(applicationId).getSuccess().orElse(null);
    }

}
