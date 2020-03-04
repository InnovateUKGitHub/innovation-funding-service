package org.innovateuk.ifs.application;

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

    //TODO IFS-5889 missing types RESEARCH_CATEGORY.
    public static Optional<String> getQuestionUrl(QuestionSetupType questionType, long questionId, long applicationId) {
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
                    return Optional.of(format("/application/%d/form/question/%d/terms-and-conditions", applicationId, questionId));
                case RESEARCH_CATEGORY:
                    return Optional.of(format("/application/%d/form/question/%d/research-category", applicationId, questionId));
            }
            if (questionType.hasFormInputResponses()) {
                return Optional.of(format("/application/%d/form/question/%d/generic", applicationId, questionId));
            }
        }
        return Optional.empty();
    }

    public Optional<String> getSectionUrl(SectionType sectionType, long sectionId, long applicationId, long organisationId, long competitionId) {
        switch (sectionType) {
            case FUNDING_FINANCES:
                return Optional.of(String.format("/application/%d/form/your-funding/organisation/%d/section/%d", applicationId, organisationId, sectionId));
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
            case OVERVIEW_FINANCES:
                return Optional.of(String.format("/application/%d/form/finances-overview/section/%d",
                        applicationId, sectionId));
            case FINANCE:
                return Optional.of(String.format("/application/%d/form/your-finances/organisation/%d/section/%d",
                        applicationId, organisationId, sectionId));
            case GENERAL:
            case TERMS_AND_CONDITIONS:
                return Optional.of(String.format("/application/%d", applicationId));
        }
        return Optional.empty();
    }

}
