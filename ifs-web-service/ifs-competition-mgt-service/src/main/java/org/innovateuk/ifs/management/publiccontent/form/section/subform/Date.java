package org.innovateuk.ifs.management.publiccontent.form.section.subform;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.management.publiccontent.form.section.DatesForm;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * The repeating date that is being used in the {@link DatesForm}
 */
@ValidAggregatedDate(yearField="year", monthField="month", dayField="day", message="{validation.publiccontent.datesform.date.required}")
public class Date {
    private Long id;

    @Min(value = 1, message = "{validation.publiccontent.datesform.date.day}")
    @Max(value = 31, message = "{validation.publiccontent.datesform.date.day}")
    private Integer day;

    @Min(value = 1, message = "{validation.publiccontent.datesform.date.month}")
    @Max(value = 12, message = "{validation.publiccontent.datesform.date.month}")
    private Integer month;

    @Min(value = 2000, message = "{validation.publiccontent.datesform.date.year}")
    @Max(value = 9999, message = "{validation.publiccontent.datesform.date.year}")
    private Integer year;

    @NotBlank(message = "{validation.publiccontent.datesform.content.required}")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
