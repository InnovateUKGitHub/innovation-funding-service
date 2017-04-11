package org.innovateuk.ifs.assessment.common.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.assessment.registration.service.AssessorService;
import org.innovateuk.ifs.assessment.registration.service.AssessorServiceImpl;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.Gender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
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
        super.setUp();
    }

    @Test
    public void createAssessorByInviteHash() throws Exception {
        String hash = "hash";
        String firstName = "Felix";
        String lastName = "Wilson";
        String phoneNumber = "12345678";
        Gender gender = Gender.MALE;
        EthnicityResource ethnicity = newEthnicityResource().build();
        Disability disability = Disability.NO;
        String password = "password";

        AssessorRegistrationForm form = new AssessorRegistrationForm();
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setPhoneNumber(phoneNumber);
        form.setGender(gender);
        form.setEthnicity(ethnicity);
        form.setDisability(disability);
        form.setPassword(password);

        UserRegistrationResource userRegistration = newUserRegistrationResource()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withPhoneNumber(phoneNumber)
                .withGender(gender)
                .withEthnicity(ethnicity)
                .withDisability(disability)
                .withPassword(password)
                .build();

        when(assessorRestService.createAssessorByInviteHash(hash, userRegistration)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.createAssessorByInviteHash(hash, form);

        assertTrue(response.isSuccess());
        verify(assessorRestService, only()).createAssessorByInviteHash(hash, userRegistration);
    }
}
