package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;

@Entity
@DiscriminatorValue("COMPETITION")
public class CompetitionInvite extends Invite<Competition, CompetitionInvite> implements Serializable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="innovation_category_id", referencedColumnName = "id")
    private InnovationArea innovationArea;

    public CompetitionInvite() {
        // no-arg constructor
    }

    /**
     * A new User invited to a Competition.
     */
    public CompetitionInvite(final String name, final String email, final String hash, final Competition competition, final InnovationArea innovationArea) {
        super(name, email, hash, CREATED);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }
        if (innovationArea == null) {
            throw new NullPointerException("innovationArea cannot be null");
        }
        this.competition = competition;
        this.innovationArea = innovationArea;
    }

    /**
     * An existing User invited to a Competition.
     */
    public CompetitionInvite(final User existingUser, final String hash, Competition competition) {
        super(existingUser.getName(), existingUser.getEmail(), hash, CREATED);
        if (competition == null) {
            throw new NullPointerException("competition cannot be null");
        }
        this.competition = competition;
    }

    @Override
    public  Competition getTarget() {
        return competition;
    }

    @Override
    public void setTarget(Competition competition) {
        this.competition = competition;
    }

    @Deprecated // TODO workaround for mapstruct see: https://devops.innovateuk.org/issue-tracking/browse/INFUND-4585
    public InnovationArea getInnovationAreaOrNull() {
        return innovationArea;
    }

    public boolean isNewAssessorInvite() {
        return innovationArea != null;
    }

    public CompetitionInvite ifNewAssessorInvite(Consumer<CompetitionInvite> consumer) {
        if (isNewAssessorInvite()) {
            consumer.accept(this);
        }
        return this;
    }

    public InnovationArea getInnovationArea() {
        if (!isNewAssessorInvite()) {
            throw new IllegalStateException(("Cannot get InnovationArea for an existing assessor CompetitionInvite"));
        }
        return requireNonNull(innovationArea, "Unexpected null innovationArea for new Assessor CompetitionInvite");
    }

    public CompetitionInvite sendOrResend(User sentBy, ZonedDateTime sentOn) {
        return doSend(sentBy, sentOn);
    }
}
