package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorService;
import org.innovateuk.ifs.competition.transactional.template.DefaultApplicationQuestionFactory;
import org.innovateuk.ifs.competition.transactional.template.QuestionTemplatePersistorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NO_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

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

    @Override
    public ServiceResult<Competition> createCompetitionByCompetitionTemplate(Competition competition, Competition template) {
        //Perform checks

        if (competition == null || !competition.getCompetitionStatus().equals(CompetitionStatus.COMPETITION_SETUP)) {
            return serviceFailure(new Error(COMPETITION_NOT_EDITABLE));
        }

        if (template == null) {
            return serviceFailure(new Error(COMPETITION_NO_TEMPLATE));
        }

        template.setId(competition.getId());

        competitionTemplatePersistor.cleanByEntityId(competition.getId());

        return serviceSuccess(competitionTemplatePersistor.persistByEntity(competition));
    }

    @Override
    public ServiceResult<Question> createDefaultForApplicationSection(Competition competition) {
        //Perform checks

        Section applicationQuestionsSection = sectionRepository.findByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);

        return serviceSuccess(questionTemplatePersistorService.persistByEntity(Arrays.asList(question)).get(0));
    }

    @Override
    public ServiceResult<Void> deleteQuestionInApplicationSection(Long questionId) {
        //Perform checks

        questionTemplatePersistorService.deleteEntityById(questionId);

        return serviceSuccess();
    }
}
