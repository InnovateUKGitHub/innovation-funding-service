package org.innovateuk.ifs.bankdetails.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.bankdetails.BankDetailsService;
import org.innovateuk.ifs.bankdetails.BankDetailsServiceImpl;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankDetailsServiceImplTest extends BaseServiceUnitTest<BankDetailsService> {
    @Mock
    private BankDetailsRestService bankDetailsRestService;

    private ProjectResource projectResource;
    private BankDetailsResource bankDetailsResource;
    private OrganisationResource organisationResource;

    @Override
    protected BankDetailsService supplyServiceUnderTest() {
        return new BankDetailsServiceImpl(bankDetailsRestService);
    }

    @Before
    public void setUp(){
        projectResource = newProjectResource().build();
        bankDetailsResource = newBankDetailsResource().build();
        organisationResource = newOrganisationResource().build();
    }

    @Test
    public void testGetById() {
        when(bankDetailsRestService.getByProjectIdAndBankDetailsId(projectResource.getId(), bankDetailsResource.getId())).thenReturn(restSuccess(bankDetailsResource));

        BankDetailsResource returnedBankDetailsResource = service.getByProjectIdAndBankDetailsId(projectResource.getId(), bankDetailsResource.getId());

        assertEquals(bankDetailsResource, returnedBankDetailsResource);

        verify(bankDetailsRestService).getByProjectIdAndBankDetailsId(projectResource.getId(), bankDetailsResource.getId());
    }

    @Test
    public void updateBankDetails() {
        when(bankDetailsRestService.updateBankDetails(projectResource.getId(), bankDetailsResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateBankDetails(projectResource.getId(), bankDetailsResource);

        assertTrue(result.isSuccess());

        verify(bankDetailsRestService).updateBankDetails(projectResource.getId(), bankDetailsResource);
    }

    @Test
    public void submitBankDetails() {
        when(bankDetailsRestService.submitBankDetails(projectResource.getId(), bankDetailsResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.submitBankDetails(projectResource.getId(), bankDetailsResource);

        assertTrue(result.isSuccess());

        verify(bankDetailsRestService).submitBankDetails(projectResource.getId(), bankDetailsResource);
    }

    @Test
    public void getBankDetailsByProjectAndOrganisation() {
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));

        BankDetailsResource returnedBankDetailsResource = service.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId());

        assertEquals(bankDetailsResource, returnedBankDetailsResource);

        verify(bankDetailsRestService).getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId());
    }

    @Test
    public void downloadByCompetition(){
        Long competitionId = 123L;

        ByteArrayResource result = new ByteArrayResource("My content!".getBytes());

        when(bankDetailsRestService.downloadByCompetition(123L)).thenReturn(restSuccess(result));

        ByteArrayResource byteArrayResource = service.downloadByCompetition(competitionId);

        assertEquals(byteArrayResource, result);

        verify(bankDetailsRestService).downloadByCompetition(123L);
    }
}
