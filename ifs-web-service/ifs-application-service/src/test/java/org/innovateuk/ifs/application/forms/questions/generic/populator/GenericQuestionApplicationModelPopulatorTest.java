package org.innovateuk.ifs.application.forms.questions.generic.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.generic.viewmodel.GenericQuestionApplicationViewModel;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResponseResourceBuilder.newApplicantFormInputResponseResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionStatusResourceBuilder.newApplicantQuestionStatusResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericQuestionApplicationModelPopulatorTest {

    @InjectMocks
    private GenericQuestionApplicationModelPopulator populator;

    @Mock
    private AssignButtonsPopulator assignButtonsPopulator;

    @Test
    public void populate() {
        ZonedDateTime now = ZonedDateTime.now();
        ApplicantQuestionResource applicantQuestion = newApplicantQuestionResource()
                .withQuestion(
                        newQuestionResource()
                                .withQuestionNumber("1")
                                .withDescription("desc")
                                .withDescription2("desc2")
                                .withShortName("short")
                                .withName("name")
                                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                                .build()
                )
                .withApplication(
                        newApplicationResource()
                                .withName("Application")
                                .build()
                )
                .withCompetition(
                        newCompetitionResource().build()
                )
                .withCurrentApplicant(
                        newApplicantResource()
                                .withProcessRole(newProcessRoleResource().withRole(LEADAPPLICANT).build())
                                .build()
                )
                .withCurrentUser(
                        newUserResource().build()
                )
                .withApplicantQuestionStatuses(
                        newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().build())
                                .build(1)
                )
                .withApplicants(
                        asList(
                                newApplicantResource()
                                        .withProcessRole(
                                                newProcessRoleResource()
                                                        .withRole(LEADAPPLICANT).build())
                                        .withOrganisation(newOrganisationResource()
                                                .build())
                                        .build()))
                .withApplicantFormInputs(asList(
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.TEXTAREA)
                                        .withGuidanceAnswer("Guidance")
                                        .withGuidanceTitle("Title")
                                        .withWordCount(500)
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource().withValue("Two words")
                                                .withFormInputMaxWordCount(500)
                                                .withUpdateDate(now)
                                                .withUpdatedByUser(2L)
                                                .withUpdatedByUserName("Bob")
                                                .build())
                                        .build(1))
                                .build(),
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.FILEUPLOAD)
                                        .withGuidanceAnswer("Appendix guidance")
                                        .withAllowedFileTypes(singleton(FileTypeCategory.PDF)).build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource()
                                                .withFileEntries(newFileEntryResource()
                                                        .withName("Appendix1.pdf", "Appendix2.pdf")
                                                        .build(2))
                                                .withUpdateDate(now.minusDays(2))
                                                .build())
                                        .build(1))
                                .build(),
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource().withType(FormInputType.TEMPLATE_DOCUMENT)
                                        .withDescription("Template")
                                        .withFile(newFileEntryResource().withName("template.odt").build())
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource()
                                                .withFileEntries(newFileEntryResource()
                                                        .withId(999L)
                                                        .withName("templateresponse.pdf")
                                                        .build(1))
                                                .withUpdateDate(now.minusDays(2))
                                                .build())
                                        .build(1))
                                .build()
                ))
                .build();

        AssignButtonsViewModel assignButtonsViewModel = mock(AssignButtonsViewModel.class);
        when(assignButtonsPopulator.populate(applicantQuestion, applicantQuestion, false)).thenReturn(assignButtonsViewModel);
        when(assignButtonsViewModel.getAssignableApplicants()).thenReturn(newProcessRoleResource().build(1));

        GenericQuestionApplicationViewModel viewModel = populator.populate(applicantQuestion, Optional.empty());

        assertEquals(applicantQuestion.getApplication().getId(), viewModel.getApplicationId());
        assertEquals((long) applicantQuestion.getQuestion().getId(), viewModel.getQuestionId());
        assertEquals((long) applicantQuestion.getCurrentUser().getId(), viewModel.getCurrentUser());

        assertEquals("Application", viewModel.getApplicationName());
        assertEquals("short", viewModel.getQuestionName());
        assertEquals("1", viewModel.getQuestionNumber());
        assertEquals("name", viewModel.getQuestionSubtitle());
        assertEquals("desc", viewModel.getQuestionDescription());
        assertEquals("desc2", viewModel.getQuestionDescription2());
        assertEquals("Title", viewModel.getQuestionGuidanceTitle());
        assertEquals("Guidance", viewModel.getQuestionGuidance());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, viewModel.getQuestionType());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(0).getFormInput().getId(), viewModel.getTextAreaFormInputId());
        assertEquals((Integer) 500, viewModel.getWordCount());
        assertEquals((Integer) 498, viewModel.getWordsLeft());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(1).getFormInput().getId(), viewModel.getAppendixFormInputId());
        assertEquals("Appendix guidance", viewModel.getAppendixGuidance());
        assertEquals(singleton(FileTypeCategory.PDF), viewModel.getAppendixAllowedFileTypes());
        assertEquals("Appendix1.pdf", viewModel.getAppendices().get(0).getFilename());
        assertEquals("Appendix2.pdf", viewModel.getAppendices().get(1).getFilename());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(2).getFormInput().getId(), viewModel.getTemplateDocumentFormInputId());
        assertEquals("Template", viewModel.getTemplateDocumentTitle());
        assertEquals("template.odt", viewModel.getTemplateDocumentFilename());
        assertEquals("templateresponse.pdf", viewModel.getTemplateDocumentResponseFilename());
        assertEquals((Long) 999L, viewModel.getTemplateDocumentResponseFileEntryId());

        assertEquals(toUkTimeZone(now), viewModel.getLastUpdated());
        assertEquals("Bob", viewModel.getLastUpdatedByName());
        assertEquals((Long) 2L, viewModel.getLastUpdatedBy());

        assertFalse(viewModel.isOpen());
        assertFalse(viewModel.isComplete());
        assertTrue(viewModel.isLeadApplicant());

        assertEquals(assignButtonsViewModel, viewModel.getAssignButtonsViewModel());

        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.shouldDisplayQuestionNumber());
        assertTrue(viewModel.hasResponse());
        assertFalse(viewModel.isRespondedByCurrentUser());
        assertEquals(" by Bob", viewModel.getLastUpdatedText());
        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.isSingleApplicant());
        assertTrue(viewModel.isTextAreaActive());
        assertTrue(viewModel.isAppendixActive());
        assertTrue(viewModel.isTemplateDocumentActive());
    }

    @Test
    public void populateForMultipleChoiceOptions() {
        ZonedDateTime now = ZonedDateTime.now();
        Long multipleChoiceOptionId = 1L;
        String multipleChoiceOptionText = "Yes";
        MultipleChoiceOptionResource multipleChoiceOption = new MultipleChoiceOptionResource(multipleChoiceOptionId, multipleChoiceOptionText);

        ApplicantQuestionResource applicantQuestion = newApplicantQuestionResource()
                .withQuestion(
                        newQuestionResource()
                                .withQuestionNumber("1")
                                .withDescription("desc")
                                .withShortName("short")
                                .withName("name")
                                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                                .build()
                )
                .withApplication(
                        newApplicationResource()
                                .withName("Application")
                                .build()
                )
                .withCompetition(
                        newCompetitionResource().build()
                )
                .withCurrentApplicant(
                        newApplicantResource()
                                .withProcessRole(newProcessRoleResource().withRole(LEADAPPLICANT).build())
                                .build()
                )
                .withCurrentUser(
                        newUserResource().build()
                )
                .withApplicantQuestionStatuses(
                        newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().build())
                                .build(1)
                )
                .withApplicantFormInputs(asList(
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.MULTIPLE_CHOICE)
                                        .withMultipleChoiceOptions(Collections.singletonList(multipleChoiceOption))
                                        .withGuidanceAnswer("Guidance")
                                        .withGuidanceTitle("Title")
                                        .withWordCount(500)
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource().withValue(multipleChoiceOptionText)
                                                .withMultipleChoiceOptionId(multipleChoiceOptionId)
                                                .withMultipleChoiceOptionText(multipleChoiceOptionText)
                                                .withUpdateDate(now)
                                                .withUpdatedByUser(2L)
                                                .withUpdatedByUserName("Bob")
                                                .build())
                                        .build(1))
                                .build()
                ))
                .withApplicants(
                        asList(
                                newApplicantResource()
                                        .withProcessRole(
                                                newProcessRoleResource()
                                                        .withRole(LEADAPPLICANT).build())
                                        .withOrganisation(newOrganisationResource()
                                                .build())
                                .build())
                )
                .build();

        AssignButtonsViewModel assignButtonsViewModel = mock(AssignButtonsViewModel.class);
        when(assignButtonsPopulator.populate(applicantQuestion, applicantQuestion, false)).thenReturn(assignButtonsViewModel);
        when(assignButtonsViewModel.getAssignableApplicants()).thenReturn(newProcessRoleResource().build(1));

        GenericQuestionApplicationViewModel viewModel = populator.populate(applicantQuestion, Optional.empty());

        assertEquals(applicantQuestion.getApplication().getId(), viewModel.getApplicationId());
        assertEquals((long) applicantQuestion.getQuestion().getId(), viewModel.getQuestionId());
        assertEquals((long) applicantQuestion.getCurrentUser().getId(), viewModel.getCurrentUser());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(0).getFormInput().getId(), viewModel.getMultipleChoiceFormInputId());
        assertEquals(multipleChoiceOptionId, viewModel.getSelectedMultipleChoiceOption().getId());
        assertEquals(multipleChoiceOptionText, viewModel.getSelectedMultipleChoiceOption().getText());

        assertEquals(toUkTimeZone(now), viewModel.getLastUpdated());
        assertEquals("Bob", viewModel.getLastUpdatedByName());
        assertEquals((Long) 2L, viewModel.getLastUpdatedBy());

        assertFalse(viewModel.isOpen());
        assertFalse(viewModel.isComplete());
        assertTrue(viewModel.isLeadApplicant());

        assertEquals(assignButtonsViewModel, viewModel.getAssignButtonsViewModel());

        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.shouldDisplayQuestionNumber());
        assertTrue(viewModel.hasResponse());
        assertFalse(viewModel.isRespondedByCurrentUser());
        assertEquals(" by Bob", viewModel.getLastUpdatedText());
        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.isSingleApplicant());
        assertFalse(viewModel.isTextAreaActive());
        assertTrue(viewModel.isMultipleChoiceOptionsActive());
    }

    @Test
    public void populateForNoQuestionNumberShownForExpressionOfInterestApplication() {
        ZonedDateTime now = ZonedDateTime.now();
        long eoiApplicationId = 3L;
        ApplicantQuestionResource applicantQuestion = newApplicantQuestionResource()
                .withQuestion(
                        newQuestionResource()
                                .withQuestionNumber("1")
                                .withDescription("Description")
                                .withDescription2("Description 2")
                                .withShortName("Short")
                                .withName("Name")
                                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                                .build()
                )
                .withApplication(
                        newApplicationResource()
                                .withName("Application")
                                .withApplicationExpressionOfInterestConfigResource(
                                        newApplicationExpressionOfInterestConfigResource()
                                                .withEnabledForExpressionOfInterest(true)
                                                .withEoiApplicationId(eoiApplicationId)
                                                .build())
                                .build()
                )
                .withCompetition(
                        newCompetitionResource().build()
                )
                .withCurrentApplicant(
                        newApplicantResource()
                                .withProcessRole(newProcessRoleResource().withRole(LEADAPPLICANT).build())
                                .build()
                )
                .withCurrentUser(
                        newUserResource().build()
                )
                .withApplicantQuestionStatuses(
                        newApplicantQuestionStatusResource()
                                .withStatus(newQuestionStatusResource().build())
                                .build(1)
                )
                .withApplicants(
                        List.of(
                                newApplicantResource()
                                        .withProcessRole(
                                                newProcessRoleResource()
                                                        .withRole(LEADAPPLICANT).build())
                                        .withOrganisation(newOrganisationResource()
                                                .build())
                                        .build()))
                .withApplicantFormInputs(asList(
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.TEXTAREA)
                                        .withGuidanceAnswer("Guidance")
                                        .withGuidanceTitle("Title")
                                        .withWordCount(500)
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource().withValue("Two words")
                                                .withFormInputMaxWordCount(500)
                                                .withUpdateDate(now)
                                                .withUpdatedByUser(2L)
                                                .withUpdatedByUserName("Bob")
                                                .build())
                                        .build(1))
                                .build(),
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.FILEUPLOAD)
                                        .withGuidanceAnswer("Appendix guidance")
                                        .withAllowedFileTypes(singleton(FileTypeCategory.PDF)).build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource()
                                                .withFileEntries(newFileEntryResource()
                                                        .withName("Appendix1.pdf", "Appendix2.pdf")
                                                        .build(2))
                                                .withUpdateDate(now.minusDays(2))
                                                .build())
                                        .build(1))
                                .build(),
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource().withType(FormInputType.TEMPLATE_DOCUMENT)
                                        .withDescription("Template")
                                        .withFile(newFileEntryResource().withName("template.odt").build())
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource()
                                                .withFileEntries(newFileEntryResource()
                                                        .withId(999L)
                                                        .withName("templateresponse.pdf")
                                                        .build(1))
                                                .withUpdateDate(now.minusDays(2))
                                                .build())
                                        .build(1))
                                .build()
                ))
                .build();

        AssignButtonsViewModel assignButtonsViewModel = mock(AssignButtonsViewModel.class);
        when(assignButtonsPopulator.populate(applicantQuestion, applicantQuestion, false)).thenReturn(assignButtonsViewModel);
        when(assignButtonsViewModel.getAssignableApplicants()).thenReturn(newProcessRoleResource().build(1));

        GenericQuestionApplicationViewModel viewModel = populator.populate(applicantQuestion, Optional.empty());

        assertEquals(applicantQuestion.getApplication().getId(), viewModel.getApplicationId());
        assertEquals((long) applicantQuestion.getQuestion().getId(), viewModel.getQuestionId());
        assertEquals((long) applicantQuestion.getCurrentUser().getId(), viewModel.getCurrentUser());

        assertEquals("Application", viewModel.getApplicationName());
        assertEquals("Short", viewModel.getQuestionName());
        assertEquals("1", viewModel.getQuestionNumber());
        assertEquals("Name", viewModel.getQuestionSubtitle());
        assertEquals("Description", viewModel.getQuestionDescription());
        assertEquals("Description 2", viewModel.getQuestionDescription2());
        assertEquals("Title", viewModel.getQuestionGuidanceTitle());
        assertEquals("Guidance", viewModel.getQuestionGuidance());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, viewModel.getQuestionType());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(0).getFormInput().getId(), viewModel.getTextAreaFormInputId());
        assertEquals((Integer) 500, viewModel.getWordCount());
        assertEquals((Integer) 498, viewModel.getWordsLeft());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(1).getFormInput().getId(), viewModel.getAppendixFormInputId());
        assertEquals("Appendix guidance", viewModel.getAppendixGuidance());
        assertEquals(singleton(FileTypeCategory.PDF), viewModel.getAppendixAllowedFileTypes());
        assertEquals("Appendix1.pdf", viewModel.getAppendices().get(0).getFilename());
        assertEquals("Appendix2.pdf", viewModel.getAppendices().get(1).getFilename());

        assertEquals(applicantQuestion.getApplicantFormInputs().get(2).getFormInput().getId(), viewModel.getTemplateDocumentFormInputId());
        assertEquals("Template", viewModel.getTemplateDocumentTitle());
        assertEquals("template.odt", viewModel.getTemplateDocumentFilename());
        assertEquals("templateresponse.pdf", viewModel.getTemplateDocumentResponseFilename());
        assertEquals((Long) 999L, viewModel.getTemplateDocumentResponseFileEntryId());

        assertEquals(toUkTimeZone(now), viewModel.getLastUpdated());
        assertEquals("Bob", viewModel.getLastUpdatedByName());
        assertEquals((Long) 2L, viewModel.getLastUpdatedBy());

        assertFalse(viewModel.isOpen());
        assertFalse(viewModel.isComplete());
        assertTrue(viewModel.isLeadApplicant());

        assertEquals(assignButtonsViewModel, viewModel.getAssignButtonsViewModel());

        assertTrue(viewModel.isReadOnly());
        assertFalse(viewModel.shouldDisplayQuestionNumber());
        assertTrue(viewModel.hasResponse());
        assertFalse(viewModel.isRespondedByCurrentUser());
        assertEquals(" by Bob", viewModel.getLastUpdatedText());
        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.isSingleApplicant());
        assertTrue(viewModel.isTextAreaActive());
        assertTrue(viewModel.isAppendixActive());
        assertTrue(viewModel.isTemplateDocumentActive());
    }
}
