package org.innovateuk.ifs.competitionsetup.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Range;
import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

/**
 * Milestone Form Entry for the Milestones form.
 */
@ValidAggregatedDate(yearField="year", monthField="month", dayField="day", message="{validation.standard.date.format}")
public class MilestoneRowForm {
	private static final Log LOG = LogFactory.getLog(MilestoneRowForm.class);
	private static final Set<MilestoneType> WITH_TIME_TYPES = asSet(MilestoneType.SUBMISSION_DATE);
    private static final Set<MilestoneType> WITH_MIDDAY_TIME = asSet(MilestoneType.ASSESSOR_ACCEPTS, MilestoneType.ASSESSOR_DEADLINE);

    @Range(min=2000, max = 9999, message = "{validation.standard.date.format}")
    private Integer year;
    private Integer month;
    private Integer day;

    private MilestoneTime time;

    private MilestoneType milestoneType;
    private String dayOfWeek;
    private LocalDateTime date;

    public MilestoneRowForm() {

    }

    public MilestoneRowForm(MilestoneType milestoneType, LocalDateTime dateTime) {
        this.setMilestoneType(milestoneType);
        if(dateTime != null) {
            this.setDay(dateTime.getDayOfMonth());
            this.setMonth(dateTime.getMonth().getValue());
            this.setYear(dateTime.getYear());
            this.setDate(dateTime);
            if (isTimeOption()) {
                this.setTime(MilestoneTime.fromLocalDateTime(dateTime));
            }
        } else if (isTimeOption() || isMiddayTime()) {
            this.setTime(MilestoneTime.TWELVE_PM);
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

    public boolean editableForCompetition(CompetitionResource competitionResource) {
        return competitionResource.isNonIfs() ||
                !(competitionResource.isSetupAndLive() && date.isBefore(LocalDateTime.now()));
    }

    public boolean isTimeOption() {
        return WITH_TIME_TYPES.contains(milestoneType);
    }

    public boolean isMiddayTime() {
        return WITH_MIDDAY_TIME.contains(milestoneType);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public MilestoneTime getTime() {
        return time;
    }

    public void setTime(MilestoneTime time) {
        this.time = time;
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

    public LocalDateTime getMilestoneAsDateTime() {
        if (day != null && month != null && year != null){
            if (time != null && (isTimeOption() || isMiddayTime())) {
                return LocalDateTime.of(year, month, day, time.getHour(), 0);
            } else {
                return LocalDateTime.of(year, month, day, 0, 0);
            }
        } else {
            return null;
        }
    }
}
