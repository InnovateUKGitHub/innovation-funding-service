package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * ApplicationStatistics defines a view on the application table for statistical information
 */
@Entity
@Table(name = "Application")
public class ApplicationStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long competition;

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

    @JsonIgnore
    private Optional<ProcessRole> getLeadProcessRole() {
        return this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
    }

    @JsonIgnore
    public Long getLeadOrganisationId() {
        return getLeadProcessRole().map(ProcessRole::getOrganisationId).orElse(null);
    }

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<ProcessRole> processRoles) {
        this.processRoles = processRoles;
    }

    @JsonIgnore
    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public long getAssessors() {
        return assessments.stream().filter(a -> !a.isInState(REJECTED) && !a.isInState(WITHDRAWN)).count();
    }

    public long getAccepted() {
        return assessments.stream().filter(a -> !(a.isInState(PENDING) || a.isInState(REJECTED) || a.isInState(WITHDRAWN))).count();
    }

    public long getSubmitted() {
        return assessments.stream().filter(a -> a.isInState(SUBMITTED)).count();
    }
}
