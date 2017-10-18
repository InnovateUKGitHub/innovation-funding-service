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
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorService;
import org.innovateuk.ifs.competition.transactional.template.DefaultApplicationQuestionFactory;
import org.innovateuk.ifs.competition.transactional.template.QuestionPriorityService;
import org.innovateuk.ifs.competition.transactional.template.QuestionTemplatePersistorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.transactional.CompetitionSetupServiceImpl.DEFAULT_ASSESSOR_PAY;

@Service
public class CompetitionSetupTemplateServiceImpl implements CompetitionSetupTemplateService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private DefaultApplicationQuestionFactory defaultApplicationQuestionFactory;

    @Autowired
    private CompetitionTemplatePersistorService competitionTemplatePersistor;

    @Autowired
    private QuestionTemplatePersistorService questionTemplatePersistorService;

    @Autowired
    private AssessorCountOptionRepository assessorCountOptionRepository;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionPriorityService questionPriorityService;

    @Override
    public ServiceResult<Competition> createCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        CompetitionType competitionType = competitionTypeRepository.findOne(competitionTypeId);
        Competition template = competitionType.getTemplate();

        Competition competition = competitionRepository.findById(competitionId);
        competition.setCompetitionType(competitionType);
        competition = setDefaultAssessorPayAndCount(competition);

        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        competitionTemplatePersistor.cleanByEntityId(competition.getId());

        List<Section> sectionList = new ArrayList<>(template.getSections());
        competition.setSections(sectionList);

        return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
    }

    @Override
    public ServiceResult<Question> createDefaultForApplicationSection(Competition competition) {
        //Perform checks
        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        Section applicationQuestionsSection = sectionRepository.findFirstByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);
        question.setCompetition(competition);

        Question createdQuestion = questionTemplatePersistorService.persistByEntity(Arrays.asList(question)).get(0);
        Question prioritizedQuestion = questionPriorityService.prioritiseAssessedQuestionAfterCreation(createdQuestion);

        return serviceSuccess(prioritizedQuestion);
    }

    @Override
    public ServiceResult<Void> deleteQuestionInApplicationSection(Long questionId) {
        Question question = questionRepository.findOne(questionId);

        if (question.getCompetition() == null || !question.getCompetition().getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        questionTemplatePersistorService.deleteEntityById(questionId);
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
}
