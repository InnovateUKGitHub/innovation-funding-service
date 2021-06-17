package org.innovateuk.ifs.application.domain;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.util.*;

import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;

/**
 * ApplicationStatistics defines a view on the application table for statistical information
 */
@Immutable
@Entity
@Table(name = "Application")
public class ApplicationStatistics {

    private static final Set<AssessmentState> ASSESSOR_STATES = EnumSet.complementOf(EnumSet.of(REJECTED, WITHDRAWN));

    private static final Set<AssessmentState> ACCEPTED_STATES = EnumSet.complementOf(EnumSet.of(PENDING, REJECTED, WITHDRAWN, CREATED));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long competition;

    @OneToOne(mappedBy = "target", optional=false, fetch = FetchType.LAZY)
    private ApplicationProcess applicationProcess;

    @OneToMany(mappedBy = "applicationId")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assessment_period_id", referencedColumnName="id")
    private AssessmentPeriod assessmentPeriod;

    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    @Where(clause = "process_type = 'Assessment'")
    private List<Assessment> assessments;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCompetition() {
        return competition;
    }

    public ApplicationState getApplicationState() {
        return applicationProcess.getProcessState();
    }

    private Optional<ProcessRole> getLeadProcessRole() {
        return this.processRoles.stream().filter(p -> LEADAPPLICANT == p.getRole()).findAny();
    }

    public Long getLeadOrganisationId() {
        return getLeadProcessRole().map(ProcessRole::getOrganisationId).orElse(null);
    }

    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public int getAssessors() {
        return assessments.stream().filter(a -> ASSESSOR_STATES.contains(a.getProcessState())).mapToInt(e -> 1).sum();
    }

    public int getAccepted() {
        return assessments.stream().filter(a -> ACCEPTED_STATES.contains(a.getProcessState())).mapToInt(e -> 1).sum();
    }

    public int getSubmitted() {
        return assessments.stream().filter(a -> a.isInState(SUBMITTED)).mapToInt(e -> 1).sum();
    }

    public AssessmentPeriod getAssessmentPeriod() {
        return assessmentPeriod;
    }
}
