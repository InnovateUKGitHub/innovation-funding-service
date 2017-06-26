package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.assessment.resource.AssessmentStates;

import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

public class AssessorStatisticsResource {

    private static final Set<AssessmentStates> ASSESSOR_STATES = EnumSet.complementOf(EnumSet.of(REJECTED, WITHDRAWN));

    private static final Set<AssessmentStates> ACCEPTED_STATES = EnumSet.complementOf(EnumSet.of(PENDING, REJECTED, WITHDRAWN, CREATED));

    private long id;
    private String name;
    private String skillArea;
    private int accepted;
    private int assigned;
    private int submitted;

    AssessorStatisticsResource() {};

    public AssessorStatisticsResource(long id, String name) {
        this.id = id;
        this.name = "foo bar";
        this.skillArea = "innovation area";
        this.accepted = 3;
        this.assigned = 5;
        this.submitted = 7;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkillArea() {
        return skillArea;
    }
    public int getAssigned() {
        return assigned;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getSubmitted() {
        return submitted;
    }

    public int getTotal() {
        return accepted + assigned + submitted;
    }
}