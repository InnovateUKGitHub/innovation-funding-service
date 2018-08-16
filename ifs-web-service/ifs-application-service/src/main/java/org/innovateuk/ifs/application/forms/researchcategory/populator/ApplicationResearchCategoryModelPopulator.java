package org.innovateuk.ifs.application.forms.researchcategory.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.populator.researchCategory.AbstractLeadOnlyModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.innovateuk.ifs.competition.service.CompetitionResearchCategoryRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
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

    public ApplicationResearchCategoryModelPopulator(final ApplicantRestService applicantRestService,
                                                     final CompetitionResearchCategoryRestService competitionResearchCategoryRestService,
                                                     final FinanceService financeService,
                                                     final QuestionRestService questionRestService,
                                                     final UserService userService) {
        super(applicantRestService, questionRestService);
        this.competitionResearchCategoryRestService = competitionResearchCategoryRestService;
        this.financeService = financeService;
        this.userService = userService;
    }

    public ResearchCategoryViewModel populate(ApplicationResource applicationResource,
                                              long loggedInUserId,
                                              Long questionId) {
        boolean hasApplicationFinances = hasApplicationFinances(applicationResource);

        boolean userIsLeadApplicant = userService.isLeadApplicant(loggedInUserId, applicationResource);
        boolean complete = isComplete(applicationResource, loggedInUserId);
        boolean allReadonly = !userIsLeadApplicant || complete;

        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);

        return new ResearchCategoryViewModel(applicationResource.getCompetitionName(),
                applicationResource.getId(),
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
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        UserResource user = userService.findById(leadApplicantProcessRole.getUser());
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
