package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.util.List;

/**
 * A abstract {@link MilestoneBase} with child {@link MilestoneBase}s
 * @param <T> the type of the child Milestones
 */
public abstract class ParentMilestoneBase<T extends MilestoneBase> extends MilestoneBase {

    ParentMilestoneBase() {

    }

    protected ParentMilestoneBase(MilestoneType type, Competition competition) {
        super(type, competition);
    }

    public abstract List<T> getChildren();
}
