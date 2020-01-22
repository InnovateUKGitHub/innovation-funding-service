package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.time.LocalDate;

/**
 * The updatable form component of the Project Start Date page
 */
public class ProjectDetailsStartDateForm extends BaseBindingResultTarget {

    private LocalDate projectStartDate;

    // for spring form binding
    public ProjectDetailsStartDateForm() {
    }

    public ProjectDetailsStartDateForm(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

}
