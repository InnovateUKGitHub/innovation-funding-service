package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDateOrEmpty;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;

/**
 * Milestone Form Entry for the Milestones form which is allowed to be empty.
 */
@ValidAggregatedDateOrEmpty(yearField="year", monthField="month", dayField="day", message="{validation.standard.date.format}")
public class MilestoneOrEmptyRowForm extends GenericMilestoneRowForm {
    public MilestoneOrEmptyRowForm() {
    }

    public MilestoneOrEmptyRowForm(MilestoneType milestoneType, ZonedDateTime dateTime) {
        this(milestoneType, dateTime, true);
    }

    public MilestoneOrEmptyRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable) {
        this.setMilestoneType(milestoneType);
        this.editable = editable;
        if(dateTime != null) {
            this.setDay(dateTime.getDayOfMonth());
            this.setMonth(dateTime.getMonth().getValue());
            this.setYear(dateTime.getYear());
            this.setDate(dateTime);
            if (isTimeOption()) {
                this.setTime(MilestoneTime.fromZonedDateTime(dateTime));
            }
        } else if (isTimeOption() || isMiddayTime()) {
            this.setTime(MilestoneTime.TWELVE_PM);
        }
    }
}
