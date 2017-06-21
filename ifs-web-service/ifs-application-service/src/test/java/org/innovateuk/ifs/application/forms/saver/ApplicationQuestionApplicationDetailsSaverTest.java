package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests {@link ApplicationQuestionApplicationDetailsSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionApplicationDetailsSaverTest {

    @InjectMocks
    private ApplicationQuestionApplicationDetailsSaver detailsSaver;

    @Test
    public void handleApplicationDetailsValidationMessages() {
        final String fieldName = "MyField";

        ValidationMessages messages = new ValidationMessages();
        messages.setObjectName("target");

        messages.addError(fieldError(fieldName, "rejected", "errorKey"));
        List<ValidationMessages> applicationMessages = asList(messages);

        ValidationMessages result = detailsSaver.handleApplicationDetailsValidationMessages(applicationMessages);

        assertFalse(result.getFieldErrors("application." + fieldName).isEmpty());
    }

    @Test
    public void setApplicationDetails_updateName() {
        final ApplicationResource application = newApplicationResource().withName("oldName").build();
        final ApplicationResource updatedApplication = newApplicationResource().build();

        detailsSaver.setApplicationDetails(application, updatedApplication);
        assertEquals("oldName", application.getName());

        updatedApplication.setName("newName");
        detailsSaver.setApplicationDetails(application, updatedApplication);

        assertEquals("newName", application.getName());
    }

    @Test
    public void setApplicationDetails_updateStartDate() {
        final LocalDate oldDate = LocalDate.of(2015, 01, 20);
        final LocalDate newDate = LocalDate.of(LocalDate.now().getYear() + 1, 01, 20);
        final ApplicationResource application = newApplicationResource().withStartDate(oldDate).build();
        final ApplicationResource updatedApplication = newApplicationResource().build();

        detailsSaver.setApplicationDetails(application, updatedApplication);
        assertEquals(null, application.getStartDate());

        updatedApplication.setStartDate(LocalDate.MIN);
        detailsSaver.setApplicationDetails(application, updatedApplication);
        assertEquals(null, application.getStartDate());

        updatedApplication.setStartDate(LocalDate.now().minusDays(1));
        detailsSaver.setApplicationDetails(application, updatedApplication);
        assertEquals(null, application.getStartDate());

        updatedApplication.setStartDate(newDate);
        detailsSaver.setApplicationDetails(application, updatedApplication);

        assertEquals(newDate, application.getStartDate());
    }

    @Test
    public void setApplicationDetails_updateResubmissionDetails() {
        final ApplicationResource application = newApplicationResource()
                .with(applicationResource -> {
                    applicationResource.setResubmission(true);
                    applicationResource.setPreviousApplicationNumber("1");
                    applicationResource.setPreviousApplicationTitle("OldApplication");
                })
                .build();
        final ApplicationResource updatedApplication = newApplicationResource()
                .with(applicationResource -> applicationResource.setResubmission(false))
                .build();


        detailsSaver.setApplicationDetails(application, updatedApplication);

        assertEquals(false, application.getResubmission());
        assertEquals(null, application.getPreviousApplicationNumber());
        assertEquals(null, application.getPreviousApplicationTitle());

        updatedApplication.setResubmission(true);
        updatedApplication.setPreviousApplicationNumber("23");
        updatedApplication.setPreviousApplicationTitle("someOldie");
        detailsSaver.setApplicationDetails(application, updatedApplication);

        assertEquals(true, application.getResubmission());
        assertEquals("23", application.getPreviousApplicationNumber());
        assertEquals("someOldie", application.getPreviousApplicationTitle());
    }

    @Test
    public void setApplicationDetails_updateDurationInMonths() {
        final ApplicationResource application = newApplicationResource().withDurationInMonths(2L).build();
        final ApplicationResource updatedApplication = newApplicationResource().build();

        detailsSaver.setApplicationDetails(application, updatedApplication);
        assertEquals(null, application.getDurationInMonths());

        updatedApplication.setDurationInMonths(12L);
        detailsSaver.setApplicationDetails(application, updatedApplication);

        assertEquals(Long.valueOf(12), application.getDurationInMonths());
    }
}
