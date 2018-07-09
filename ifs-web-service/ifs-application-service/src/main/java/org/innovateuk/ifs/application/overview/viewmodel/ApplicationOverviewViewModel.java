package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.List;

/**
 * View model for the application overview
 */
public class ApplicationOverviewViewModel {

    private Long applicationId;
    private String applicationName;
    private ApplicationState applicationState;
    private boolean isApplicationSubmitted;
    private boolean projectWithdrawn;
    private CompetitionResource currentCompetition;
    private OrganisationResource userOrganisation;
    private Integer completedQuestionsPercentage;
    private Long financeSectionId;
    private ApplicationOverviewUserViewModel user;
    private ApplicationOverviewAssignableViewModel assignable;
    private ApplicationOverviewCompletedViewModel completed;
    private ApplicationOverviewSectionViewModel section;
    private List<ResearchCategoryResource> researchCategories;
    private ApplicationForm form;

    public ApplicationOverviewViewModel(Long applicationId,
                                        String applicationName,
                                        ApplicationState applicationState,
                                        boolean isApplicationSubmitted,
                                        boolean projectWithdrawn,
                                        CompetitionResource currentCompetition,
                                        OrganisationResource userOrganisation,
                                        Integer completedQuestionsPercentage,
                                        Long financeSectionId,
                                        ApplicationOverviewUserViewModel user,
                                        ApplicationOverviewAssignableViewModel assignable,
                                        ApplicationOverviewCompletedViewModel completed,
                                        ApplicationOverviewSectionViewModel section,
                                        List<ResearchCategoryResource> researchCategories,
                                        ApplicationForm form) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationState = applicationState;
        this.isApplicationSubmitted = isApplicationSubmitted;
        this.projectWithdrawn = projectWithdrawn;
        this.currentCompetition = currentCompetition;
        this.userOrganisation = userOrganisation;
        this.completedQuestionsPercentage = completedQuestionsPercentage;
        this.financeSectionId = financeSectionId;
        this.user = user;
        this.assignable = assignable;
        this.completed = completed;
        this.section = section;
        this.researchCategories = researchCategories;
        this.form = form;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public boolean isApplicationSubmitted() {
        return isApplicationSubmitted;
    }

    public boolean isProjectWithdrawn() {
        return projectWithdrawn;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public OrganisationResource getUserOrganisation() {
        return userOrganisation;
    }

    public Integer getCompletedQuestionsPercentage() {
        return completedQuestionsPercentage;
    }

    public List<ResearchCategoryResource> getResearchCategories() {
        return researchCategories;
    }

    public Long getFinanceSectionId() {
        return financeSectionId;
    }

    public ApplicationOverviewUserViewModel getUser() {
        return user;
    }

    public ApplicationOverviewAssignableViewModel getAssignable() {
        return assignable;
    }

    public ApplicationOverviewCompletedViewModel getCompleted() {
        return completed;
    }

    public ApplicationOverviewSectionViewModel getSection() {
        return section;
    }

    public ApplicationForm getForm() {
        return form;
    }
}
