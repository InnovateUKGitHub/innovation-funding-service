package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * An Assessment Period. {@link AssessmentPeriod}s contain a List of {@link Milestone}s.
 */
@Entity
@DiscriminatorValue("ASSESSMENT_PERIOD")
public class AssessmentPeriod extends Milestone {

    @OneToMany(mappedBy = "assessmentPeriod", cascade = CascadeType.ALL)
    @OrderBy("priority ASC")
    private List<Milestone> children;

    public AssessmentPeriod() {
        // default constructor
    }

    protected AssessmentPeriod(MilestoneType type, Competition competition) {
        super(type, competition);
    }

    public List<Milestone> getChildren() {
        return children;
    }

    public void setChildren(List<Milestone> children) {
        this.children = children;
        children.forEach(milestone -> milestone.setAssessmentPeriod(this));
    }
}
