package com.worth.ifs.project.viewmodel;

import com.worth.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.util.List;

/**
 * The updatable form component of the Project Start Date page
 */
public class ProjectDetailsStartDateForm implements BindingResultTarget {

    private LocalDate projectStartDate;
    private List<ObjectError> objectErrors;
    private BindingResult bindingResult;

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
