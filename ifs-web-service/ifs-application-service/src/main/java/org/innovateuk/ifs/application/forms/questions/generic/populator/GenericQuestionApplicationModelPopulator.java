package org.innovateuk.ifs.application.forms.questions.generic.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResource;
import org.innovateuk.ifs.applicant.resource.ApplicantFormInputResponseResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.generic.viewmodel.GenericQuestionApplicationViewModel;
import org.innovateuk.ifs.application.forms.questions.generic.viewmodel.GenericQuestionApplicationViewModel.GenericQuestionApplicationViewModelBuilder;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.forms.questions.generic.viewmodel.GenericQuestionApplicationViewModel.GenericQuestionApplicationViewModelBuilder.aGenericQuestionApplicationViewModel;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

@Component
public class GenericQuestionApplicationModelPopulator {

    @Autowired
    private AssignButtonsPopulator assignButtonsPopulator;

    public GenericQuestionApplicationViewModel populate(ApplicantQuestionResource applicantQuestion) {
        Map<FormInputType, ApplicantFormInputResource> formInputs = applicantQuestion.getApplicantFormInputs()
                .stream()
                .collect(toMap(input -> input.getFormInput().getType(), Function.identity()));
        QuestionResource question = applicantQuestion.getQuestion();
        ApplicationResource application = applicantQuestion.getApplication();
        CompetitionResource competition = applicantQuestion.getCompetition();

        GenericQuestionApplicationViewModelBuilder viewModelBuilder = aGenericQuestionApplicationViewModel();

        ofNullable(formInputs.get(FormInputType.TEXTAREA)).ifPresent(input -> buildTextAreaViewModel(viewModelBuilder, input));
        ofNullable(formInputs.get(FormInputType.FILEUPLOAD)).ifPresent(input -> buildAppendixViewModel(viewModelBuilder, input));
        ofNullable(formInputs.get(FormInputType.TEMPLATE_DOCUMENT)).ifPresent(input -> buildTemplateDocumentViewModel(viewModelBuilder, input));

        applicantQuestion.getApplicantFormInputs()
                .stream()
                .flatMap(input -> input.getApplicantResponses().stream())
                .map(ApplicantFormInputResponseResource::getResponse)
                .max(Comparator.comparing(FormInputResponseResource::getUpdateDate))
                .ifPresent(response -> viewModelBuilder.withLastUpdated(toUkTimeZone(response.getUpdateDate()))
                                                       .withLastUpdatedBy(response.getUpdatedByUser())
                                                       .withLastUpdatedByName(response.getUpdatedByUserName()));

        return viewModelBuilder.withApplicationId(application.getId())
                .withCompetitionName(competition.getName())
                .withApplicationName(application.getName())
                .withCurrentUser(applicantQuestion.getCurrentUser().getId())
                .withQuestionId(question.getId())
                .withQuestionName(question.getShortName())
                .withQuestionSubtitle(question.getName())
                .withQuestionDescription(question.getDescription())
                .withQuestionNumber(question.getQuestionNumber())
                .withQuestionType(question.getQuestionSetupType())
                .withComplete(applicantQuestion.isCompleteByApplicant(applicantQuestion.getCurrentApplicant()))
                .withOpen(application.isOpen() && competition.isOpen())
                .withLeadApplicant(applicantQuestion.getCurrentApplicant().isLead())
                .withAssignButtonsViewModel(assignButtonsPopulator.populate(applicantQuestion, applicantQuestion, false))
                .build();
    }

    private void buildTemplateDocumentViewModel(GenericQuestionApplicationViewModelBuilder viewModelBuilder, ApplicantFormInputResource input) {
        viewModelBuilder.withTemplateDocumentFormInputId(input.getFormInput().getId())
                .withTemplateDocumentTitle(input.getFormInput().getDescription())
                .withTemplateDocumentFilename(input.getFormInput().getFile().getName())
                .withTemplateDocumentResponseFilename(filenameResponseOrNull(input));
    }

    private void buildAppendixViewModel(GenericQuestionApplicationViewModelBuilder viewModelBuilder, ApplicantFormInputResource input) {
        viewModelBuilder.withAppendixFormInputId(input.getFormInput().getId())
                .withAppendixGuidance(input.getFormInput().getGuidanceAnswer())
                .withAppendixFilename(filenameResponseOrNull(input))
                .withAppendixAllowedFileTypes(input.getFormInput().getAllowedFileTypes());
    }

    private void buildTextAreaViewModel(GenericQuestionApplicationViewModelBuilder viewModelBuilder, ApplicantFormInputResource input) {
        viewModelBuilder.withTextAreaFormInputId(input.getFormInput().getId())
                .withWordCount(input.getFormInput().getWordCount())
                .withQuestionGuidanceTitle(input.getFormInput().getGuidanceTitle())
                .withQuestionGuidance(input.getFormInput().getGuidanceAnswer())
                .withWordsLeft(firstResponse(input).map(FormInputResponseResource::getWordCountLeft).orElse(input.getFormInput().getWordCount()));
    }

    private String filenameResponseOrNull(ApplicantFormInputResource input) {
        return firstResponse(input)
                .map(FormInputResponseResource::getFilename)
                .orElse(null);
    }

    private Optional<FormInputResponseResource> firstResponse(ApplicantFormInputResource input) {
        return input.getApplicantResponses()
                .stream()
                .findAny() //Generic questions only have one respsonse.
                .map(ApplicantFormInputResponseResource::getResponse);
    }

}
