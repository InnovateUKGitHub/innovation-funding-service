package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.competition.AssessmentInvite;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link AssessmentInvite}s.
 */
public class CompetitionAssessmentInviteBuilder extends BaseInviteBuilder<Competition, AssessmentInvite, CompetitionAssessmentInviteBuilder> {

    private CompetitionAssessmentInviteBuilder(List<BiConsumer<Integer, AssessmentInvite>> multiActions) {
        super(multiActions);
    }

    public static CompetitionAssessmentInviteBuilder newCompetitionAssessmentInvite() {
        return new CompetitionAssessmentInviteBuilder(emptyList()).with(uniqueIds());
    }

    public static CompetitionAssessmentInviteBuilder newCompetitionInviteWithoutId() {
        return new CompetitionAssessmentInviteBuilder(emptyList());
    }

    @Override
    protected CompetitionAssessmentInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentInvite>> actions) {
        return new CompetitionAssessmentInviteBuilder(actions);
    }

    public CompetitionAssessmentInviteBuilder withCompetition(Competition... competitions) {
        return withTarget(competitions);
    }

    public CompetitionAssessmentInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    public CompetitionAssessmentInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public CompetitionAssessmentInviteBuilder withInnovationArea(InnovationArea... innovationAreas) {
        return withArraySetFieldByReflection("innovationArea", innovationAreas);
    }

    public CompetitionAssessmentInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public CompetitionAssessmentInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
    @Override
    protected AssessmentInvite createInitial() {
        return new AssessmentInvite();
    }
}
