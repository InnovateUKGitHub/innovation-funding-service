package org.innovateuk.ifs.application.forms.questions.applicationdetails.form;

import org.hibernate.validator.constraints.Range;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.constraints.FutureLocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Form for application details.
 */

@FieldRequiredIf(required = "previousApplicationNumber", argument = "resubmission", predicate = true, message = "{validation.application.previous.application.number.required}")
@FieldRequiredIf(required = "previousApplicationTitle", argument = "resubmission", predicate = true, message = "{validation.application.previous.application.title.required}")
public class ApplicationDetailsForm {

    @NotBlank(message = "{validation.project.name.must.not.be.empty}")
    private String name;

    @NotNull(message = "{validation.project.start.date.is.valid.date}")
    @FutureLocalDate(message = "{validation.project.start.date.not.in.future}")
    private LocalDate startDate;

    @NotNull(message = "{validation.project.duration.range.invalid}")
    @Range(min = 1, max = 36, message = "{validation.project.duration.range.invalid}")
    private Long durationInMonths;

    @NotNull(message = "{validation.application.must.indicate.resubmission.or.not}")
    private Boolean resubmission;

    private String previousApplicationNumber;

    private String previousApplicationTitle;

    public void populateForm(ApplicationResource application) {
        this.name = application.getName();
        this.durationInMonths = application.getDurationInMonths();
        this.resubmission = application.getResubmission();
        this.previousApplicationNumber = application.getPreviousApplicationNumber();
        this.previousApplicationTitle = application.getPreviousApplicationTitle();
        this.startDate = application.getStartDate();
    }

    public boolean isEmpty() {
        return null == name &&
                null == durationInMonths &&
                null == resubmission &&
                null == previousApplicationNumber &&
                null == previousApplicationTitle &&
                null == startDate;
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

}
