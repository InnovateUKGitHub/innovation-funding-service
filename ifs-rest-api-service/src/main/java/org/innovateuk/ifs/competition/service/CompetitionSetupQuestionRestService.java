package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.*;
import org.innovateuk.ifs.competition.resource.*;

public interface CompetitionSetupQuestionRestService {
    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionRestService")
    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);
    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionRestService")
    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);
    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionRestService")
    RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId);
    @ZeroDowntime(reference = "IFS-3016", description = "endpoint moved to QuestionSetupCompetitionRestService")
    RestResult<Void> deleteById(Long questionId);
}
