package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;

@Component
public class ApplicationTermsModelPopulator {

    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
    private SectionService sectionService;
    private UserRestService userRestService;
    private OrganisationService organisationService;
    private QuestionStatusRestService questionStatusRestService;

    public ApplicationTermsModelPopulator(ApplicationRestService applicationRestService,
                                          CompetitionRestService competitionRestService,
                                          SectionService sectionService,
                                          UserRestService userRestService,
                                          OrganisationService organisationService,
                                          QuestionStatusRestService questionStatusRestService) {
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.sectionService = sectionService;
        this.userRestService = userRestService;
        this.organisationService = organisationService;
        this.questionStatusRestService = questionStatusRestService;
    }

    public ApplicationTermsViewModel populate(UserResource currentUser,
                                              long applicationId,
                                              long termsQuestionId,
                                              boolean readOnly) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        boolean additionalTerms = competition.getCompetitionTerms() != null;

        if (!readOnly && !competition.isExpressionOfInterest())  {
            // is the current user a member of this application?
            Optional<OrganisationResource> organisation = organisationService.getOrganisationForUser(currentUser.getId(), userApplicationRoles);
            if (organisation.isPresent() && competition.isOpen() && application.isOpen()) {
                Optional<QuestionStatusResource> optionalMarkedAsCompleteQuestionStatus =
                        questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(
                                termsQuestionId, applicationId, organisation.get().getId()).getSuccess();

                boolean termsAccepted = optionalMarkedAsCompleteQuestionStatus
                        .map(QuestionStatusResource::getMarkedAsComplete)
                        .orElse(false);

                String termsAcceptedByName = optionalMarkedAsCompleteQuestionStatus
                        .map(t -> t.getMarkedAsCompleteByUserId() == currentUser.getId() ? "you" : t.getMarkedAsCompleteByUserName())
                        .orElse(null);
                ZonedDateTime termsAcceptedOn = optionalMarkedAsCompleteQuestionStatus
                        .map(QuestionStatusResource::getMarkedAsCompleteOn)
                        .orElse(null);

                return new ApplicationTermsViewModel(
                        applicationId,
                        competition.getName(),
                        competition.getId(),
                        termsQuestionId,
                        competition.getTermsAndConditions().getTemplate(),
                        application.isCollaborativeProject(),
                        termsAccepted,
                        termsAcceptedByName,
                        termsAcceptedOn,
                        isAllOrganisationsTermsAccepted(applicationId, competition.getId()),
                        additionalTerms);
            }
        }

        return new ApplicationTermsViewModel(
                applicationId,
                competition.getName(),
                competition.getId(),
                termsQuestionId,
                competition.getTermsAndConditions().getTemplate(),
                application.isCollaborativeProject(),
                isAllOrganisationsTermsAccepted(applicationId, competition.getId()), additionalTerms);
    }

    private boolean isAllOrganisationsTermsAccepted(long applicationId, long competitionId) {
        long termsAndConditionsSectionId =
                sectionService.getSectionsForCompetitionByType(competitionId, TERMS_AND_CONDITIONS).get(0).getId();

        return sectionService.getCompletedSectionsByOrganisation(applicationId)
                .values()
                .stream()
                .allMatch(completedSections -> completedSections.contains(termsAndConditionsSectionId));
    }
}