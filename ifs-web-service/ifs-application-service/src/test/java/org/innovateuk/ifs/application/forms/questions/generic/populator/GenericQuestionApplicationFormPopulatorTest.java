package org.innovateuk.ifs.application.forms.questions.generic.populator;

import org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.application.forms.questions.generic.form.GenericQuestionApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.MultipleChoiceOptionResource;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResourceBuilder.newApplicantFormInputResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantFormInputResponseResourceBuilder.newApplicantFormInputResponseResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenericQuestionApplicationFormPopulatorTest {

    private GenericQuestionApplicationForm genericQuestionApplicationForm;

    private GenericQuestionApplicationFormPopulator genericQuestionApplicationFormPopulator;

    @Before
    public void setup() {
        genericQuestionApplicationForm = new GenericQuestionApplicationForm();
        genericQuestionApplicationFormPopulator = new GenericQuestionApplicationFormPopulator();
    }

    @Test
    public void populate() {

        String value = "Two words";

        ApplicantQuestionResource applicantQuestion = newApplicantQuestionResource()
                .withApplicantFormInputs(asList(
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.TEXTAREA)
                                        .withGuidanceAnswer("Guidance")
                                        .withGuidanceTitle("Title")
                                        .withWordCount(500)
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource().withValue(value)
                                                .withFormInputMaxWordCount(500)
                                                .withUpdateDate(ZonedDateTime.now())
                                                .withUpdatedByUser(2L)
                                                .withUpdatedByUserName("Bob")
                                                .build())
                                        .build(1))
                                .build()))
                .build();

        genericQuestionApplicationFormPopulator.populate(genericQuestionApplicationForm, Optional.empty(), applicantQuestion);

        assertTrue(genericQuestionApplicationForm.isTextAreaActive());
        assertEquals(value, genericQuestionApplicationForm.getAnswer());
    }

    @Test
    public void populateMultipleChoiceOptions() {

        Long multipleChoiceOptionId = 1L;
        String multipleChoiceOptionText = "Option 1";
        MultipleChoiceOptionResource multipleChoiceOption = new MultipleChoiceOptionResource(multipleChoiceOptionId, multipleChoiceOptionText);

        ApplicantQuestionResource applicantQuestion = newApplicantQuestionResource()
                .withApplicantFormInputs(asList(
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.MULTIPLE_CHOICE)
                                        .withMultipleChoiceOptions(Collections.singletonList(multipleChoiceOption))
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withResponse(newFormInputResponseResource()
                                                .withMultipleChoiceOptionId(multipleChoiceOptionId)
                                                .withMultipleChoiceOptionText(multipleChoiceOptionText)
                                                .build())
                                        .build(1))
                                .build()))
                .build();

        genericQuestionApplicationFormPopulator.populate(genericQuestionApplicationForm, Optional.empty(), applicantQuestion);

        assertTrue(genericQuestionApplicationForm.isMultipleChoiceOptionsActive());
        assertEquals(multipleChoiceOptionId, genericQuestionApplicationForm.getMultipleChoiceOptionId());
    }

    @Test
    public void populateMultipleChoiceOptionsWithMultipleOrganisations() {

        Long multipleChoiceOptionId = 1L;
        String multipleChoiceOptionText = "Option 1";
        Long organisationId = 3L;

        Long otherMultipleChoiceOptionId = 6L;
        String otherMultipleChoiceOptionText = "Option 2";
        Long otherOrganisationId = 7L;
        MultipleChoiceOptionResource multipleChoiceOption = new MultipleChoiceOptionResource(multipleChoiceOptionId, multipleChoiceOptionText);

        ApplicantQuestionResource applicantQuestion = newApplicantQuestionResource()
                .withApplicantFormInputs(asList(
                        newApplicantFormInputResource()
                                .withFormInput(newFormInputResource()
                                        .withType(FormInputType.MULTIPLE_CHOICE)
                                        .withMultipleChoiceOptions(Collections.singletonList(multipleChoiceOption))
                                        .build())
                                .withApplicantResponses(newApplicantFormInputResponseResource()
                                        .withApplicant(newApplicantResource()
                                                .withOrganisation(newOrganisationResource()
                                                        .withId(otherOrganisationId)
                                                        .build()).build(),
                                            newApplicantResource()
                                                    .withOrganisation(newOrganisationResource()
                                                            .withId(organisationId)
                                                            .build()).build())
                                        .withResponse(newFormInputResponseResource()
                                                    .withMultipleChoiceOptionId(otherMultipleChoiceOptionId)
                                                    .withMultipleChoiceOptionText(otherMultipleChoiceOptionText)
                                                    .build(),
                                                newFormInputResponseResource()
                                                        .withMultipleChoiceOptionId(multipleChoiceOptionId)
                                                        .withMultipleChoiceOptionText(multipleChoiceOptionText)
                                                        .build())
                                        .build(2))
                                .build()))
                .build();

        genericQuestionApplicationFormPopulator.populate(genericQuestionApplicationForm, Optional.of(organisationId), applicantQuestion);

        assertTrue(genericQuestionApplicationForm.isMultipleChoiceOptionsActive());
        assertEquals(multipleChoiceOptionId, genericQuestionApplicationForm.getMultipleChoiceOptionId());
    }
}
