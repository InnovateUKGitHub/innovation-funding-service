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
import org.innovateuk.ifs.competition.transactional.template.QuestionTemplatePersistorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ServiceResult<Competition> createCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
        CompetitionType competitionType = competitionTypeRepository.findOne(competitionTypeId);
        Competition template = competitionType.getTemplate();

        Competition competition = competitionRepository.findById(competitionId);
        competition.setCompetitionType(competitionType);
        competition = setDefaultAssessorPayAndCount(competition);

        List<Section> sectionList = new ArrayList<>(template.getSections());

        competition.setSections(sectionList);

        //Perform checks

        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        competitionTemplatePersistor.cleanByEntityId(competition.getId());
        return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
    }

    @Override
    public ServiceResult<Question> createDefaultForApplicationSection(Competition competition) {
        //Perform checks

        Section applicationQuestionsSection = sectionRepository.findFirstByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);
        question.setCompetition(competition);

        Question createdQuestion = questionTemplatePersistorService.persistByEntity(Arrays.asList(question)).get(0);
        Question prioritizedQuestion = prioritizeQuestion(createdQuestion);
        updateQuestionNumbers(createdQuestion.getCompetition().getId(), createdQuestion.getSection().getId());

        return serviceSuccess(prioritizedQuestion);
    }

    @Override
    public ServiceResult<Void> deleteQuestionInApplicationSection(Long questionId) {
        //Perform checks
        Question question = questionRepository.findOne(questionId);
        questionTemplatePersistorService.deleteEntityById(questionId);
        updateFollowingQuestionsPrioritiesByDelta(-1, question.getPriority().longValue(), question.getCompetition().getId());
        updateQuestionNumbers(question.getCompetition().getId(), question.getSection().getId());

        return serviceSuccess();
    }

    @Transactional
    private Question prioritizeQuestion(Question createdQuestion) {
        Question assessedQuestionWithHighestPriority = questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(createdQuestion.getCompetition().getId(), createdQuestion.getSection().getId());
        createdQuestion.setPriority(assessedQuestionWithHighestPriority.getPriority() + 1);

        updateFollowingQuestionsPrioritiesByDelta(1, createdQuestion.getPriority().longValue(), createdQuestion.getCompetition().getId());

        return questionRepository.save(createdQuestion);
    }

    private void updateFollowingQuestionsPrioritiesByDelta(int delta, Long priority, Long competitionId) {
        List<Question> subsequentQuestions = questionRepository.findByCompetitionIdAndPriorityGreaterThanOrderByPriorityAsc(competitionId, priority);

        subsequentQuestions.stream().forEach(question -> question.setPriority(question.getPriority() + 1));

        questionRepository.save(subsequentQuestions);
    }

    @Transactional
    private void updateQuestionNumbers(Long competitionId, Long sectionId) {
        List<Question> assessedQuestions = questionRepository.findByCompetitionIdAndSectionIdOrderByPriorityAsc(competitionId, sectionId);

        Integer questionNumber = 1;

        for(Question question : assessedQuestions) {
            question.setQuestionNumber(questionNumber.toString());
            questionNumber++;
        }

        questionRepository.save(assessedQuestions);
    }

    /*@Transactional
    private Question reprioritizeSubsequentQuestions() {
        List<Question> followingQuestions =
    }*/

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
