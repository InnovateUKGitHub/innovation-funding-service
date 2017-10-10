package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * TODO: Add description
 */
@Service
public class QuestionSetupTemplateServiceImpl implements QuestionSetupTemplateService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionTemplatePersistor questionTemplatePersistor;

    @Autowired
    private DefaultApplicationQuestionFactory defaultApplicationQuestionFactory;

    @Override
    public ServiceResult<Question> createDefaultForApplicationSection(Competition competition) {
        Section applicationQuestionsSection = sectionRepository.findByCompetitionIdAndName(competition.getId(), "Application questions");
        Question question = defaultApplicationQuestionFactory.buildQuestion(competition);
        question.setSection(applicationQuestionsSection);

        return serviceSuccess(questionTemplatePersistor.persistByEntity(Arrays.asList(question)).get(0));
    }
}
