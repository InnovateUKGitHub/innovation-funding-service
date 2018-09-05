package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionInvite;
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
public class AssessmentInvite extends CompetitionInvite<AssessmentInvite> implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="innovation_category_id", referencedColumnName = "id")
    private InnovationArea innovationArea;

    public AssessmentInvite() {
        super();
    }

    /**
     * A new User invited to a Competition.
     */
    public AssessmentInvite(final String name, final String email, final String hash, final Competition competition, final InnovationArea innovationArea) {
        super(competition, name, email, hash, CREATED);
        if (innovationArea == null) {
            throw new NullPointerException("innovationArea cannot be null");
        }
        this.innovationArea = innovationArea;
    }

    /**
     * An existing User invited to a Competition.
     */
    public AssessmentInvite(final User existingUser, final String hash, Competition competition) {
        super(competition, existingUser.getName(), existingUser.getEmail(), hash, CREATED);
    }

    /**
     * @deprecated
     */
    @Deprecated // TODO workaround for mapstruct see: https://devops.innovateuk.org/issue-tracking/browse/INFUND-4585
    public InnovationArea getInnovationAreaOrNull() {
        return innovationArea;
    }

    public boolean isNewAssessorInvite() {
        return innovationArea != null;
    }

    public AssessmentInvite ifNewAssessorInvite(Consumer<AssessmentInvite> consumer) {
        if (isNewAssessorInvite()) {
            consumer.accept(this);
        }
        return this;
    }

    public InnovationArea getInnovationArea() {
        if (!isNewAssessorInvite()) {
            throw new IllegalStateException(("Cannot get InnovationArea for an existing assessor AssessmentInvite"));
        }
        return requireNonNull(innovationArea, "Unexpected null innovationArea for new Assessor AssessmentInvite");
    }
}