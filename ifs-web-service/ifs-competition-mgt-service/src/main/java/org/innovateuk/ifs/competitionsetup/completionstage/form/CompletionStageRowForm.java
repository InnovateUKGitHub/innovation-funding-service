package org.innovateuk.ifs.competitionsetup.completionstage.form;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.core.form.GenericMilestoneRowForm;

import java.time.ZonedDateTime;

/**
 * TODO DW - comment
 */
public class CompletionStageRowForm extends GenericMilestoneRowForm {

    public CompletionStageRowForm() {

    }

    public CompletionStageRowForm(MilestoneType milestoneType, ZonedDateTime dateTime) {
        super(milestoneType, dateTime);
    }

    public CompletionStageRowForm(MilestoneType milestoneType, ZonedDateTime dateTime, boolean editable) {
        super(milestoneType, dateTime, editable);
    }
}
