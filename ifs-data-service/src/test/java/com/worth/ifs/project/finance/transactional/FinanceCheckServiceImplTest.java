package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.commons.error.CommonFailureKeys.FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW;
import static com.worth.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class FinanceCheckServiceImplTest extends BaseServiceUnitTest<FinanceCheckServiceImpl> {

    @Test
    public void testGenerateFinanceCheck(){
        // TODO RP
    }

    @Test
    public void testSaveFinanceCheck() {
        // TODO RP
    }

    @Test
    public void testGetFinanceCheck() {
        // TODO RP
    }

    @Test
    public void testApprove() {

        User loggedInUser = newUser().build();
        UserResource loggedInUserResource = newUserResource().withId(loggedInUser.getId()).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();

        when(userRepositoryMock.findOne(loggedInUserResource.getId())).thenReturn(loggedInUser);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(123L, 456L)).thenReturn(partnerOrganisation);
        when(financeCheckWorkflowHandlerMock.approveFinanceCheckFigures(partnerOrganisation, loggedInUser)).thenReturn(true);

        setLoggedInUser(loggedInUserResource);

        ServiceResult<Void> result = service.approve(123L, 456L);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testApproveButWorkflowStepFails() {

        User loggedInUser = newUser().build();
        UserResource loggedInUserResource = newUserResource().withId(loggedInUser.getId()).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();

        when(userRepositoryMock.findOne(loggedInUserResource.getId())).thenReturn(loggedInUser);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(123L, 456L)).thenReturn(partnerOrganisation);
        when(financeCheckWorkflowHandlerMock.approveFinanceCheckFigures(partnerOrganisation, loggedInUser)).thenReturn(false);

        setLoggedInUser(loggedInUserResource);

        ServiceResult<Void> result = service.approve(123L, 456L);
        assertTrue(result.getFailure().is(FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW));
    }


    @Override
    protected FinanceCheckServiceImpl supplyServiceUnderTest() {
        return new FinanceCheckServiceImpl();
    }
}
