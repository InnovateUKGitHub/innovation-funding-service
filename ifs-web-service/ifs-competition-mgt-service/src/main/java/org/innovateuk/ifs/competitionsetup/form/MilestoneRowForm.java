package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;

/**
 * Milestone Form Entry for the Milestones form.
 */
@ValidAggregatedDate(yearField="year", monthField="month", dayField="day", message="{validation.standard.date.format}")
public class MilestoneRowForm extends GenericMilestoneRowForm {
    public MilestoneRowForm() {
    }

    public MilestoneRowForm(MilestoneType milestoneType, ZonedDateTime dateTime) {
        this(milestoneType, dateTime, true);
    }

    public MilestoneRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable) {
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
