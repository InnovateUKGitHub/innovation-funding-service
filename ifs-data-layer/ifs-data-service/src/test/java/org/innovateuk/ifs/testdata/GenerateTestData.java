package org.innovateuk.ifs.testdata;


import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Generates web test data based upon csvs in /src/test/resources/testdata using data builders
 */
public class GenerateTestData extends BaseGenerateTestData {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    protected boolean cleanDbFirst() {
        return true;
    }

    /**
     * We might need to fix up the database before we start generating data.
     * This can happen if for example we have pushed something out to live that would make this generation step fail.
     * Note that if we make a fix is made here it will most likely need a corresponding fix in sql script
     * VX_Y_Z__Remove_old_competition.sql
     * To repeat the fix when running up the full flyway mechanism
     */
    @Override
    public void fixUpDatabase() {
        correctGenericCompetitionQuestionDetails();
    }

    private void correctGenericCompetitionQuestionDetails() {
        Competition competition = competitionRepository.findByName("Template for the Generic competition type").get(0);
        List<Question> questions = questionRepository.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competition.getId(), "Application questions");
        Question question = questions.get(0);
        question.setName("Generic question heading");
        question.setDescription("Generic question description");
        questionRepository.save(question);
    }
}
