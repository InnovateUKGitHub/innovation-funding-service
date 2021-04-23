package org.innovateuk.ifs.management.competition.setup.completionstage.util;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CompletionStageUtils {

    @Value("${ifs.always.open.competition.enabled}")
    private boolean alwaysOpenCompetitionEnabled;

    public CompletionStageUtils() {}

    public boolean isAlwaysOpenCompetitionEnabled() {
        return alwaysOpenCompetitionEnabled;
    }

    public boolean isApplicationSubmissionEnabled(CompetitionCompletionStage competitionCompletionStage) {
        return isAlwaysOpenCompetitionEnabled()
                && CompetitionCompletionStage.alwaysOpenValues().stream()
                .anyMatch(completionStage -> (completionStage == competitionCompletionStage));
    }
}
