package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.ZonedDateTime;


/**
 * Row model for the milestones on the in flight dashboard
 */
public class MilestonesRowViewModel {
    private MilestoneType milestoneType;
    private ZonedDateTime dateTime;
    private boolean passed;

    public MilestonesRowViewModel(MilestoneResource milestoneResource) {
        this.milestoneType = milestoneResource.getType();
        this.dateTime = TimeZoneUtil.toUkTimeZone(milestoneResource.getDate());
        if (this.dateTime != null) {
            this.passed = ZonedDateTime.now().isAfter(dateTime);
        } else {
            this.passed = false;
        }
    }

    public MilestoneType getMilestoneType() {
        return milestoneType;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public boolean isPassed() {
        return passed;
    }
}
