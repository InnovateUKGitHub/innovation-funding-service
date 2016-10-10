package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.builder.CostGroupResourceBuilder;
import com.worth.ifs.project.builder.CostResourceBuilder;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.finance.domain.CostCategory;
import com.worth.ifs.project.finance.domain.FinanceCheck;
import com.worth.ifs.project.finance.repository.FinanceCheckRepository;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.project.builder.CostBuilder.newCost;
import static com.worth.ifs.project.builder.CostCategoryBuilder.newCostCategory;
import static com.worth.ifs.project.builder.CostGroupBuilder.newCostGroup;
import static com.worth.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static com.worth.ifs.project.builder.CostResourceBuilder.newCostResource;
import static com.worth.ifs.project.builder.FinanceCheckBuilder.newFinanceCheck;
import static com.worth.ifs.project.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class FinanceCheckServiceImplTest extends BaseServiceUnitTest<FinanceCheckServiceImpl> {

    @Mock
    private FinanceCheckRepository financeCheckRepositoryMock;

    @Test
    public void testGetByProjectAndOrganisationNotFound() {
        // Set up
        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectOrganisationCompositeId compositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(null);
        // Method under test
        ServiceResult<FinanceCheckResource> result = service.getByProjectAndOrganisation(compositeId);
        // Assertions
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(FinanceCheck.class, compositeId)));
    }

    @Test
    public void testGetByProjectAndOrganisation() {
        // Set up
        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectOrganisationCompositeId compositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        FinanceCheck financeCheck = newFinanceCheck().
                withProject(newProject().with(id(projectId)).build()).
                withOrganisation(newOrganisation().with(id(organisationId)).build()).
                withCostGroup(newCostGroup().
                        withCosts(newCost().
                                withValue("1", "2").
                                withCostCategory(
                                        newCostCategory().
                                                withName("cat 1", "cat 2").
                                                buildArray(2, CostCategory.class)).
                                build(2)).
                        build()).
                build();
        // Method under test
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(financeCheck);
        ServiceResult<FinanceCheckResource> result = service.getByProjectAndOrganisation(compositeId);
        // Assertions - basically testing the deserialisation into resource objects
        assertTrue(result.isSuccess());
        assertEquals(financeCheck.getId(), result.getSuccessObject().getId());
        assertEquals(financeCheck.getOrganisation().getId(), result.getSuccessObject().getOrganisation());
        assertEquals(financeCheck.getProject().getId(), result.getSuccessObject().getProject());
        assertEquals(financeCheck.getCostGroup().getId(), result.getSuccessObject().getCostGroup().getId());
        assertEquals(financeCheck.getCostGroup().getCosts().size(), result.getSuccessObject().getCostGroup().getCosts().size());
        assertEquals(financeCheck.getCostGroup().getCosts().get(0).getCostCategory().getId(), result.getSuccessObject().getCostGroup().getCosts().get(0).getCostCategory().getId());
    }

    @Test
    public void testSaveFinanceCheck(){
        Long projectId = 1L;
        Long organisationId = 2L;

        User loggedInUser = newUser().build();
        UserResource loggedInUserResource = newUserResource().withId(loggedInUser.getId()).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().withProject(projectId).withOrganisation(organisationId).build();
        FinanceCheck financeCheck = newFinanceCheck().withOrganisation(newOrganisation().withId(organisationId).build()).withProject(newProject().withId(projectId).build()).build();
        when(userRepositoryMock.findOne(loggedInUserResource.getId())).thenReturn(loggedInUser);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisation);
        when(financeCheckWorkflowHandlerMock.financeCheckFiguresEdited(partnerOrganisation, loggedInUser)).thenReturn(true);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(financeCheck);
        setLoggedInUser(loggedInUserResource);

        ServiceResult result = service.save(financeCheckResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveFinanceCheckValidationFail(){
        Long projectId = 1L;
        Long organisationId = 2L;

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().
                withProject(projectId).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(newCostResource().
                                withValue(new BigDecimal("1.01"), null, new BigDecimal("-1"), new BigDecimal("1.00")).build(4)).
                        build()).
                build();

        ServiceResult result = service.validate(financeCheckResource);

        assertTrue(result.isFailure());
        assertEquals(3, result.getErrors().size());
        assertEquals(3, result.getErrors().size());
        assertTrue(result.getErrors().contains(new Error(FINANCE_CHECKS_COST_LESS_THAN_ZERO, HttpStatus.BAD_REQUEST)));
        assertTrue(result.getErrors().contains(new Error(FINANCE_CHECKS_CONTAINS_FRACTIONS_IN_COST,  HttpStatus.BAD_REQUEST)));
        assertTrue(result.getErrors().contains(new Error(FINANCE_CHECKS_COST_NULL, HttpStatus.BAD_REQUEST)));
    }

    @Test
    public void testSaveFinanceCheckValidationPass(){
        Long projectId = 1L;
        Long organisationId = 2L;

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().
                withProject(projectId).
                withOrganisation(organisationId).
                withCostGroup(newCostGroupResource().
                        withCosts(newCostResource().
                                withValue(new BigDecimal("1.00"), BigDecimal.ZERO, BigDecimal.ONE).build(3)).
                        build()).
                build();

        ServiceResult result = service.validate(financeCheckResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveFinanceCheckWhenWorkflowStepFails(){
        Long projectId = 1L;
        Long organisationId = 2L;

        User loggedInUser = newUser().build();
        UserResource loggedInUserResource = newUserResource().withId(loggedInUser.getId()).build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().build();
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().withProject(projectId).withOrganisation(organisationId).build();
        FinanceCheck financeCheck = newFinanceCheck().withOrganisation(newOrganisation().withId(organisationId).build()).withProject(newProject().withId(projectId).build()).build();
        when(userRepositoryMock.findOne(loggedInUserResource.getId())).thenReturn(loggedInUser);
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(partnerOrganisation);
        when(financeCheckWorkflowHandlerMock.financeCheckFiguresEdited(partnerOrganisation, loggedInUser)).thenReturn(false);
        when(financeCheckRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(financeCheck);
        setLoggedInUser(loggedInUserResource);

        ServiceResult<Void> result = service.save(financeCheckResource);

        assertTrue(result.getFailure().is(FINANCE_CHECKS_CANNOT_PROGRESS_WORKFLOW));
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



    @Test
    public void testGetFinanceCheckSummary(){

    }

    @Override
    protected FinanceCheckServiceImpl supplyServiceUnderTest() {
        return new FinanceCheckServiceImpl();
    }
}
