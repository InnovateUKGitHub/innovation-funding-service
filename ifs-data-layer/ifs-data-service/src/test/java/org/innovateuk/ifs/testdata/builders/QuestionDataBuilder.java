package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.testdata.builders.data.QuestionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class QuestionDataBuilder extends BaseDataBuilder<QuestionData, QuestionDataBuilder> {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionData.class);

    private QuestionDataBuilder(List<BiConsumer<Integer, QuestionData>> multiActions,
                               ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    public QuestionDataBuilder updateApplicationQuestionHeading(int ordinal,
                                                  String competitionName,
                                                  String heading,
                                                  String title,
                                                  String subtitle) {
        return asCompAdmin(data -> {
            Competition competition = competitionRepository.findByName(competitionName).get(0);
                    List<Question> questions = questionRepository.findByCompetitionIdAndSectionNameOrderByPriorityAsc(competition.getId(), "Application questions");
                    Question question = questions.get(ordinal);
                    question.setName(heading);
                    question.setShortName(title);
                    question.setDescription(subtitle);

                    questionRepository.save(question);
                }
        );
    }

    @Override
    protected QuestionDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, QuestionData>> actions) {
        return new QuestionDataBuilder(actions, serviceLocator);
    }

    @Override
    protected QuestionData createInitial() {
        return new QuestionData();
    }

    @Override
    protected void postProcess(int index, QuestionData instance) {
        super.postProcess(index, instance);
        LOG.info("Created Question: ", instance.getQuestionResource().getName());
    }

    public static QuestionDataBuilder newQuestionData(ServiceLocator serviceLocator) {
        return new QuestionDataBuilder(emptyList(), serviceLocator);
    }

}
