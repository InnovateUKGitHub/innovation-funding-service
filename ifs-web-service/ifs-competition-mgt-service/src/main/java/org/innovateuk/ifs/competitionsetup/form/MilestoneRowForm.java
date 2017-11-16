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
        super(milestoneType, dateTime);
    }

    public MilestoneRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable) {
        super(milestoneType, dateTime, editable);
    }
}
