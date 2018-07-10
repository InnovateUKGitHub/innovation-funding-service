package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;


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



    public ApplicationOverviewViewModel(Long applicationId,
                                        String applicationName,
                                        ApplicationState applicationState,
                                        boolean isApplicationSubmitted, boolean projectWithdrawn, CompetitionResource currentCompetition,
                                        OrganisationResource userOrganisation, Integer completedQuestionsPercentage, Long financeSectionId,
                                        ApplicationOverviewUserViewModel user, ApplicationOverviewAssignableViewModel assignable,
                                        ApplicationOverviewCompletedViewModel completed, ApplicationOverviewSectionViewModel section) {
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

    public boolean isFinanceSectionComplete(SectionResource section) {
        if (section.getId().equals(financeSectionId)) {
            return completed.getUserFinanceSectionCompleted();
        }
        return completed.getSectionsMarkedAsComplete().contains(financeSectionId);
    }
}
