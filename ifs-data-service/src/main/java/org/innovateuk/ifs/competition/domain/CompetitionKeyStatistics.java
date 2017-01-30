package org.innovateuk.ifs.competition.domain;

import org.hibernate.annotations.Where;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;

/**
 * Entity for key statistics for {@link Competition}s
 */
/*
@Entity
@Table(name = "competition")
*/
public class CompetitionKeyStatistics {
/*
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Integer assessorCount;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY)
    private List<CompetitionInvite> competitionInvites;

    @OneToMany(mappedBy = "competition", fetch = FetchType.LAZY)
    @Where(clause = "competition_role = 'ASSESSOR'")
    private List<CompetitionParticipant> competitionParticipants;

    @OneToMany(mappedBy = "competition")
    private List<Application> applications;

    public long getAssessorsInvited() {
        return competitionInvites.stream().filter(ci -> ci.getStatus() != CREATED).count();
    }

    public long getAssessorsAccepted() {
        return competitionParticipants.stream().filter(cp -> cp.getStatus() == ACCEPTED).count();
    }

    public long getDeclined() {
        return competitionParticipants.stream().filter(cp -> cp.getStatus() == REJECTED).count();
    }

    public long getInviteList() {
        return competitionInvites.stream().filter(ci -> ci.getStatus() == CREATED).count();
    }

    public Integer getAssessorCount() {
        return assessorCount;
    }

    public long getApplicationsStarted() {
        BigDecimal limit = new BigDecimal(50L);
        return applications.stream()
                .filter(ap -> CREATED_AND_OPEN_STATUS_IDS.contains(ap.getApplicationStatus().getId())
                        && ap.getCompletion().compareTo(limit) < 0).count();
    }

    public long getApplicationsPastHalf() {
        BigDecimal limit = new BigDecimal(50L);
        return applications.stream()
                .filter(ap -> !SUBMITTED_STATUS_IDS.contains(ap.getApplicationStatus().getId())
                        && ap.getCompletion().compareTo(limit) > 0).count();
    }

    public long getApplicationsSubmitted() {
        return applications.stream()
                .filter(ap -> SUBMITTED_STATUS_IDS.contains(ap.getApplicationStatus().getId())).count();
    }
    */
}
