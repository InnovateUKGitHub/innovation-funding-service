package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;

import javax.persistence.*;
import java.util.*;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * ApplicationStatistics defines a view on the application table for statistical information
 */
@Immutable
@Entity
@Table(name = "Application")
public class ApplicationStatistics {

    private static final Set<AssessmentStates> ASSESSOR_STATES = EnumSet.complementOf(EnumSet.of(REJECTED, WITHDRAWN));

    private static final Set<AssessmentStates> ACCEPTED_STATES = EnumSet.complementOf(EnumSet.of(PENDING, REJECTED, WITHDRAWN, CREATED));

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationStatusId", referencedColumnName = "id")
    private ApplicationStatus applicationStatus;

    @OneToMany(mappedBy = "applicationId")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
    @Where(clause = "process_type = 'Assessment'")
    // TODO 7668 Issue with retrieval may be caused by this class noting being an Application
    private List<Assessment> assessments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    private Optional<ProcessRole> getLeadProcessRole() {
        return this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
    }

    public Long getLeadOrganisationId() {
        return getLeadProcessRole().map(ProcessRole::getOrganisationId).orElse(null);
    }

    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public int getAssessors() {
        return assessments.stream().filter(a -> ASSESSOR_STATES.contains(a.getActivityState())).mapToInt(e -> 1).sum();
    }

    public int getAccepted() {
        return assessments.stream().filter(a -> ACCEPTED_STATES.contains(a.getActivityState())).mapToInt(e -> 1).sum();
    }

    public int getSubmitted() {
        return assessments.stream().filter(a -> a.isInState(SUBMITTED)).mapToInt(e -> 1).sum();
    }
}
