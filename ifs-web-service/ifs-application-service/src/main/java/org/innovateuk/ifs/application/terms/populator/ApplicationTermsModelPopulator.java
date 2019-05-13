package org.innovateuk.ifs.application.terms.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.terms.viewmodel.ApplicationTermsViewModel;
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

@Component
public class ApplicationTermsModelPopulator {

    private ApplicationRestService applicationRestService;
    private CompetitionRestService competitionRestService;
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
        this.userRestService = userRestService;
        this.organisationService = organisationService;
        this.questionStatusRestService = questionStatusRestService;
    }

    public ApplicationTermsViewModel populate(UserResource currentUser, long applicationId, long termsQuestionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> userApplicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();
        OrganisationResource organisation = organisationService.getOrganisationForUser(currentUser.getId(), userApplicationRoles).get();

        Optional<QuestionStatusResource> optionalQuestionStatus =
                questionStatusRestService.findByQuestionAndApplicationAndOrganisation(termsQuestionId, application.getId(), organisation.getId())
                        .getSuccess()
                        .stream()
                        .findFirst();

        boolean termsAccepted = optionalQuestionStatus
                .map(QuestionStatusResource::getMarkedAsComplete)
                .orElse(false);
        String termsAcceptedByName = optionalQuestionStatus
                .map(t -> t.getMarkedAsCompleteByUserId() == currentUser.getId() ? "you" : t.getMarkedAsCompleteByUserName())
                .orElse(null);
        ZonedDateTime termsAcceptedOn = optionalQuestionStatus
                .map(QuestionStatusResource::getMarkedAsCompleteOn)
                .orElse(null);

        return new ApplicationTermsViewModel(
                applicationId,
                termsQuestionId,
                competition.getTermsAndConditions().getTemplate(),
                application.isCollaborativeProject(),
                termsAccepted,
                termsAcceptedByName,
                termsAcceptedOn);
    }
}