package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Implements {@link QuestionSetupCompetitionRestService}
 */
@Service
public class QuestionSetupCompetitionRestServiceImpl extends BaseRestService implements
        QuestionSetupCompetitionRestService {

    private static final String QUESTION_SETUP_REST_URL = "/question-setup";

    @Override
    public RestResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        return getWithRestResult(QUESTION_SETUP_REST_URL + "/get-by-id/" + questionId,
                CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        return putWithRestResult(QUESTION_SETUP_REST_URL + "/save", competitionSetupQuestionResource, Void.class);
    }

    @Override
    public RestResult<CompetitionSetupQuestionResource> addDefaultToCompetition(Long competitionId) {
        return postWithRestResult(QUESTION_SETUP_REST_URL + "/add-default-to-competition/" +
                competitionId, CompetitionSetupQuestionResource.class);
    }

    @Override
    public RestResult<Void> addResearchCategoryQuestionToCompetition(final long competitionId) {
        return postWithRestResult(QUESTION_SETUP_REST_URL + "/add-research-category-question-to-competition/" +
                competitionId, Void.class);
    }

    @Override
    public RestResult<Void> deleteById(long questionId) {
        return deleteWithRestResult(QUESTION_SETUP_REST_URL + "/delete-by-id/" + questionId, Void.class);
    }

    @Override
    public RestResult<Void> uploadTemplateDocument(long questionId, String contentType, long size, String originalFilename, byte[] multipartFileBytes) {
        String url = format("%s/%s/%s?filename=%s", QUESTION_SETUP_REST_URL, "template-file", questionId, originalFilename);
        return postWithRestResult(url, multipartFileBytes, createFileUploadHeader(contentType, size), Void.class);
    }

    @Override
    public RestResult<Void> deleteTemplateDocument(long questionId) {
        String url = format("%s/%s/%s", QUESTION_SETUP_REST_URL, "template-file", questionId);
        return deleteWithRestResult(url);
    }
}
