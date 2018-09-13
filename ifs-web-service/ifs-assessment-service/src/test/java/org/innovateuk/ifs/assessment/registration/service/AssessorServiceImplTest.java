package org.innovateuk.ifs.assessment.registration.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessorServiceImplTest extends BaseServiceUnitTest<AssessorService> {

    @Mock
    private AssessorRestService assessorRestService;

    @Override
    protected AssessorService supplyServiceUnderTest() {
        return new AssessorServiceImpl();
    }

    @Before
    public void setUp() {
        super.setup();
    }

    @Test
    public void createAssessorByInviteHash() {
        String hash = "hash";
        String firstName = "Felix";
        String lastName = "Wilson";
        String phoneNumber = "12345678";
        String password = "password";

        AssessorRegistrationForm form = new AssessorRegistrationForm();
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setPhoneNumber(phoneNumber);
        form.setPassword(password);

        UserRegistrationResource userRegistration = newUserRegistrationResource()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withPhoneNumber(phoneNumber)
                .withPassword(password)
                .build();

        when(assessorRestService.createAssessorByInviteHash(hash, userRegistration)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.createAssessorByInviteHash(hash, form);

        assertTrue(response.isSuccess());
        verify(assessorRestService, only()).createAssessorByInviteHash(hash, userRegistration);
    }
}