package org.innovateuk.ifs.management.competition.setup.application.validator;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource.MultipleChoiceValidationGroup;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource.TextAreaValidationGroup;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm.TypeOfQuestion;
import org.innovateuk.ifs.management.competition.setup.application.form.GuidanceRowForm;
import org.innovateuk.ifs.management.competition.setup.application.form.ProjectForm;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

@Component
public class CompetitionSetupApplicationQuestionValidator {

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    public void validate(QuestionForm form, BindingResult bindingResult, long questionId) {
        validateRadioButtons(form, bindingResult);
        validateAssessmentGuidanceRows(form, bindingResult);
        validateFileUploaded(form, bindingResult, questionId);
        validateTypeOfQuestion(form, bindingResult);
    }

    public void validate(ProjectForm form, BindingResult bindingResult) {
        validateScopeGuidanceRows(form, bindingResult);
        validateTypeOfQuestion(form, bindingResult);
    }


    private void validateAssessmentGuidanceRows(QuestionForm applicationQuestionForm, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(applicationQuestionForm.getQuestion().getWrittenFeedback())) {
            ValidationUtils.invokeValidator(validator, applicationQuestionForm, bindingResult, GuidanceRowForm.GuidanceRowViewGroup.class);
        }
    }

    private void validateScopeGuidanceRows(ProjectForm applicationProjectForm, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(applicationProjectForm.getQuestion().getWrittenFeedback())) {
            ValidationUtils.invokeValidator(validator, applicationProjectForm, bindingResult, GuidanceRowResource.GuidanceRowGroup.class);
        }
    }

    private void validateAppendix(QuestionForm competitionSetupForm, BindingResult bindingResult) {
        if (competitionSetupForm.getNumberOfUploads() != null) {
            if (competitionSetupForm.getNumberOfUploads() > 0
                    && isNullOrEmpty(competitionSetupForm.getQuestion().getAppendixGuidance())) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.appendixGuidance", "This field cannot be left blank."));
            }
            if (competitionSetupForm.getNumberOfUploads() > 0
                    && competitionSetupForm.getQuestion().getAllowedAppendixResponseFileTypes().size() == 0) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.allowedAppendixResponseFileTypes", "This field cannot be left blank."));
            }
        }
    }

    private void validateRadioButtons(QuestionForm competitionSetupForm, BindingResult bindingResult) {
        if(competitionSetupForm.getNumberOfUploads() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "numberOfUploads", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getNumberOfUploads() >0 && competitionSetupForm.getQuestion().getAllowedAppendixResponseFileTypes().size() == 0) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.allowedAppendixResponseFileTypes", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getQuestion().getTemplateDocument() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.templateDocument", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getQuestion().getWrittenFeedback() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.writtenFeedback", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getQuestion().getScored() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.scored", "This field cannot be left blank."));
        }
    }

    private void validateFileUploaded(QuestionForm questionForm, BindingResult bindingResult, long questionId) {
        if (TRUE.equals(questionForm.getQuestion().getTemplateDocument())) {
            CompetitionSetupQuestionResource question = questionSetupCompetitionRestService.getByQuestionId(questionId).getSuccess();
            if (question.getTemplateFilename() == null) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "templateDocumentFile", "You must upload a file."));
            }
        }
    }

    private void validateTypeOfQuestion(AbstractQuestionForm form, BindingResult bindingResult) {
        if (form.getTypeOfQuestion() != null) {
            if (form.getTypeOfQuestion().equals(TypeOfQuestion.FREE_TEXT)) {
                validateTextArea(form, bindingResult);
            } else {
                validateMultipleChoice(form, bindingResult);
            }
        } else {
            if (TRUE.equals(form.getQuestion().getTextArea())) {
                validateTextArea(form, bindingResult);
            }
            if (TRUE.equals(form.getQuestion().getMultipleChoice())) {
                validateMultipleChoice(form , bindingResult);
            }
        }

    }
    private void validateTextArea(AbstractQuestionForm form, BindingResult bindingResult) {
        ValidationUtils.invokeValidator(validator, form, bindingResult, TextAreaValidationGroup.class);
    }

    private void validateMultipleChoice(AbstractQuestionForm form, BindingResult bindingResult) {
        ValidationUtils.invokeValidator(validator, form, bindingResult, MultipleChoiceValidationGroup.class);

        List<MultipleChoiceOptionResource> nonNullChoices = form.getQuestion().getChoices().stream().filter(choice -> !isNullOrEmpty(choice.getText())).collect(Collectors.toList());
        Multimap<String, MultipleChoiceOptionResource> indexedByText = Multimaps.index(nonNullChoices, choice -> choice.getText().toLowerCase().trim());

        indexedByText.asMap().entrySet().forEach(entry -> {
            if (entry.getValue().size() > 1) {
                entry.getValue().forEach(invalid -> {
                    int index = form.getQuestion().getChoices().indexOf(invalid);
                    bindingResult.rejectValue(String.format("question.choices[%d].text", index), "validation.competition.setup.multiple.choice.duplicate");
                });
            }
        });
    }

}
