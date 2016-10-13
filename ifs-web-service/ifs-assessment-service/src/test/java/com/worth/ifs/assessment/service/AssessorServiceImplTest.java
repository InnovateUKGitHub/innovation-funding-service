package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.form.registration.AssessorRegistrationForm;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
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
        String title = "Mr";
        String firstName = "Felix";
        String lastName = "Wilson";
        String phoneNumber = "12345678";
        Gender gender = Gender.MALE;
        EthnicityResource ethnicity = newEthnicityResource().build();
        Disability disability = Disability.NO;
        String password = "password";

        AssessorRegistrationForm form = new AssessorRegistrationForm();
        form.setTitle(title);
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setPhoneNumber(phoneNumber);
        form.setGender(gender);
        form.setEthnicity(ethnicity);
        form.setDisability(disability);
        form.setPassword(password);

        UserRegistrationResource userRegistration = newUserRegistrationResource()
                .withTitle(title)
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
