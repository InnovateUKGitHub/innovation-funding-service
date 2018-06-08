package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public class SummaryViewModel {

    private final ApplicationResource currentApplication;
    private final Map<Long, SectionResource> sections;
    private final Map<Long, List<QuestionResource>> sectionQuestions;
    private final ApplicationAssessmentAggregateResource scores;
    private final Future<Set<Long>> markedAsComplete;
    private final  Map<Long, List<FormInputResource>> questionFormInputs;

    public SummaryViewModel(ApplicationResource currentApplication,
                            Map<Long, SectionResource> sections,
                            Map<Long, List<QuestionResource>> sectionQuestions,
                            ApplicationAssessmentAggregateResource scores,
                            Future<Set<Long>> markedAsComplete,
                            Map<Long, List<FormInputResource>> questionFormInputs) {
        this.currentApplication = currentApplication;
        this.sections = sections;
        this.sectionQuestions = sectionQuestions;
        this.scores = scores;
        this.markedAsComplete = markedAsComplete;
        this.questionFormInputs = questionFormInputs;
    }


    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public Map<Long, SectionResource> getSections() {
        return sections;
    }

    public Map<Long, List<QuestionResource>> getSectionQuestions() {
        return sectionQuestions;
    }

    public ApplicationAssessmentAggregateResource getScores() {
        return scores;
    }

    public Future<Set<Long>> getMarkedAsComplete() {
        return markedAsComplete;
    }

    public Map<Long, List<FormInputResource>> getQuestionFormInputs() {
        return questionFormInputs;
    }
}
