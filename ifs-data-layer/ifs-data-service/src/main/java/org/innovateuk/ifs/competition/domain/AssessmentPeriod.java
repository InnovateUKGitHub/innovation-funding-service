package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import javax.persistence.*;
import java.util.List;

/**
 * An Assessment Period. {@link AssessmentPeriod}s contain a List of {@link Milestone}s.
 */
@Entity
@DiscriminatorValue("ASSESSMENT_PERIOD")
public class AssessmentPeriod extends ParentMilestoneBase<Milestone> {

    @OneToMany(mappedBy = "assessmentPeriod", cascade = CascadeType.ALL)
    @OrderBy("priority ASC")
    private List<Milestone> children;

    AssessmentPeriod() {
        // default constructor
    }

    protected AssessmentPeriod(MilestoneType type, Competition competition) {
        super(type, competition);
    }

    @Override
    public List<Milestone> getChildren() {
        return children;
    }

    public void setChildren(List<Milestone> children) {
        this.children = children;
        children.forEach(milestone -> milestone.setAssessmentPeriod(this));
    }
}
