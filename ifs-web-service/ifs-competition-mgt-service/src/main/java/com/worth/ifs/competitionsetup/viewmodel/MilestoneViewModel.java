package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.MilestoneType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Range;

import java.time.DateTimeException;
import java.time.LocalDateTime;

/**
 * Milestone Form Entry for the Milestones form.
 */
public class MilestoneViewModel {
    @Range(min = 1, max = 31)
    private Integer day;
    @Range(min = 1, max = 12)
    private Integer month;
    @Range(min = 2016, max = 9000)
    private Integer year;
    private MilestoneType milestoneType;
    private String dayOfWeek;

    private static final Log LOG = LogFactory.getLog(MilestoneViewModel.class);

    public MilestoneViewModel(MilestoneType milestoneType, LocalDateTime dateTime) {
        this.setMilestoneType(milestoneType);
        if(dateTime != null) {
            this.setDay(dateTime.getDayOfMonth());
            this.setMonth(dateTime.getMonth().getValue());
            this.setYear(dateTime.getYear());
        }
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

    public String getDayOfWeek() {
        return getNameOfDay();
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public MilestoneType getMilestoneType() {
        return milestoneType;
    }

    public void setMilestoneType(MilestoneType milestoneType) {
        this.milestoneType = milestoneType;
    }

    public String getMilestoneNameType() {
        return milestoneType.name();
    }

    private String getNameOfDay() {
        String dayName =  getMilestoneDate(day, month, year);
        if(dayName == null) {
            dayOfWeek = "-";
        }
        else {
            try {
                dayOfWeek = dayName.substring(0, 1) + dayName.substring(1, 3).toLowerCase();
            } catch (Exception ex) {
                LOG.error(ex);
            }
        }
        return dayOfWeek;
    }

    private String getMilestoneDate (Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            try {
                return LocalDateTime.of(year, month, day, 0, 0).getDayOfWeek().name();
            } catch (DateTimeException ex) {
                LOG.error("Invalid date");
                LOG.debug(ex.getMessage());
            }
        }

        return null;
    }
}
