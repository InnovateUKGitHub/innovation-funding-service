package org.innovateuk.ifs.application.forms.questions.researchcategory.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.populator.researchCategory.AbstractLeadOnlyModelPopulator;
import org.innovateuk.ifs.application.forms.questions.researchcategory.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.innovateuk.ifs.competition.service.CompetitionResearchCategoryRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryModelPopulator extends AbstractLeadOnlyModelPopulator {

    private CompetitionResearchCategoryRestService competitionResearchCategoryRestService;
    private FinanceService financeService;
    private UserService userService;
    private UserRestService userRestService;

    public ApplicationResearchCategoryModelPopulator(final ApplicantRestService applicantRestService,
                                                     final CompetitionResearchCategoryRestService competitionResearchCategoryRestService,
                                                     final FinanceService financeService,
                                                     final QuestionRestService questionRestService,
                                                     final UserService userService,
                                                     final UserRestService userRestService) {
        super(applicantRestService, questionRestService);
        this.competitionResearchCategoryRestService = competitionResearchCategoryRestService;
        this.financeService = financeService;
        this.userService = userService;
        this.userRestService = userRestService;
    }

    public ResearchCategoryViewModel populate(ApplicationResource applicationResource,
                                              long loggedInUserId,
                                              Long questionId) {
        boolean hasApplicationFinances = hasApplicationFinances(applicationResource);

        boolean userIsLeadApplicant = userService.isLeadApplicant(loggedInUserId, applicationResource);
        boolean complete = isComplete(applicationResource, loggedInUserId, QuestionSetupType.RESEARCH_CATEGORY);
        boolean allReadonly = !userIsLeadApplicant || complete;

        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);

        return new ResearchCategoryViewModel(applicationResource.getName(),
                applicationResource.getId(),
                applicationResource.getCompetitionName(),
                questionId,
                getResearchCategories(applicationResource.getCompetition()),
                hasApplicationFinances,
                researchCategoryName,
                isApplicationSubmitted(applicationResource) || !isCompetitionOpen(applicationResource),
                complete,
                userIsLeadApplicant,
                allReadonly,
                userIsLeadApplicant,
                getLeadApplicantName(applicationResource.getId()));
    }

    private boolean hasApplicationFinances(ApplicationResource applicationResource) {
        if (applicationResource.getResearchCategory() != null
                && applicationResource.getResearchCategory().getId() != null) {
            return financeService.getApplicationFinanceDetails
                    (applicationResource.getId())
                    .stream()
                    .anyMatch(applicationFinanceResource -> applicationFinanceResource.getOrganisationSize() != null);
        }
        return false;
    }

    private String getLeadApplicantName(long applicationId) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRole(applicationId);
        UserResource user = userRestService.retrieveUserById(leadApplicantProcessRole.getUser()).getSuccess();
        return user.getName();
    }

    private List<ResearchCategoryResource> getResearchCategories(Long competitionId) {
        List<CompetitionResearchCategoryLinkResource> competitionResearchCategories = competitionResearchCategoryRestService.findByCompetition(
                competitionId).handleSuccessOrFailure(failure -> emptyList(), success -> success);

        return competitionResearchCategories.stream()
                .map(CompetitionResearchCategoryLinkResource::getCategory)
                .collect(Collectors.toList());
    }
}
