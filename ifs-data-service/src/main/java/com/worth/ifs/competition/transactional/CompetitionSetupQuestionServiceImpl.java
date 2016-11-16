package com.worth.ifs.competition.transactional;

import com.google.common.collect.Lists;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.resource.ApplicantFormInputType;
import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.assessment.resource.AssessorFormInputType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.mapper.FormInputGuidanceRowMapper;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.repository.FormInputTypeRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for operations around the usage and processing of Competitions questions in setup.
 */
@Service
public class CompetitionSetupQuestionServiceImpl extends BaseTransactionalService implements CompetitionSetupQuestionService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionSetupQuestionServiceImpl.class);

    private static String APPENDIX_GUIDANCE_QUESTION = "What should I include in the appendix?";
    private static String APPENDIX_GUIDANCE_ANSWER = "<p>You may include an appendix of additional information to support the question.</p>" +
                                                     "<p>You may include, for example, a Gantt chart or project management structure.</p>" +
                                                     "<p>The appendix should:</p>" +
                                                     "<ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li>" +
                                                     "<li>be readable with 100% magnification</li>" +
                                                     "<li>contain your application number and project title at the top</li>" +
                                                     "<li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><" +
                                                     "li>be less than 1mb in size</li>" +
                                                     "</ul>";
    private static String APPENDIX_DESCRIPTION = "Appendix";



    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FormInputTypeRepository formInputTypeRepository;

    @Autowired
    private FormInputGuidanceRowMapper formInputGuidanceRowMapper;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        CompetitionSetupQuestionResource setupResource = new CompetitionSetupQuestionResource();

        setupResource.setWrittenFeedback(false);
        setupResource.setAppendix(false);
        setupResource.setScored(false);
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
        return ServiceResult.serviceSuccess(setupResource);
    }

    private void mapApplicationFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        if (ApplicantFormInputType.FILE_UPLOAD.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setAppendix(true);
        } else if (ApplicantFormInputType.QUESTION.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setGuidanceTitle(formInput.getGuidanceQuestion());
            setupResource.setGuidance(formInput.getGuidanceAnswer());
            setupResource.setMaxWords(wordCountWithDefault(formInput.getWordCount()));
        }
    }


    private void mapAssessmentFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        if (AssessorFormInputType.FEEDBACK.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setWrittenFeedback(true);
            setupResource.setAssessmentMaxWords(wordCountWithDefault(formInput.getWordCount()));
        } else if (AssessorFormInputType.SCORE.getTitle().equals(formInput.getFormInputType().getTitle())) {
            setupResource.setScored(true);
            setupResource.setAssessmentGuidance(formInput.getGuidanceQuestion());
            setupResource.setGuidanceRows(Lists.newArrayList(formInputGuidanceRowMapper.mapToResource(formInput.getFormInputGuidanceRows())));
        } else if (AssessorFormInputType.APPLICATION_IN_SCOPE.getTitle().equals(formInput.getFormInputType().getTitle())) {
            //TODO: INFUND-5631
        } else if (AssessorFormInputType.RESEARCH_CATEGORY.getTitle().equals(formInput.getFormInputType().getTitle())) {
            //TODO: INFUND-5631
        }
    }

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> save(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        Long questionId = competitionSetupQuestionResource.getQuestionId();
        Question question = questionRepository.findOne(questionId);

        question.setShortName(competitionSetupQuestionResource.getShortTitle());
        question.setName(competitionSetupQuestionResource.getTitle());
        question.setDescription(competitionSetupQuestionResource.getSubTitle());

        FormInput questionFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputType_Title(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.QUESTION.getTitle());
        questionFormInput.setGuidanceQuestion(competitionSetupQuestionResource.getGuidanceTitle());
        questionFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
        questionFormInput.setWordCount(competitionSetupQuestionResource.getMaxWords());

        createOrDeleteAppendixFormInput(questionId, competitionSetupQuestionResource, question, questionFormInput);

        //TODO INFUND-5685 and INFUND-5631 Save assessor form inputs for AssessorFormInputTypes

        return ServiceResult.serviceSuccess(competitionSetupQuestionResource);
    }


    private void createOrDeleteAppendixFormInput(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource, Question question, FormInput questionFormInput) {
        FormInput appendixFormInput = formInputRepository.findByQuestionIdAndScopeAndFormInputType_Title(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.FILE_UPLOAD.getTitle());
        if (competitionSetupQuestionResource.getAppendix()) {
            if (appendixFormInput == null) {
                appendixFormInput = new FormInput();
                appendixFormInput.setScope(FormInputScope.APPLICATION);
                appendixFormInput.setFormInputType(formInputTypeRepository.findOneByTitle(ApplicantFormInputType.FILE_UPLOAD.getTitle()));
                appendixFormInput.setQuestion(question);
                appendixFormInput.setGuidanceQuestion(APPENDIX_GUIDANCE_QUESTION);
                appendixFormInput.setGuidanceQuestion(APPENDIX_GUIDANCE_ANSWER);
                appendixFormInput.setDescription(APPENDIX_DESCRIPTION);
                appendixFormInput.setIncludedInApplicationSummary(true);
                appendixFormInput.setCompetition(question.getCompetition());
                if (questionFormInput != null) {
                    appendixFormInput.setPriority(questionFormInput.getPriority() + 1);
                } else {
                    appendixFormInput.setPriority(0);
                }
                formInputRepository.save(appendixFormInput);
            }
        } else {
            formInputRepository.delete(appendixFormInput);
        }
    }


    private int wordCountWithDefault(Integer wordCount) {
        if (wordCount != null && wordCount > 0) {
            return wordCount;
        } else {
            return 400;
        }
    }
}
