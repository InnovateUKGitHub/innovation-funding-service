package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.overview.ApplicationOverviewAssignableViewModel;
import org.innovateuk.ifs.application.viewmodel.overview.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.viewmodel.overview.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.overview.ApplicationOverviewUserViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * View model for the application overview
 */
public class ApplicationOverviewViewModel {
    private ApplicationResource currentApplication;
    private ProjectResource currentProject;
    private CompetitionResource currentCompetition;
    private OrganisationResource userOrganisation;

    private Integer completedQuestionsPercentage;
    private Long financeSectionId;

    private ApplicationOverviewUserViewModel user;
    private ApplicationOverviewAssignableViewModel assignable;
    private ApplicationOverviewCompletedViewModel completed;
    private ApplicationOverviewSectionViewModel section;

    private FileDetailsViewModel assessorFeedback;
    private List<ResearchCategoryResource> researchCategories;

    public ApplicationOverviewViewModel(ApplicationResource currentApplication, ProjectResource currentProject, CompetitionResource currentCompetition,
                                        OrganisationResource userOrganisation, Integer completedQuestionsPercentage, Long financeSectionId,
                                        ApplicationOverviewUserViewModel user, ApplicationOverviewAssignableViewModel assignable,
                                        ApplicationOverviewCompletedViewModel completed, ApplicationOverviewSectionViewModel section,
                                        FileDetailsViewModel assessorFeedback, List<ResearchCategoryResource> researchCategories) {
        this.currentApplication = currentApplication;
        this.currentProject = currentProject;
        this.currentCompetition = currentCompetition;
        this.userOrganisation = userOrganisation;
        this.completedQuestionsPercentage = completedQuestionsPercentage;
        this.financeSectionId = financeSectionId;
        this.user = user;
        this.assignable = assignable;
        this.completed = completed;
        this.section = section;
        this.assessorFeedback = assessorFeedback;
        this.researchCategories = researchCategories;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public ProjectResource getCurrentProject() {
        return currentProject;
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

    public FileDetailsViewModel getAssessorFeedback() {
        return assessorFeedback;
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
}
