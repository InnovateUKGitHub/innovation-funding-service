package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.time.LocalDateTime;


/**
 * Row model for the milestones on the in flight dashboard
 */
public class MilestonesRowViewModel {
    private MilestoneType milestoneType;
    private LocalDateTime dateTime;
    private boolean passed;

    public MilestonesRowViewModel(MilestoneResource milestoneResource) {
        this.milestoneType = milestoneResource.getType();
        this.dateTime = TimeZoneUtil.toBritishSummerTime(milestoneResource.getDate());
        if (this.dateTime != null) {
            this.passed = LocalDateTime.now().isAfter(dateTime);
        } else {
            this.passed = false;
        }
    }

    public MilestoneType getMilestoneType() {
        return milestoneType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isPassed() {
        return passed;
    }
}
