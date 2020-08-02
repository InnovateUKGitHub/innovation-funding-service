package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm.TypeOfQuestion;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;


public abstract class AbstractApplicationSectionUpdater<T extends AbstractQuestionForm> extends AbstractSectionUpdater {

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return APPLICATION_FORM;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        T form = (T) competitionSetupForm;
        mapGuidanceRows(form);
        mapAppendix(form);
        if (form.getTypeOfQuestion() != null) {
            if (form.getTypeOfQuestion() == TypeOfQuestion.FREE_TEXT) {
                form.getQuestion().setTextArea(true);
                form.getQuestion().setMultipleChoice(false);
            } else if (form.getTypeOfQuestion() == TypeOfQuestion.MULTIPLE_CHOICE) {
                form.getQuestion().setTextArea(false);
                form.getQuestion().setMultipleChoice(true);
            }
        }
        return questionSetupCompetitionRestService.save(form.getQuestion()).toServiceResult();
    }

    protected abstract void mapAppendix(T form);

    protected abstract void mapGuidanceRows(T form);

}
