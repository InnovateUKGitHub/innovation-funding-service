package org.innovateuk.ifs.application.domain;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;

import javax.persistence.*;
import java.util.*;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * UserStatistics defines a view on the user table for statistical information
 */
@Immutable
@Entity
@Table(name = "User")
public class AssessorStatistics {

    private static final Set<AssessmentStates> ASSESSOR_STATES = EnumSet.complementOf(EnumSet.of(REJECTED, WITHDRAWN));

    private static final Set<AssessmentStates> ACCEPTED_STATES = EnumSet.complementOf(EnumSet.of(PENDING, REJECTED, WITHDRAWN, CREATED));

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    private String firstName;
    private String lastName;

//    private Long competition;

    @ManyToOne()
    @JoinColumn(name="id", referencedColumnName = "user_id", updatable=false, insertable = false)
    private CompetitionParticipant competitionParticipant;
//
//    @OneToOne(mappedBy = "user", optional=false)
//    private ApplicationProcess applicationProcess;

    @OneToMany(mappedBy = "user")
    private List<ProcessRole> processRoles = new ArrayList<>();

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "participant")
    //joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name="participant_id")
    @ManyToMany
    @JoinTable(name="process_role",
            joinColumns = @JoinColumn(table="process_role", updatable=false, referencedColumnName = "id", name="id", insertable = false, nullable = false), // user.id  referencedColumnName = "id",
            inverseJoinColumns = @JoinColumn(table="process", updatable = false, name="id", referencedColumnName = "participant_id", insertable = false, nullable = false)) // process
//    @Where(clause = "process_type = 'Assessment'")
    private List<Assessment> assessments;

    public Long getId() {
        return id;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public Long getCompetition() {
        return competitionParticipant.getProcess().getId();
    }

//    public ApplicationState getApplicationState() {
//        return applicationProcess.getActivityState();
//    }

//    private Optional<ProcessRole> getLeadProcessRole() {
//        return this.processRoles.stream().filter(p -> UserRoleType.LEADAPPLICANT.getName().equals(p.getRole().getName())).findAny();
//    }
//
//    public Long getLeadOrganisationId() {
//        return getLeadProcessRole().map(ProcessRole::getOrganisationId).orElse(null);
//    }

//    public List<ProcessRole> getProcessRoles() {
//        return processRoles;
//    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    // TODO because everything here revolves around assessments I think we can move these onto a common base class
    public int getAssigned() {
        return assessments.stream().filter(a -> ASSESSOR_STATES.contains(a.getActivityState())).mapToInt(e -> 1).sum();
    }

    public int getAccepted() {
        return assessments.stream().filter(a -> ACCEPTED_STATES.contains(a.getActivityState())).mapToInt(e -> 1).sum();
    }

    public int getSubmitted() {
        return assessments.stream().filter(a -> a.isInState(SUBMITTED)).mapToInt(e -> 1).sum();
    }
}