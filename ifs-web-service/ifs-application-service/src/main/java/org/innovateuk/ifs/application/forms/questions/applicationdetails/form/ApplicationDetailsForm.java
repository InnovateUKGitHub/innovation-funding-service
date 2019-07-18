package org.innovateuk.ifs.application.forms.questions.applicationdetails.form;

import org.hibernate.validator.constraints.Range;
import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static java.lang.Boolean.TRUE;

/**
 * Form for application details.
 */

@FieldRequiredIf(required = "previousApplicationNumber", argument = "resubmission", predicate = true, message = "{validation.application.previous.application.number.required}")
@FieldRequiredIf(required = "previousApplicationTitle", argument = "resubmission", predicate = true, message = "{validation.application.previous.application.title.required}")
@FieldRequiredIf(required = "innovationAreaName", argument = "canSelectInnovationArea", predicate = true, message = "{validation.application.innovationarea.category.required}")
public class ApplicationDetailsForm {

    @NotBlank(message = "{validation.project.name.must.not.be.empty}")
    private String name;

    @NotNull(message = "{validation.project.start.date.is.valid.date}")
    @FutureLocalDate(message = "{validation.project.start.date.not.in.future}")
    private LocalDate startDate;

    @NotNull
    @Range(min = 1, max = 36, message = "{validation.project.duration.range.invalid}")
    private Long durationInMonths;

    @NotNull(message = "{validation.application.must.indicate.resubmission.or.not}")
    private Boolean resubmission;

    private String previousApplicationNumber;

    private String previousApplicationTitle;

    private boolean canSelectInnovationArea;

    private String innovationArea;

    private String innovationAreaName;

    public void populateForm(ApplicationDetailsViewModel viewModel) {
        this.name = viewModel.getApplication().getName();
        this.durationInMonths = viewModel.getApplication().getDurationInMonths();
        this.resubmission = viewModel.getApplication().getResubmission();
        this.previousApplicationNumber = viewModel.getApplication().getPreviousApplicationNumber();
        this.previousApplicationTitle = viewModel.getApplication().getPreviousApplicationTitle();
        this.startDate = viewModel.getApplication().getStartDate();
        this.canSelectInnovationArea = viewModel.getFormInputViewModel().isCanSelectInnovationArea() == TRUE;
        this.innovationArea = viewModel.getFormInputViewModel().getInnovationAreaText();
        this.innovationAreaName = viewModel.getFormInputViewModel().getSelectedInnovationAreaName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public void setResubmission(Boolean resubmission) {
        this.resubmission = resubmission;
    }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public void setPreviousApplicationNumber(String previousApplicationNumber) {
        this.previousApplicationNumber = previousApplicationNumber;
    }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    public void setPreviousApplicationTitle(String previousApplicationTitle) {
        this.previousApplicationTitle = previousApplicationTitle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isCanSelectInnovationArea() {
        return canSelectInnovationArea;
    }

    public void setCanSelectInnovationArea(boolean canSelectInnovationArea) {
        this.canSelectInnovationArea = canSelectInnovationArea;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(String innovationArea) {
        this.innovationArea = innovationArea;
    }

    public String getInnovationAreaName() {
        return innovationAreaName;
    }

    public void setInnovationAreaName(String innovationAreaName) {
        this.innovationAreaName = innovationAreaName;
    }
}
