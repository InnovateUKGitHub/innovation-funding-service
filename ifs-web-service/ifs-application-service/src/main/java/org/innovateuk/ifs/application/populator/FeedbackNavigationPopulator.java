package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;

/**
 * Populator for the assessor feedback navigation view model.
 */
@Component
public class FeedbackNavigationPopulator {

    private static final String PATH = "/application/{applicationId}/question/{questionId}/feedback";

    @Autowired
    private QuestionService questionService;

    public NavigationViewModel addNavigation(QuestionResource question, long applicationId) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();

        if (question == null) {
            return navigationViewModel;
        }

        addPreviousQuestionToModel(questionService.getPreviousQuestion(question.getId()),
                question,
                applicationId,
                navigationViewModel);
        addNextQuestionToModel(questionService.getNextQuestion(question.getId()),
                question,
                applicationId,
                navigationViewModel);
        return navigationViewModel;
    }

    protected void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional,
                                              QuestionResource currentQuestion,
                                              long applicationId,
                                              NavigationViewModel navigationViewModel) {
        if (previousQuestionOptional.isPresent()) {
            QuestionResource previousQuestion = previousQuestionOptional.get();
            if (Objects.equals(currentQuestion.getSection(), previousQuestion.getSection())) {
                navigationViewModel.setPreviousUrl(
                        UriComponentsBuilder.fromPath(PATH)
                                .buildAndExpand(applicationId, previousQuestion.getId())
                                .toUriString());
                navigationViewModel.setPreviousText(previousQuestion.getShortName());
            }
        }
    }

    protected void addNextQuestionToModel(Optional<QuestionResource> optionalResource,
                                          QuestionResource currentQuestion,
                                          long applicationId,
                                          NavigationViewModel navigationViewModel) {
        if (optionalResource.isPresent()) {
            QuestionResource nextQuestion = optionalResource.get();
            if (Objects.equals(currentQuestion.getSection(), nextQuestion.getSection())) {
                navigationViewModel.setNextUrl(
                        UriComponentsBuilder.fromPath(PATH)
                                .buildAndExpand(applicationId, nextQuestion.getId())
                                .toUriString());
                navigationViewModel.setNextText(nextQuestion.getShortName());
            }
        }
    }
}
