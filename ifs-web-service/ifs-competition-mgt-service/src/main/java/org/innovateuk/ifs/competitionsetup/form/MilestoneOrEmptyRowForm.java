package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;

/**
 * Extending the default @{@link GenericMilestoneRowForm} for adding validation which allows an empty date
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
