package com.worth.ifs.bankdetails.service;

import com.worth.ifs.bankdetails.BankDetailsService;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankDetailsServiceImplTest {

    @InjectMocks
    private BankDetailsService service;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Test
    public void testGetById() {
        ProjectResource projectResource = newProjectResource().build();
        BankDetailsResource bankDetailsResource = newBankDetailsResource().build();

        when(bankDetailsRestService.getById(projectResource.getId(), bankDetailsResource.getId())).thenReturn(restSuccess(bankDetailsResource));

        BankDetailsResource returnedBankDetailsResource = service.getByProjectIdAndBankDetailsId(projectResource.getId(), bankDetailsResource.getId());

        assertEquals(bankDetailsResource, returnedBankDetailsResource);

        verify(bankDetailsRestService).getById(projectResource.getId(), bankDetailsResource.getId());
    }

    @Test
    public ServiceResult<Void> updateBankDetails(Long projectId, BankDetailsResource bankDetailsResource) {
        return bankDetailsRestService.updateBankDetails(projectId, bankDetailsResource).toServiceResult();
    }

    @Test
    public BankDetailsResource getBankDetailsByProjectAndOrganisation(Long projectId, Long organisationId) {
        return bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationId).getSuccessObjectOrThrowException();
    }
}
