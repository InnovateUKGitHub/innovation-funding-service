package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;


/**
 * Interface for getting and saving statuses on in setup for questions.
 */
public interface QuestionSetupCompetitionRestService {

    RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId);

    RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource);

    RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId);

    RestResult<Void> addResearchCategoryQuestionToCompetition(long competitionId);

    RestResult<Void> deleteById(long questionId);

    RestResult<Void> uploadTemplateDocument(long questionId, String contentType, long size, String originalFilename, byte[] multipartFileBytes);

    RestResult<Void> deleteTemplateDocument(long questionId);

}
