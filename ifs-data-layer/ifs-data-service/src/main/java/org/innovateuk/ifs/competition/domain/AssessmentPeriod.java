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
    @OrderBy("date ASC")
    private List<Milestone> children;

    public AssessmentPeriod() {
        // default constructor
    }

    public AssessmentPeriod(Competition competition) {
        super(MilestoneType.ASSESSMENT_PERIOD, competition);
    }

    public List<Milestone> getChildren() {
        return children;
    }

    public void setChildren(List<Milestone> children) {
        this.children = children;
        children.forEach(milestone -> milestone.setAssessmentPeriod(this));
    }
}
