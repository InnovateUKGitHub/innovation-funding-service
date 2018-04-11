package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessorCountOption;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.repository.TermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.question.transactional.template.DefaultApplicationQuestionCreator;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.question.transactional.template.QuestionTemplatePersistorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.transactional.CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY;
import static org.innovateuk.ifs.setup.resource.QuestionSection.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service that can create Competition template copies and add and delete Questions to competitions.
 */
@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private DefaultApplicationQuestionCreator defaultApplicationQuestionCreator;

    @Autowired
    private CompetitionTemplatePersistorImpl competitionTemplatePersistor;

    @Autowired
    private QuestionTemplatePersistorImpl questionTemplatePersistorServiceImpl;

    @Autowired
    private AssessorCountOptionRepository assessorCountOptionRepository;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionPriorityOrderService questionPriorityService;

    @Autowired
    private TermsAndConditionsRepository termsAndConditionsRepository;

    @Override
    public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        CompetitionType competitionType = competitionTypeRepository.findOne(competitionTypeId);

        if (competitionType == null) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Competition template = competitionType.getTemplate();
        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        Competition competition = competitionRepository.findById(competitionId);
        if (competition == null || competitionIsNotInSetupState(competition)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        competition.setCompetitionType(competitionType);
        competition = setDefaultAssessorPayAndCount(competition);

        competitionTemplatePersistor.cleanByEntityId(competitionId);

        Competition populatedCompetition = copyTemplatePropertiesToCompetition(template, competition);
        return serviceSuccess(competitionTemplatePersistor.persistByEntity(populatedCompetition));
    }

    private Competition copyTemplatePropertiesToCompetition(Competition template, Competition competition) {
        competition.setSections(new ArrayList<>(template.getSections()));
        competition.setFullApplicationFinance(template.isFullApplicationFinance());
        competition.setTermsAndConditions(template.getTermsAndConditions());
        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
        competition.setMinProjectDuration(template.getMinProjectDuration());
        competition.setMaxProjectDuration(template.getMaxProjectDuration());
        return competition;
    }

    @Override
    public ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition) {
        if (competition == null || competitionIsNotInSetupOrReadyToOpenState(competition)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        return find(sectionRepository.findFirstByCompetitionIdAndName(competition.getId(), APPLICATION_QUESTIONS.getName()), notFoundError(Section.class))
                .andOnSuccess(section -> initializeAndPersistQuestion(section, competition));
    }

    private ServiceResult<Question> initializeAndPersistQuestion(Section applicationQuestionsSection, Competition competition) {
        Question question = defaultApplicationQuestionCreator.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);

        Question createdQuestion = questionTemplatePersistorServiceImpl.persistByEntity(Arrays.asList(question)).get(0);
        Question prioritizedQuestion = questionPriorityService.prioritiseAssessedQuestionAfterCreation(createdQuestion);

        return serviceSuccess(prioritizedQuestion);
    }

    @Override
    public ServiceResult<Void> deleteQuestionInCompetition(Long questionId) {
        return find(questionRepository.findFirstById(questionId),
                    notFoundError(Question.class, questionId))
                .andOnSuccess(question -> deleteQuestion(question));
    }

    private ServiceResult<Void> deleteQuestion(Question question) {
        if (question.getCompetition() == null || competitionIsNotInSetupOrReadyToOpenState(question.getCompetition())) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if(sectionIsInValidForDeletion(question.getSection().getName())) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        if(questionRepository.countByCompetitionId(question.getCompetition().getId()) <= 1) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        questionTemplatePersistorServiceImpl.deleteEntityById(question.getId());
        questionPriorityService.reprioritiseAssessedQuestionsAfterDeletion(question);

        return serviceSuccess();
    }

    private Competition setDefaultAssessorPayAndCount(Competition competition) {
        if (competition.getAssessorCount() == null) {
            Optional<AssessorCountOption> defaultAssessorOption = assessorCountOptionRepository.findByCompetitionTypeIdAndDefaultOptionTrue(competition.getCompetitionType().getId());
            defaultAssessorOption.ifPresent(assessorCountOption -> competition.setAssessorCount(assessorCountOption.getOptionValue()));
        }

        if (competition.getAssessorPay() == null) {
            competition.setAssessorPay(DEFAULT_ASSESSOR_PAY);
        }
        return competition;
    }

    private boolean competitionIsNotInSetupState(Competition competition) {
        return !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP);
    }

    private boolean competitionIsNotInSetupOrReadyToOpenState(Competition competition) {
        return !(competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)
                || competition.getCompetitionStatus().equals(CompetitionStatus.READY_TO_OPEN));
    }

    private boolean sectionIsInValidForDeletion(String sectionName) {
        return !sectionName.equals(APPLICATION_QUESTIONS.getName()) && !sectionName.equals(PROJECT_DETAILS.getName());
    }
}
