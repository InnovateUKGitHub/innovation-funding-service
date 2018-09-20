package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.BaseInviteBuilder;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link AssessmentInvite}s.
 */
public class AssessmentInviteBuilder extends BaseInviteBuilder<Competition, AssessmentInvite, AssessmentInviteBuilder> {

    private AssessmentInviteBuilder(List<BiConsumer<Integer, AssessmentInvite>> multiActions) {
        super(multiActions);
    }

    public static AssessmentInviteBuilder newAssessmentInvite() {
        return new AssessmentInviteBuilder(emptyList()).with(uniqueIds());
    }

    public static AssessmentInviteBuilder newAssessmentInviteWithoutId() {
        return new AssessmentInviteBuilder(emptyList());
    }

    @Override
    protected AssessmentInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentInvite>> actions) {
        return new AssessmentInviteBuilder(actions);
    }

    public AssessmentInviteBuilder withCompetition(Competition... competitions) {
        return withTarget(competitions);
    }

    public AssessmentInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    public AssessmentInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public AssessmentInviteBuilder withInnovationArea(InnovationArea... innovationAreas) {
        return withArraySetFieldByReflection("innovationArea", innovationAreas);
    }

    public AssessmentInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public AssessmentInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
    @Override
    protected AssessmentInvite createInitial() {
        return new AssessmentInvite();
    }
}
