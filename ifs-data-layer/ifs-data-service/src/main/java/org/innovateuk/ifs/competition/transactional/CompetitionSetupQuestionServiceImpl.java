package org.innovateuk.ifs.competition.transactional;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.mapper.GuidanceRowMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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

    @Autowired
    private CompetitionSetupTemplateService competitionSetupTemplateService;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(Question.class, questionId))
                .andOnSuccess(question -> mapQuestionToSuperQuestionResource(question));
    }

    private ServiceResult<CompetitionSetupQuestionResource> mapQuestionToSuperQuestionResource(Question question) {
        CompetitionSetupQuestionResource setupResource = new CompetitionSetupQuestionResource();

        question.getFormInputs().forEach(formInput -> {
            if (FormInputScope.ASSESSMENT.equals(formInput.getScope())) {
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

        return serviceSuccess(setupResource);
    }

    private void mapApplicationFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case FILEUPLOAD:
                setupResource.setAppendix(formInput.getActive());
                break;
            case TEXTAREA:
                setupResource.setGuidanceTitle(formInput.getGuidanceTitle());
                setupResource.setGuidance(formInput.getGuidanceAnswer());
                setupResource.setMaxWords(formInput.getWordCount());
                break;
        }
    }

    private void mapAssessmentFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case TEXTAREA:
                setupResource.setWrittenFeedback(formInput.getActive());
                setupResource.setAssessmentMaxWords(formInput.getWordCount());
                setupResource.setAssessmentGuidance(formInput.getGuidanceAnswer());
                setupResource.setGuidanceRows(sortByPriority((guidanceRowMapper.mapToResource(formInput.getGuidanceRows()))));
                setupResource.setAssessmentGuidanceTitle(formInput.getGuidanceTitle());
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

    private List<GuidanceRowResource> sortByPriority(Iterable<GuidanceRowResource> guidanceRowResources) {
        List<GuidanceRowResource> resources = Lists.newArrayList(guidanceRowResources);
        Collections.sort(resources, (o1, o2) -> o1.getPriority().compareTo(o2.getPriority()));
        return resources;
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionSetupQuestionResource> createByCompetitionId(Long competitionId) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccess(competition -> competitionSetupTemplateService.addDefaultAssessedQuestionToCompetition(competition))
                .andOnSuccess(question -> mapQuestionToSuperQuestionResource(question));
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(Long questionId) {
        competitionSetupTemplateService.deleteAssessedQuestionInCompetition(questionId);

        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionSetupQuestionResource> update(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
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

        return serviceSuccess(competitionSetupQuestionResource);
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

            writtenFeedbackFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getAssessmentGuidance());
            writtenFeedbackFormInput.setGuidanceTitle(competitionSetupQuestionResource.getAssessmentGuidanceTitle());
            writtenFeedbackFormInput.setWordCount(competitionSetupQuestionResource.getAssessmentMaxWords());

            // Delete all existing guidance rows and replace with new list
            List<GuidanceRow> newRows = newArrayList(guidanceRowMapper.mapToDomain(competitionSetupQuestionResource.getGuidanceRows()));
            // Ensure form input and priority set against newly added rows
            newRows.forEach(row -> {
                row.setFormInput(writtenFeedbackFormInput);
                row.setPriority(newRows.indexOf(row));
            });
            guidanceRowRepository.delete(writtenFeedbackFormInput.getGuidanceRows());
            guidanceRowRepository.save(newRows);
            writtenFeedbackFormInput.setGuidanceRows(newRows);
        }
    }

    private boolean isShortNameEditable(CompetitionSetupQuestionType type) {
        return CompetitionSetupQuestionType.ASSESSED_QUESTION.equals(type);
    }
}
