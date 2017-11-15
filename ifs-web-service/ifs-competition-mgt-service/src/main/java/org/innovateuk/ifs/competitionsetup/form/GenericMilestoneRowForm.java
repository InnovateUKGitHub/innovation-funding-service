package org.innovateuk.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.Range;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

/**
 * Milestone Form Entry for the Milestones form.
 */
public class GenericMilestoneRowForm {
	protected static final Set<MilestoneType> WITH_TIME_TYPES = asSet(MilestoneType.SUBMISSION_DATE, MilestoneType.REGISTRATION_DATE);
    protected static final Set<MilestoneType> WITH_MIDDAY_TIME = asSet(MilestoneType.ASSESSOR_ACCEPTS, MilestoneType.ASSESSOR_DEADLINE);

    @Range(min=2000, max = 9999, message = "{validation.standard.date.format}")
    protected Integer year;
    protected Integer month;
    protected Integer day;

    protected MilestoneTime time;

    protected MilestoneType milestoneType;
    protected String dayOfWeek;
    protected ZonedDateTime date;
    protected boolean editable;

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

    public boolean isTimeOption() {
        return WITH_TIME_TYPES.contains(milestoneType);
    }

    public boolean isFirstMilestone() {
        return MilestoneType.OPEN_DATE.equals(milestoneType);
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public boolean isMiddayTime() {
        return WITH_MIDDAY_TIME.contains(milestoneType);
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public MilestoneTime getTime() {
        return time;
    }

    public void setTime(MilestoneTime time) {
        this.time = time;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isReadonly() {
        return !editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    protected String getNameOfDay() {
        String dayName =  getMilestoneDate(day, month, year);
        if(dayName == null) {
            dayOfWeek = "-";
        }
        else {
            try {
                dayOfWeek = dayName.substring(0, 1) + dayName.substring(1, 3).toLowerCase();
            } catch (Exception ex) {
            }
        }
        return dayOfWeek;
    }

    protected String getMilestoneDate (Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            try {
                return TimeZoneUtil.fromUkTimeZone(year, month, day).getDayOfWeek().name();
            } catch (DateTimeException ex) {
            }
        }

        return null;
    }

    public ZonedDateTime getMilestoneAsZonedDateTime() {

        if (day != null && month != null && year != null){
            if ( time != null && (isTimeOption() || isMiddayTime())) {
                return TimeZoneUtil.fromUkTimeZone(year, month, day, time.getHour());
            } else {
                return TimeZoneUtil.fromUkTimeZone(year, month, day);
            }
        } else {
            return null;
        }
    }
}
