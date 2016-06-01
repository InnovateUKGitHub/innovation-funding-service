package com.worth.ifs.project.viewmodel;

import com.worth.ifs.controller.BindingResultTarget;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.util.List;

/**
 * View model that backs the Project Details - Start Date page
 */
public class ProjectDetailsStartDateViewModel {

    private String projectNumber;
    private String projectName;
    private long projectDurationInMonths;

    private ProjectDetailsStartDateViewModelForm form;

    public ProjectDetailsStartDateViewModel(ProjectResource project) {
        this.projectNumber = project.getFormattedId();
        this.projectName = project.getName();
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

    public static class ProjectDetailsStartDateViewModelForm implements BindingResultTarget {

        private LocalDate projectStartDate;
        private List<ObjectError> objectErrors;
        private BindingResult bindingResult;

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

        @Override
        public List<ObjectError> getObjectErrors() {
            return objectErrors;
        }

        @Override
        public void setObjectErrors(List<ObjectError> objectErrors) {
            this.objectErrors = objectErrors;
        }

        @Override
        public BindingResult getBindingResult() {
            return bindingResult;
        }

        @Override
        public void setBindingResult(BindingResult bindingResult) {
            this.bindingResult = bindingResult;
        }
    }
}
