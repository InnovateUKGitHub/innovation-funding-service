package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.AssessorCountOption;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.AssessorCountOptionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.competition.transactional.template.DefaultApplicationQuestionCreator;
import org.innovateuk.ifs.competition.transactional.template.QuestionReprioritisationService;
import org.innovateuk.ifs.competition.transactional.template.QuestionTemplatePersistorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_FORBIDDEN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.transactional.CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY;

/**
 * Service that can create Competition template copies and add and delete Questions to competitions.
 */
@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {
    private static String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

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
    private QuestionReprioritisationService questionPriorityService;

    @Override
    public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        CompetitionType competitionType = competitionTypeRepository.findOne(competitionTypeId);
        Competition template = competitionType.getTemplate();

        Competition competition = competitionRepository.findById(competitionId);
        competition.setCompetitionType(competitionType);
        competition = setDefaultAssessorPayAndCount(competition);

        if (competition == null || competitionIsNotInSetupState(competition)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        competitionTemplatePersistor.cleanByEntityId(competitionId);

        List<Section> sectionList = new ArrayList<>(template.getSections());
        competition.setSections(sectionList);

        return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
    }

    @Override
    public ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition) {
        if (competition == null || competitionIsNotInSetupOrReadyToOpenState(competition)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Section applicationQuestionsSection = sectionRepository.findFirstByCompetitionIdAndName(competition.getId(), ASSESSED_QUESTIONS_SECTION_NAME);
        Question question = defaultApplicationQuestionCreator.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);
        question.setCompetition(competition);

        Question createdQuestion = questionTemplatePersistorServiceImpl.persistByEntity(Arrays.asList(question)).get(0);
        Question prioritizedQuestion = questionPriorityService.prioritiseAssessedQuestionAfterCreation(createdQuestion);

        return serviceSuccess(prioritizedQuestion);
    }

    @Override
    public ServiceResult<Void> deleteAssessedQuestionInCompetition(Long questionId) {
        Question question = questionRepository.findFirstByIdAndSectionName(questionId, ASSESSED_QUESTIONS_SECTION_NAME);

        if(questionRepository.countByCompetitionIdAndSectionName(question.getCompetition().getId(), ASSESSED_QUESTIONS_SECTION_NAME) <= 1) {
            return serviceFailure(new Error(GENERAL_FORBIDDEN));
        }

        if (question.getCompetition() == null || !question.getCompetition().getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        questionTemplatePersistorServiceImpl.deleteEntityById(questionId);
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
}
