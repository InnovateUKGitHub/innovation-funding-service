package org.innovateuk.ifs.management.nonifs.form;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;

import java.time.ZonedDateTime;

/**
 * Extending the default @{@link GenericMilestoneRowForm} for adding validation which allows an empty date
 */
@ValidAggregatedDate(yearField="year", monthField="month", dayField="day", message="{validation.standard.date.format}", required = false)
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
