package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InternalUserServiceImplTest extends BaseServiceUnitTest<InternalUserServiceImpl> {

    @Mock
    private UserRestService userRestServiceMock;

    @Test
    public void testCreateInternalUser(){
        InternalUserRegistrationForm registrationForm = new InternalUserRegistrationForm("Arden", "Pimenta", "Passw0rd");
        InternalUserRegistrationResource internalUserRegistrationResource =
                newInternalUserRegistrationResource()
                        .withFirstName(registrationForm.getFirstName())
                        .withLastName(registrationForm.getLastName())
                        .withPassword(registrationForm.getPassword())
                        .build();
        when(userRestServiceMock.createInternalUser("hash", internalUserRegistrationResource)).thenReturn(RestResult.restSuccess());
        ServiceResult<Void> result = service.createInternalUser("hash", registrationForm);
        assertTrue(result.isSuccess());
    }

    @Test
    public void editInternalUser() throws Exception {

        EditUserResource editUserResource = new EditUserResource();
        when(userRestServiceMock.editInternalUser(editUserResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.editInternalUser(editUserResource);

        assertTrue(result.isSuccess());
        verify(userRestServiceMock).editInternalUser(editUserResource);

    }

    @Override
    protected InternalUserServiceImpl supplyServiceUnderTest() {
        return new InternalUserServiceImpl();
    }
}
