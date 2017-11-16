package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;

/**
 * Milestone Form Entry for the Milestones form which is allowed to be empty.
 */
@ValidAggregatedDate(yearField="year", monthField="month", dayField="day", message="{validation.standard.date.format}", emptyAllowed = true)
public class MilestoneOrEmptyRowForm extends GenericMilestoneRowForm {
    public MilestoneOrEmptyRowForm() {
    }

    public MilestoneOrEmptyRowForm(MilestoneType milestoneType, ZonedDateTime dateTime) {
        super(milestoneType, dateTime);
    }

    public MilestoneOrEmptyRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable) {
        super(milestoneType, dateTime, editable);
    }
}
