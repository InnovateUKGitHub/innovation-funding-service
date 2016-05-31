package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;

import java.time.LocalDate;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel {

    private String projectNumber;
    private String projectName;
    private long projectDurationInMonths;

    private ProjectDetailsStartDateViewModelForm form;

    public ProjectDetailsStartDateViewModel(ApplicationResource project) {
        this.projectNumber = project.getFormattedId();
        this.projectName = project.getApplicationDisplayName();
        this.projectDurationInMonths = project.getDurationInMonths();
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getProjectDurationInMonths() {
        return projectDurationInMonths;
    }

    public ProjectDetailsStartDateViewModelForm getForm() {
        return form;
    }

    public static class ProjectDetailsStartDateViewModelForm {

        private LocalDate projectStartDate;

        // for spring form binding
        public ProjectDetailsStartDateViewModelForm() {
        }

        public ProjectDetailsStartDateViewModelForm(LocalDate projectStartDate) {
            this.projectStartDate = projectStartDate;
        }

        public LocalDate getProjectStartDate() {
            return projectStartDate;
        }

        public void setProjectStartDate(LocalDate projectStartDate) {
            this.projectStartDate = projectStartDate;
        }
    }
}
