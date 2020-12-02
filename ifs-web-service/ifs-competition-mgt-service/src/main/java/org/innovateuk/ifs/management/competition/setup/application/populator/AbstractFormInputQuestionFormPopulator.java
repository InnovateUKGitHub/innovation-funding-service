package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm.TypeOfQuestion;

/**
 * Form populator for the application form competition setup section.
 */
public abstract class AbstractFormInputQuestionFormPopulator {

    public void populateCommon(CompetitionSetupQuestionResource questionResource, AbstractQuestionForm competitionSetupForm) {
        if (questionResource.getTextArea() != null && questionResource.getMultipleChoice() != null) {
            competitionSetupForm.setTypeOfQuestion(questionResource.getTextArea() ? TypeOfQuestion.FREE_TEXT : TypeOfQuestion.MULTIPLE_CHOICE);
        }
        if (questionResource.getMultipleChoice() != null) {
            if (questionResource.getChoices().size() == 0) {
                questionResource.getChoices().add(new MultipleChoiceOptionResource());
            }
            if (questionResource.getChoices().size() == 1) {
                questionResource.getChoices().add(new MultipleChoiceOptionResource());
            }
        }
    }


}
