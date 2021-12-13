package org.innovateuk.ifs.project.setupcomplete.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.DateTimeException;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@ValidAggregatedDate(yearField="startDateYear", monthField="startDateMonth", dayField="startDateDay", message="{validation.standard.date.format}")
public class ProjectSetupCompleteForm {

    private Boolean successful;
    private boolean successfulConfirmation;
    private boolean unsuccessfulConfirmation;

    @Min(value = 2000, message = "{validation.standard.date.format}")
    @Max(value = 9999, message = "{validation.standard.date.format}")
    protected Integer startDateYear;
    @Min(value = 1, message = "{validation.standard.date.format}")
    @Max(value = 12, message = "{validation.standard.date.format}")
    protected Integer startDateMonth;
    @Min(value = 1, message = "{validation.standard.date.format}")
    @Max(value = 31, message = "{validation.standard.date.format}")
    protected Integer startDateDay;

    public LocalDate getStartDate() {
        if (startDateDay != null && startDateMonth != null && startDateYear != null){
            try {
                return LocalDate.of(startDateYear, startDateMonth, startDateDay);
            } catch(DateTimeException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessfulConfirmation() {
        return successfulConfirmation;
    }

    public void setSuccessfulConfirmation(boolean successfulConfirmation) {
        this.successfulConfirmation = successfulConfirmation;
    }

    public boolean isUnsuccessfulConfirmation() {
        return unsuccessfulConfirmation;
    }

    public void setUnsuccessfulConfirmation(boolean unsuccessfulConfirmation) {
        this.unsuccessfulConfirmation = unsuccessfulConfirmation;
    }

    public Integer getStartDateYear() {
        return startDateYear;
    }

    public void setStartDateYear(Integer startDateYear) {
        this.startDateYear = startDateYear;
    }

    public Integer getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(Integer startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public Integer getStartDateDay() {
        return startDateDay;
    }

    public void setStartDateDay(Integer startDateDay) {
        this.startDateDay = startDateDay;
    }
}
