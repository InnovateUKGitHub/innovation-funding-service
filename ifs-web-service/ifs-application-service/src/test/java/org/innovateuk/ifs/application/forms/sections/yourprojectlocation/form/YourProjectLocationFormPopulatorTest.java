package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form;

import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class YourProjectLocationFormPopulatorTest {

    @InjectMocks
    private YourProjectLocationFormPopulator populator;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestServiceMock;

    @Test
    public void populate() {

        long applicationId = 123L;
        long organisationId = 456L;
        String postcode = "S2 5AB";

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).
                thenReturn(restSuccess(newApplicationFinanceResource().withWorkPostcode(postcode).build()));

        YourProjectLocationForm form = populator.populate(applicationId, organisationId);

        assertThat(form.getPostcode()).isEqualTo(postcode);

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
    }
}
