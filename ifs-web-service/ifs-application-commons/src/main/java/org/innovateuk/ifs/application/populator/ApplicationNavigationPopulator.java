package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationNavigationPopulator {
    private static final String SECTION_URL = "/section/";
    private static final String QUESTION_URL = "/question/";
    private static final String APPLICATION_BASE_URL = "/application/";
    private static final String FORM_URL = "/form";
    private static final String BACK_TITLE = "backTitle";
    private static final String BACK_URL = "backURL";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    public NavigationViewModel addNavigation(SectionResource section, Long applicationId) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();

        if (section == null) {
            return navigationViewModel;
        }
        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestionBySection(section.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, navigationViewModel);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestionBySection(section.getId());
        addNextQuestionToModel(nextQuestion, applicationId, navigationViewModel);

        return navigationViewModel;
    }

    public NavigationViewModel addNavigation(QuestionResource question, Long applicationId) {
        NavigationViewModel navigationViewModel = new NavigationViewModel();

        if (question == null) {
            return navigationViewModel;
        }

        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, navigationViewModel);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, navigationViewModel);

        return navigationViewModel;
    }

    private void addPreviousQuestionToModel(Optional<QuestionResource> question, long applicationId,
                                            NavigationViewModel navigationViewModel) {
        if (question.isPresent()) {
            final String previousUrl;
            final String previousText;

            final QuestionResource previousQuestion = question.get();
            final SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());

            if (previousSection.isQuestionGroup()) {
                previousUrl = APPLICATION_BASE_URL + applicationId + FORM_URL + SECTION_URL + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = APPLICATION_BASE_URL + applicationId + FORM_URL + QUESTION_URL + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }

            navigationViewModel.setPreviousUrl(previousUrl);
            navigationViewModel.setPreviousText(previousText);
        }
    }

    private void addNextQuestionToModel(Optional<QuestionResource> nextQuestionOptional, long applicationId,
                                        NavigationViewModel navigationViewModel) {
        if (nextQuestionOptional.isPresent()) {
            final String nextUrl;
            final String nextText;

            final QuestionResource nextQuestion = nextQuestionOptional.get();
            final SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = APPLICATION_BASE_URL + applicationId + FORM_URL + SECTION_URL + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = APPLICATION_BASE_URL + applicationId + FORM_URL + QUESTION_URL + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            navigationViewModel.setNextUrl(nextUrl);
            navigationViewModel.setNextText(nextText);
        }
    }
}
