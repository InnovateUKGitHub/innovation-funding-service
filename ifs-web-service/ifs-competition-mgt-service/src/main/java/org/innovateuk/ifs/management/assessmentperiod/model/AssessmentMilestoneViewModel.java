package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.management.competition.setup.core.form.MilestoneTime;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm.WITH_MIDDAY_TIME;
import static org.innovateuk.ifs.util.DateUtil.getNameOfDay;


public class AssessmentMilestoneViewModel {
    protected MilestoneTime time;
    protected MilestoneType milestoneType;
    protected ZonedDateTime date;


    public AssessmentMilestoneViewModel(MilestoneType milestoneType, ZonedDateTime dateTime) {
        this.milestoneType = milestoneType;
        this.date = dateTime;
    }

    public String getDayOfWeek() {
        return date == null ? "-" : getNameOfDay(date.getDayOfMonth(), date.getMonth().getValue(), date.getYear());
    }

    public MilestoneType getMilestoneType() {
        return milestoneType;
    }

    public String getMilestoneNameType() {
        return milestoneType.name();
    }


    public boolean isMiddayTime() {
        return WITH_MIDDAY_TIME.contains(milestoneType);
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public MilestoneTime getTime() {
        return time;
    }

    public boolean isPast(){
        return date != null && !date.isAfter(ZonedDateTime.now());
    }
}
