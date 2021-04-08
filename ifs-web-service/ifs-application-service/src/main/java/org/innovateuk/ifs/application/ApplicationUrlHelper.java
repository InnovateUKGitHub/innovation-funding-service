package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.lang.String.format;

@Component
public class ApplicationUrlHelper {

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private QuestionRestService questionRestService;

    public Optional<String> getQuestionUrl(QuestionSetupType questionType, long questionId, long applicationId, long organisationId) {
        if (questionType != null) {
            switch (questionType) {
                case APPLICATION_DETAILS:
                    return Optional.of(format("/application/%d/form/question/%d/application-details", applicationId, questionId));
                case GRANT_AGREEMENT:
                    return Optional.of(format("/application/%d/form/question/%d/grant-agreement", applicationId, questionId));
                case GRANT_TRANSFER_DETAILS:
                    return Optional.of(format("/application/%d/form/question/%d/grant-transfer-details", applicationId, questionId));
                case APPLICATION_TEAM:
                    return Optional.of(format("/application/%d/form/question/%d/team", applicationId, questionId));
                case TERMS_AND_CONDITIONS:
                    return Optional.of(format("/application/%d/form/terms-and-conditions/organisation/%d/question/%d", applicationId, organisationId, questionId));
                case RESEARCH_CATEGORY:
                    return Optional.of(format("/application/%d/form/question/%d/research-category", applicationId, questionId));
                default:
                    // do nothing
            }
            if (questionType.isQuestionnaire()) {
                return Optional.of(format("/application/%d/form/organisation/%d/question/%d/questionnaire", applicationId, organisationId, questionId));
            }
            if (questionType.hasFormInputResponses()) {

                Boolean hasMultipleStatuses = questionRestService.findById(questionId).getSuccess().hasMultipleStatuses();

                if (Boolean.TRUE.equals(hasMultipleStatuses)) {
                    return Optional.of(format("/application/%d/form/organisation/%d/question/%d/generic", applicationId, organisationId, questionId));
                } else {
                    return Optional.of(format("/application/%d/form/question/%d/generic", applicationId, questionId));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<String> getSectionUrl(SectionType sectionType, long sectionId, long applicationId, long organisationId, long competitionId) {
        switch (sectionType) {
            case FUNDING_FINANCES:
                return Optional.of(String.format("/application/%d/form/your-funding/organisation/%d/section/%d", applicationId, organisationId, sectionId));
            case FEC_COSTS_FINANCES:
                return Optional.of(String.format("/application/%d/form/your-fec-model/organisation/%d/section/%d", applicationId, organisationId, sectionId));
            case PROJECT_COST_FINANCES:
                CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
                OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
                if (competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum())) {
                    return Optional.of(String.format("/application/%d/form/academic-costs/organisation/%d/section/%d", applicationId, organisationId, sectionId));
                } else if (competition.isH2020()) {
                    return Optional.of(String.format("/application/%d/form/horizon-2020-costs/organisation/%d/section/%d", applicationId, organisationId, sectionId));
                } else {
                    return Optional.of(String.format("/application/%d/form/your-project-costs/organisation/%d/section/%d", applicationId, organisationId, sectionId));
                }
            case PROJECT_LOCATION:
                return Optional.of(String.format("/application/%d/form/your-project-location/organisation/%d/section/%d",
                        applicationId, organisationId, sectionId));
            case ORGANISATION_FINANCES:
                return Optional.of(String.format("/application/%d/form/your-organisation/competition/%d/organisation/%d/section/%d",
                        applicationId, competitionId, organisationId, sectionId));
            case PAYMENT_MILESTONES:
                return Optional.of(String.format("/application/%d/form/procurement-milestones/organisation/%d/section/%d",
                        applicationId, organisationId, sectionId));
            case OVERVIEW_FINANCES:
                return Optional.of(String.format("/application/%d/form/finances-overview/section/%d",
                        applicationId, sectionId));
            case FINANCE:
                return Optional.of(String.format("/application/%d/form/your-finances/organisation/%d/section/%d",
                        applicationId, organisationId, sectionId));
            case GENERAL:
            case TERMS_AND_CONDITIONS:
                return Optional.of(String.format("/application/%d", applicationId));
            default:
                return Optional.empty();
        }
    }

}
