package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.competition.resource.MilestoneResource;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 *
 */
public class MilestonesFormEntry extends CompetitionSetupForm {

    @Range(min = 1, max = 31, message = "MFE")
    public Integer day;
    @Range(min = 1, max = 12, message = "MFE")
    public Integer month;
    @Range(min = 1900, max = 9000, message = "MFE")
    public Integer year;
    public MilestoneResource.MilestoneName milestoneName;
    public String dayOfWeek;

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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public MilestoneResource.MilestoneName getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(MilestoneResource.MilestoneName milestoneName) {
        this.milestoneName = milestoneName;
    }
}
