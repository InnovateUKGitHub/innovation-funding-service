package org.innovateuk.ifs.management.registration.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.management.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.management.registration.service.StakeholderServiceImpl;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.stakeholder.builder.StakeholderRegistrationResourceBuilder.newStakeholderRegistrationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class StakeholderServiceImplTest extends BaseServiceUnitTest<StakeholderServiceImpl> {

    @Mock
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestServiceMock;

    @Test
    public void createStakeholder() {
        StakeholderRegistrationForm registrationForm = new StakeholderRegistrationForm("Billy", "Ocean", "Passw0rd");
        StakeholderRegistrationResource stakeholderRegistrationResource =
                newStakeholderRegistrationResource()
                        .withFirstName(registrationForm.getFirstName())
                        .withLastName(registrationForm.getLastName())
                        .withPassword(registrationForm.getPassword())
                        .build();

        when(competitionSetupStakeholderRestServiceMock.createStakeholder("hash1234", stakeholderRegistrationResource)).thenReturn(RestResult.restSuccess());
        ServiceResult<Void> result = service.createStakeholder("hash1234", registrationForm);
        assertTrue(result.isSuccess());
    }

    @Override
    protected StakeholderServiceImpl supplyServiceUnderTest()
    {
        return new StakeholderServiceImpl();
    }
}
