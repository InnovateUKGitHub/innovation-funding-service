package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

@Entity
@Table(name = "Application")
public class ApplicationStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long competition;

    @OneToMany(mappedBy = "application")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY)
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
    public String getLeadOrganisation() {
        return getLeadProcessRole().map(role -> role.getOrganisation().getName()).orElse(null);
    }

    public long getAssessors() {
        return assessments.stream().filter(a -> !a.isInState(REJECTED)).count();
    }

    public long getAccepted() {
        return assessments.stream().filter(a -> !(a.isInState(PENDING) || a.isInState(REJECTED))).count();
    }

    public long getSubmitted() {
        return assessments.stream().filter(a -> a.isInState(SUBMITTED)).count();
    }
}
