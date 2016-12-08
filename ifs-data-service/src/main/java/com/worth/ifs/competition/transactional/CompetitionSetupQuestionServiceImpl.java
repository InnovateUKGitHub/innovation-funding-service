package com.worth.ifs.competition.transactional;

import com.google.common.collect.Lists;
import com.worth.ifs.application.domain.GuidanceRow;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.GuidanceRowRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.mapper.GuidanceRowMapper;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.resource.FormInputType;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for operations around the usage and processing of Competitions questions in setup.
 */
@Service
public class CompetitionSetupQuestionServiceImpl extends BaseTransactionalService implements CompetitionSetupQuestionService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowMapper guidanceRowMapper;

    @Autowired
    private GuidanceRowRepository guidanceRowRepository;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        CompetitionSetupQuestionResource setupResource = new CompetitionSetupQuestionResource();

        question.getFormInputs().forEach(formInput -> {
            if(FormInputScope.ASSESSMENT.equals(formInput.getScope())) {
                mapAssessmentFormInput(formInput, setupResource);
            } else {
                mapApplicationFormInput(formInput, setupResource);
            }
        });

        setupResource.setScoreTotal(question.getAssessorMaximumScore());
        setupResource.setNumber(question.getQuestionNumber());
        setupResource.setShortTitle(question.getShortName());
        setupResource.setTitle(question.getName());
        setupResource.setSubTitle(question.getDescription());
        setupResource.setQuestionId(question.getId());
        setupResource.setType(CompetitionSetupQuestionType.typeFromQuestionTitle(question.getShortName()));
        setupResource.setShortTitleEditable(isShortNameEditable(setupResource.getType()));

        return ServiceResult.serviceSuccess(setupResource);
    }

    private void mapApplicationFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case FILEUPLOAD:
                setupResource.setAppendix(formInput.getActive());
                break;
            case TEXTAREA:
                setupResource.setGuidanceTitle(formInput.getGuidanceTitle());
                setupResource.setGuidance(formInput.getGuidanceAnswer());
                setupResource.setMaxWords(wordCountWithDefault(formInput.getWordCount()));
                break;
        }
    }

    private void mapAssessmentFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case TEXTAREA:
                setupResource.setWrittenFeedback(formInput.getActive());
                setupResource.setAssessmentMaxWords(wordCountWithDefault(formInput.getWordCount()));
                setupResource.setAssessmentGuidance(formInput.getGuidanceTitle());
                setupResource.setGuidanceRows(Lists.newArrayList(guidanceRowMapper.mapToResource(formInput.getGuidanceRows())));
                break;
            case ASSESSOR_SCORE:
                setupResource.setScored(formInput.getActive());
                break;
            case ASSESSOR_APPLICATION_IN_SCOPE:
                setupResource.setScope(formInput.getActive());
                break;
            case ASSESSOR_RESEARCH_CATEGORY:
                setupResource.setResearchCategoryQuestion(formInput.getActive());
                break;
        }
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        Long questionId = competitionSetupQuestionResource.getQuestionId();
        Question question = questionRepository.findOne(questionId);

        if (isShortNameEditable(CompetitionSetupQuestionType.typeFromQuestionTitle(question.getShortName()))) {
            question.setShortName(competitionSetupQuestionResource.getShortTitle());
        }

        question.setName(competitionSetupQuestionResource.getTitle());
        question.setDescription(competitionSetupQuestionResource.getSubTitle());
        question.setAssessorMaximumScore(competitionSetupQuestionResource.getScoreTotal());

        FormInput questionFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA);
        questionFormInput.setGuidanceTitle(competitionSetupQuestionResource.getGuidanceTitle());
        questionFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
        questionFormInput.setWordCount(competitionSetupQuestionResource.getMaxWords());

        markAppendixAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markScoredAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markWrittenFeedbackAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markResearchCategoryQuestionAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markScopeAsActiveOrInactive(questionId, competitionSetupQuestionResource);

        return ServiceResult.serviceSuccess(competitionSetupQuestionResource);
    }

    private void markAppendixAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        FormInput appendixFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD);
        if (appendixFormInput != null && competitionSetupQuestionResource.getAppendix() != null) {
            appendixFormInput.setActive(competitionSetupQuestionResource.getAppendix());
        }
    }

    private void markScoredAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {

        FormInput scoredFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_SCORE);

        if (scoredFormInput != null && competitionSetupQuestionResource.getScored() != null) {
            scoredFormInput.setActive(competitionSetupQuestionResource.getScored());
        }
    }

    private void markResearchCategoryQuestionAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {

        FormInput researchCategoryQuestionFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_RESEARCH_CATEGORY);

        if (researchCategoryQuestionFormInput != null && competitionSetupQuestionResource.getResearchCategoryQuestion() != null) {
            researchCategoryQuestionFormInput.setActive(competitionSetupQuestionResource.getResearchCategoryQuestion());
        }
    }

    private void markScopeAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {

        FormInput scopeFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_APPLICATION_IN_SCOPE);

        if (scopeFormInput != null && competitionSetupQuestionResource.getScope() != null) {
            scopeFormInput.setActive(competitionSetupQuestionResource.getScope());
        }
    }

    private void markWrittenFeedbackAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {

        FormInput writtenFeedbackFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.TEXTAREA);

        if (writtenFeedbackFormInput != null && competitionSetupQuestionResource.getWrittenFeedback() != null) {
            writtenFeedbackFormInput.setActive(competitionSetupQuestionResource.getWrittenFeedback());

            writtenFeedbackFormInput.setGuidanceTitle(competitionSetupQuestionResource.getAssessmentGuidance());
            writtenFeedbackFormInput.setWordCount(competitionSetupQuestionResource.getAssessmentMaxWords());

            // Delete all existing guidance rows and replace with new list
            List<GuidanceRow> newRows = Lists.newArrayList(guidanceRowMapper.mapToDomain(competitionSetupQuestionResource.getGuidanceRows()));
            // Ensure form input and priority set against newly added rows
            AtomicInteger ai = new AtomicInteger(0);
            newRows.stream().forEach(row -> {
                row.setFormInput(writtenFeedbackFormInput);
                row.setPriority(ai.get());
                ai.getAndIncrement();
            });
            guidanceRowRepository.delete(writtenFeedbackFormInput.getGuidanceRows());
            guidanceRowRepository.save(newRows);
            writtenFeedbackFormInput.setGuidanceRows(newRows);
        }
    }

    private int wordCountWithDefault(Integer wordCount) {
        if (wordCount != null && wordCount > 0) {
            return wordCount;
        } else {
            return 400;
        }
    }

    private boolean isShortNameEditable(CompetitionSetupQuestionType type) {
        return CompetitionSetupQuestionType.ASSESSED_QUESTION.equals(type);
    }
}
