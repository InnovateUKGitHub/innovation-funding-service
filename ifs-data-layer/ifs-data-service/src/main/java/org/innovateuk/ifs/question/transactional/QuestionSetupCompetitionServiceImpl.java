package org.innovateuk.ifs.question.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.form.domain.*;
import org.innovateuk.ifs.form.mapper.GuidanceRowMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.MultipleChoiceOptionRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.question.transactional.template.QuestionSetupTemplateService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;
import static org.hibernate.Hibernate.initialize;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.QuestionType.LEAD_ONLY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.setup.resource.QuestionSection.PROJECT_DETAILS;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of Competitions questions in setup.
 */
@Service
public class QuestionSetupCompetitionServiceImpl extends BaseTransactionalService implements QuestionSetupCompetitionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private GuidanceRowMapper guidanceRowMapper;

    @Autowired
    private QuestionSetupTemplateService questionSetupTemplateService;

    @Autowired
    private QuestionPriorityOrderService questionPriorityOrderService;

    @Autowired
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;

    @Override
    public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(long questionId) {
        return find(questionRepository.findById(questionId), notFoundError(Question.class, questionId))
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
        setupResource.setType(question.getQuestionSetupType());

        return serviceSuccess(setupResource);
    }

    private void mapApplicationFormInput(FormInput formInput, CompetitionSetupQuestionResource setupResource) {
        switch (formInput.getType()) {
            case FILEUPLOAD:
                initialize(formInput.getAllowedFileTypes());
                setupResource.setAppendix(formInput.getActive());
                setupResource.setAllowedAppendixResponseFileTypes(formInput.getAllowedFileTypes());
                setupResource.setAppendixGuidance(formInput.getGuidanceAnswer());
                break;
            case TEXTAREA:
                if (formInput.getActive()) {
                    setupResource.setGuidanceTitle(formInput.getGuidanceTitle());
                    setupResource.setGuidance(formInput.getGuidanceAnswer());
                }
                setupResource.setTextArea(formInput.getActive());
                setupResource.setMaxWords(formInput.getWordCount());
                break;
            case TEMPLATE_DOCUMENT:
                initialize(formInput.getAllowedFileTypes());
                setupResource.setTemplateDocument(formInput.getActive());
                setupResource.setAllowedTemplateResponseFileTypes(formInput.getAllowedFileTypes());
                setupResource.setTemplateTitle(formInput.getDescription());
                setupResource.setTemplateFilename(Optional.ofNullable(formInput.getFile())
                        .map(FileEntry::getName)
                        .orElse(null));
                setupResource.setTemplateFormInput(formInput.getId());
                break;
            case MULTIPLE_CHOICE:
                if (formInput.getActive()) {
                    setupResource.setGuidanceTitle(formInput.getGuidanceTitle());
                    setupResource.setGuidance(formInput.getGuidanceAnswer());
                }
                setupResource.setMultipleChoice(formInput.getActive());
                setupResource.setChoices(formInput.getMultipleChoiceOptions().stream()
                        .map(choice -> new MultipleChoiceOptionResource(choice.getId(), choice.getText()))
                        .collect(Collectors.toList()));
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
        sort(resources, comparing(GuidanceRowResource::getPriority));
        return resources;
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionSetupQuestionResource> createByCompetitionId(long competitionId) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccess(competition -> questionSetupTemplateService.addDefaultAssessedQuestionToCompetition(competition))
                .andOnSuccess(this::mapQuestionToSuperQuestionResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> addResearchCategoryQuestionToCompetition(long competitionId) {
        return find(competitionRepository.findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccess(this::saveNewResearchCategoryQuestionForCompetition)
                .andOnSuccessReturnVoid();
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(long questionId) {
        return questionSetupTemplateService.deleteQuestionInCompetition(questionId);
    }

    @Override
    @Transactional
    public ServiceResult<CompetitionSetupQuestionResource> update(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        Long questionId = competitionSetupQuestionResource.getQuestionId();
        Question question = questionRepository.findById(questionId).get();

        if (question.getQuestionSetupType() != QuestionSetupType.APPLICATION_DETAILS) {
            question.setShortName(competitionSetupQuestionResource.getShortTitle());
        }
        question.setName(competitionSetupQuestionResource.getTitle());
        question.setDescription(competitionSetupQuestionResource.getSubTitle());
        question.setAssessorMaximumScore(competitionSetupQuestionResource.getScoreTotal());

        /* form inputs */
        markTextAreaAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markMultipleChoiceAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markAppendixAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markTemplateUploadAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markScoredAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markWrittenFeedbackAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markResearchCategoryQuestionAsActiveOrInactive(questionId, competitionSetupQuestionResource);
        markScopeAsActiveOrInactive(questionId, competitionSetupQuestionResource);

        return serviceSuccess(competitionSetupQuestionResource);
    }

    private void markMultipleChoiceAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        FormInput multipleChoiceFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.MULTIPLE_CHOICE);
        if (multipleChoiceFormInput != null && competitionSetupQuestionResource.getMultipleChoice() != null) {
            if (competitionSetupQuestionResource.getMultipleChoice()) {
                multipleChoiceFormInput.setActive(true);
                multipleChoiceFormInput.setGuidanceTitle(competitionSetupQuestionResource.getGuidanceTitle());
                multipleChoiceFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
                createUpdateOrDeleteMultipleChoices(multipleChoiceFormInput, competitionSetupQuestionResource);
            } else {
                multipleChoiceFormInput.setActive(false);
                multipleChoiceFormInput.setGuidanceTitle(null);
                multipleChoiceFormInput.setGuidanceAnswer(null);
                multipleChoiceFormInput.getMultipleChoiceOptions().forEach(multipleChoiceOptionRepository::delete);
                multipleChoiceFormInput.getMultipleChoiceOptions().clear();
            }
        }
    }

    private void createUpdateOrDeleteMultipleChoices(FormInput multipleChoiceFormInput, CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        //delete
        multipleChoiceFormInput.getMultipleChoiceOptions().removeIf(dbChoice -> {
            Optional<MultipleChoiceOptionResource> maybeChoiceResource = competitionSetupQuestionResource.getChoices().stream()
                    .filter(choiceResource -> choiceResource.getId() != null && choiceResource.getId().equals(dbChoice.getId()))
                    .findFirst();
            boolean delete = !maybeChoiceResource.isPresent();
            if (delete) {
                multipleChoiceOptionRepository.delete(dbChoice);
            }
            return delete;
        });

        //create and update
        competitionSetupQuestionResource.getChoices().forEach(choiceResource -> {
            Optional<MultipleChoiceOption> maybeDbChoice = multipleChoiceFormInput.getMultipleChoiceOptions().stream()
                    .filter(dbChoice -> dbChoice.getId().equals(choiceResource.getId()))
                    .findFirst();
            if (maybeDbChoice.isPresent()) {
                //update
                maybeDbChoice.get().setText(choiceResource.getText());
            } else {
                //create
                multipleChoiceOptionRepository.save(new MultipleChoiceOption(choiceResource.getText(), multipleChoiceFormInput));
            }
        });
    }

    private void markTextAreaAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        FormInput textAreaFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA);
        if (textAreaFormInput != null && competitionSetupQuestionResource.getTextArea() != null) {
            if (competitionSetupQuestionResource.getTextArea()) {
                textAreaFormInput.setGuidanceTitle(competitionSetupQuestionResource.getGuidanceTitle());
                textAreaFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getGuidance());
                textAreaFormInput.setWordCount(competitionSetupQuestionResource.getMaxWords());
                textAreaFormInput.setActive(true);
            } else {
                textAreaFormInput.setGuidanceTitle(null);
                textAreaFormInput.setGuidanceAnswer(null);
                textAreaFormInput.setWordCount(null);
                textAreaFormInput.setActive(false);
            }
        }
    }

    private void markTemplateUploadAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        FormInput templateFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId,
                FormInputScope.APPLICATION,
                FormInputType.TEMPLATE_DOCUMENT);
        if (templateFormInput != null && competitionSetupQuestionResource.getTemplateDocument() != null) {
            templateFormInput.setActive(competitionSetupQuestionResource.getTemplateDocument());

            if(competitionSetupQuestionResource.getTemplateDocument()) {
                templateFormInput.setAllowedFileTypes(competitionSetupQuestionResource.getAllowedTemplateResponseFileTypes());
                if (competitionSetupQuestionResource.getTemplateTitle() != null) {
                    templateFormInput.setDescription(competitionSetupQuestionResource.getTemplateTitle());
                }
            } else {
                templateFormInput.setAllowedFileTypes(null);
                templateFormInput.setDescription(null);
            }
        }
    }

    private void markAppendixAsActiveOrInactive(Long questionId, CompetitionSetupQuestionResource competitionSetupQuestionResource) {
        FormInput appendixFormInput = formInputRepository.findByQuestionIdAndScopeAndType(questionId,
                FormInputScope.APPLICATION,
                FormInputType.FILEUPLOAD);
        if (appendixFormInput != null && competitionSetupQuestionResource.getAppendix() != null) {
            appendixFormInput.setActive(competitionSetupQuestionResource.getAppendix());

            if(competitionSetupQuestionResource.getAppendix()) {
                appendixFormInput.setAllowedFileTypes(competitionSetupQuestionResource.getAllowedAppendixResponseFileTypes());
                if (competitionSetupQuestionResource.getAppendixGuidance() != null) {
                    appendixFormInput.setGuidanceAnswer(competitionSetupQuestionResource.getAppendixGuidance());
                }
            } else {
                appendixFormInput.setAllowedFileTypes(null);
                appendixFormInput.setGuidanceAnswer(null);
            }
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
            forEachWithIndex(newRows, (index, row) -> {
                row.setFormInput(writtenFeedbackFormInput);
                row.setPriority(index);
            });
            writtenFeedbackFormInput.getGuidanceRows().clear();
            writtenFeedbackFormInput.getGuidanceRows().addAll(newRows);
            formInputRepository.save(writtenFeedbackFormInput);
        }
    }

    private ServiceResult<Question> saveNewResearchCategoryQuestionForCompetition(Competition competition) {
        return find(sectionRepository.findFirstByCompetitionIdAndName(competition.getId(), PROJECT_DETAILS
                .getName()), notFoundError(Section.class)).andOnSuccessReturn(section -> {
            Question question = new Question();
            question.setAssignEnabled(false);
            question.setDescription("Description not used");
            question.setMarkAsCompletedEnabled(true);
            question.setName(RESEARCH_CATEGORY.getShortName());
            question.setShortName(RESEARCH_CATEGORY.getShortName());
            question.setCompetition(competition);
            question.setSection(section);
            question.setType(LEAD_ONLY);
            question.setQuestionSetupType(RESEARCH_CATEGORY);

            Question createdQuestion = questionRepository.save(question);
            return questionPriorityOrderService.prioritiseResearchCategoryQuestionAfterCreation(createdQuestion);
        });
    }
}
