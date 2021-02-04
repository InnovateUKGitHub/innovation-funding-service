package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;

@Component
public class ApplicationTermsModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    public ApplicationTermsViewModel populate(UserResource currentUser,
                                              long applicationId,
                                              long termsQuestionId,
                                              Long organisationId,
                                              boolean readOnly) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        boolean additionalTerms = competition.getCompetitionTerms() != null;

        if (organisationId != null && !readOnly && !competition.isExpressionOfInterest())  {
            // is the current user a member of this application?
            if (competition.isOpen() && application.isOpen()) {
                Optional<QuestionStatusResource> optionalMarkedAsCompleteQuestionStatus =
                        questionStatusRestService.getMarkedAsCompleteByQuestionApplicationAndOrganisation(
                                termsQuestionId, applicationId, organisationId).getSuccess();

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
                        getTermsAndConditionsTemplate(competition, applicationId, organisationId),
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
                getTermsAndConditionsTemplate(competition, applicationId, organisationId),
                application.isCollaborativeProject(),
                isAllOrganisationsTermsAccepted(applicationId, competition.getId()), additionalTerms);
    }

    private String getTermsAndConditionsTemplate(CompetitionResource competition, long applicationId, Long organisationId) {
        if (!competition.isExpressionOfInterest() && organisationId != null) {
            ApplicationFinanceResource applicationFinanceResource = applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();
            if (Boolean.TRUE.equals(applicationFinanceResource.getNorthernIrelandDeclaration())) {
                return competition.getOtherFundingRulesTermsAndConditions().getTemplate();
            }
        }
        return competition.getTermsAndConditions().getTemplate();
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