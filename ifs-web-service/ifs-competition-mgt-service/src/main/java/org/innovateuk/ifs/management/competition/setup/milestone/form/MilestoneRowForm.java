package org.innovateuk.ifs.management.competition.setup.milestone.form;

import org.innovateuk.ifs.commons.validation.constraints.ValidAggregatedDate;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;

import java.time.ZonedDateTime;

/**
 * Extending the default @{@link GenericMilestoneRowForm} for adding validation which does not allows an empty dates
 */
@ValidAggregatedDate(yearField="year", monthField="month", dayField="day", message="{validation.standard.date.format}")
public class MilestoneRowForm extends GenericMilestoneRowForm {
    public MilestoneRowForm() {

    }

    public MilestoneRowForm(MilestoneType milestoneType, ZonedDateTime dateTime) {
        super(milestoneType, dateTime);
    }

    public MilestoneRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable) {
        super(milestoneType, dateTime, editable);
    }

    public MilestoneRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable, Long assessmentPeriodId) {
        super(milestoneType, dateTime, editable, assessmentPeriodId);
    }
}
