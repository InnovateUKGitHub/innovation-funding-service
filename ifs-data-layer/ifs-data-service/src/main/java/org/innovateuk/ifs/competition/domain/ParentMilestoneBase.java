package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;

/**
 * A abstract {@link MilestoneBase} with child {@link MilestoneBase}s
 * @param <T> the type of the child Categories
 */
public abstract class ParentMilestoneBase<T extends MilestoneBase> extends MilestoneBase {

    ParentMilestoneBase() {
        // default constructor
    }

    protected ParentMilestoneBase(MilestoneType type, Competition competition) {
        super(type, competition);
    }
}
