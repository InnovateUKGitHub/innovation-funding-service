package org.innovateuk.ifs.invite.domain.competition;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;


/**
 * An Invite to assess a Competition. Can be to a new user or an existing User.
 */
@Entity
@DiscriminatorValue("COMPETITION")
public class CompetitionAssessmentInvite extends CompetitionInvite<CompetitionAssessmentInvite> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="innovation_category_id", referencedColumnName = "id")
    private InnovationArea innovationArea;

    public CompetitionAssessmentInvite() {
        super();
    }

    /**
     * A new User invited to a Competition.
     */
    public CompetitionAssessmentInvite(final String name, final String email, final String hash, final Competition competition, final InnovationArea innovationArea) {
        super(competition, name, email, hash, CREATED);
        if (innovationArea == null) {
            throw new NullPointerException("innovationArea cannot be null");
        }
        this.innovationArea = innovationArea;
    }

    /**
     * An existing User invited to a Competition.
     */
    public CompetitionAssessmentInvite(final User existingUser, final String hash, Competition competition) {
        super(competition, existingUser.getName(), existingUser.getEmail(), hash, CREATED);
    }

    @Deprecated // TODO workaround for mapstruct see: https://devops.innovateuk.org/issue-tracking/browse/INFUND-4585
    public InnovationArea getInnovationAreaOrNull() {
        return innovationArea;
    }

    public boolean isNewAssessorInvite() {
        return innovationArea != null;
    }

    public CompetitionAssessmentInvite ifNewAssessorInvite(Consumer<CompetitionAssessmentInvite> consumer) {
        if (isNewAssessorInvite()) {
            consumer.accept(this);
        }
        return this;
    }

    public InnovationArea getInnovationArea() {
        if (!isNewAssessorInvite()) {
            throw new IllegalStateException(("Cannot get InnovationArea for an existing assessor CompetitionAssessmentInvite"));
        }
        return requireNonNull(innovationArea, "Unexpected null innovationArea for new Assessor CompetitionAssessmentInvite");
    }
}