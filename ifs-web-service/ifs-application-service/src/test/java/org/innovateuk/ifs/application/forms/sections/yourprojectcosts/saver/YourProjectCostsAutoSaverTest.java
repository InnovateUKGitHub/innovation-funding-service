package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.AcademicAndSecretarialSupportBuilder.newAcademicAndSecretarialSupport;
import static org.innovateuk.ifs.finance.builder.AssociateSalaryCostBuilder.newAssociateSalaryCost;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.IndirectCostBuilder.newIndirectCost;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class YourProjectCostsAutoSaverTest {
    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationFinanceRowRestService financeRowRestService;

    @InjectMocks
    private YourProjectCostsAutosaver target;

    private ArgumentCaptor<AbstractFinanceRowItem> costArgumentCaptor = ArgumentCaptor.forClass(AbstractFinanceRowItem.class);

    @Test
    public void associateSalaryCostRowsChangeTriggerAutoSaveForIndirectCost() {
        String field = "associateSalaryCostRows[0].cost";
        String value = "100";
        long applicationId = 1L;
        long organisationId = 2L;
        BigDecimal grantClaimPercentage = BigDecimal.valueOf(50);
        BigDecimal expectedIndirectCost = BigDecimal.valueOf(23);

        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().withFecEnabled(false).build();
        AssociateSalaryCost associateSalaryCost = newAssociateSalaryCost().withCost(new BigInteger(value)).build();
        DefaultCostCategory associateCostCategory = newDefaultCostCategory()
                .withCosts(Collections.singletonList(associateSalaryCost))
                .build();
        IndirectCost indirectCost = newIndirectCost().withCost(BigInteger.ZERO).build();
        DefaultCostCategory indirectCostCategory = newDefaultCostCategory()
                .withCosts(Collections.singletonList(indirectCost))
                .build();
        ApplicationFinanceResource applicationOrganisationFinance = newApplicationFinanceResource()
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, associateCostCategory,
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.INDIRECT_COSTS, indirectCostCategory
                ))
                .withGrantClaimPercentage(grantClaimPercentage)
                .build();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(applicationFinanceRestService.getApplicationFinance(applicationId, organisationId)).thenReturn(restSuccess(applicationFinance));
        when(financeRowRestService.get(0)).thenReturn(restSuccess(associateSalaryCost));
        when(financeRowRestService.update(any(AssociateSalaryCost.class))).thenReturn(restSuccess(new ValidationMessages()));
        when(applicationFinanceRestService.getFinanceDetails(applicationId, organisationId)).thenReturn(restSuccess(applicationOrganisationFinance));
        when(financeRowRestService.update(any(IndirectCost.class))).thenReturn(restSuccess(new ValidationMessages()));

        target.autoSave(field, value, applicationId, user);

        verify(financeRowRestService, times(2)).update(costArgumentCaptor.capture());

        assertNotNull(costArgumentCaptor);
        assertEquals(2, costArgumentCaptor.getAllValues().size());

        AssociateSalaryCost associateSalaryCostToSave = (AssociateSalaryCost) costArgumentCaptor.getAllValues().get(0);
        assertNotNull(associateSalaryCostToSave);
        assertEquals(FinanceRowType.ASSOCIATE_SALARY_COSTS, associateSalaryCostToSave.getCostType());
        assertEquals(new BigInteger(value), associateSalaryCostToSave.getCost());
        assertEquals(new BigInteger(value), associateSalaryCostToSave.getTotal().toBigInteger());

        IndirectCost indirectCostToSave = (IndirectCost) costArgumentCaptor.getAllValues().get(1);
        assertNotNull(indirectCostToSave);
        assertEquals(FinanceRowType.INDIRECT_COSTS, indirectCostToSave.getCostType());
        assertEquals(expectedIndirectCost.toBigIntegerExact(), indirectCostToSave.getCost());
        assertEquals(expectedIndirectCost, indirectCostToSave.getTotal());
    }

    @Test
    public void academicAndSecretarialSupportCostRowsChangeTriggerAutoSaveForIndirectCost() {
        String field = "academicAndSecretarialSupportForm.cost";
        String value = "100";
        long applicationId = 1L;
        long organisationId = 2L;
        BigDecimal grantClaimPercentage = BigDecimal.valueOf(50);
        BigDecimal expectedIndirectCost = BigDecimal.valueOf(23);

        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().withFecEnabled(false).build();
        AcademicAndSecretarialSupport academicAndSecretarialSupport = newAcademicAndSecretarialSupport().withCost(new BigInteger(value)).build();
        DefaultCostCategory academicAndSecretarialSupportCostCategory = newDefaultCostCategory()
                .withCosts(Collections.singletonList(academicAndSecretarialSupport))
                .build();
        IndirectCost indirectCost = newIndirectCost().withCost(BigInteger.ZERO).build();
        ApplicationFinanceResource applicationOrganisationFinance = newApplicationFinanceResource()
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, academicAndSecretarialSupportCostCategory,
                        FinanceRowType.INDIRECT_COSTS, newDefaultCostCategory().build()
                ))
                .withGrantClaimPercentage(grantClaimPercentage)
                .build();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(applicationFinanceRestService.getApplicationFinance(applicationId, organisationId)).thenReturn(restSuccess(applicationFinance));
        when(financeRowRestService.get(0)).thenReturn(restSuccess(academicAndSecretarialSupport));
        when(financeRowRestService.update(any(AssociateSalaryCost.class))).thenReturn(restSuccess(new ValidationMessages()));
        when(applicationFinanceRestService.getFinanceDetails(applicationId, organisationId)).thenReturn(restSuccess(applicationOrganisationFinance));
        when(financeRowRestService.create(any(IndirectCost.class))).thenReturn(restSuccess(indirectCost));
        when(financeRowRestService.update(any(IndirectCost.class))).thenReturn(restSuccess(new ValidationMessages()));

        target.autoSave(field, value, applicationId, user);

        verify(financeRowRestService, times(2)).update(costArgumentCaptor.capture());
        verify(financeRowRestService, times(1)).create(isA(IndirectCost.class));

        assertNotNull(costArgumentCaptor);
        assertEquals(2, costArgumentCaptor.getAllValues().size());

        AcademicAndSecretarialSupport academicAndSecretarialSupportCostToSave = (AcademicAndSecretarialSupport) costArgumentCaptor.getAllValues().get(0);
        assertNotNull(academicAndSecretarialSupportCostToSave);
        assertEquals(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, academicAndSecretarialSupportCostToSave.getCostType());
        assertEquals(new BigInteger(value), academicAndSecretarialSupportCostToSave.getCost());
        assertEquals(new BigInteger(value), academicAndSecretarialSupportCostToSave.getTotal().toBigInteger());

        IndirectCost indirectCostToSave = (IndirectCost) costArgumentCaptor.getAllValues().get(1);
        assertNotNull(indirectCostToSave);
        assertEquals(FinanceRowType.INDIRECT_COSTS, indirectCostToSave.getCostType());
        assertEquals(expectedIndirectCost.toBigIntegerExact(), indirectCostToSave.getCost());
        assertEquals(expectedIndirectCost, indirectCostToSave.getTotal());
    }

    @Test
    public void RowsChangeForFecModelNotTriggerAutoSaveForIndirectCost() {
        String field = "associateSalaryCostRows[0].cost";
        String value = "100";
        long applicationId = 1L;
        long organisationId = 2L;

        UserResource user = newUserResource().build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().withFecEnabled(true).build();
        AssociateSalaryCost associateSalaryCost = newAssociateSalaryCost().withCost(new BigInteger(value)).build();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(applicationFinanceRestService.getApplicationFinance(applicationId, organisationId)).thenReturn(restSuccess(applicationFinance));
        when(financeRowRestService.get(0)).thenReturn(restSuccess(associateSalaryCost));
        when(financeRowRestService.update(any(AssociateSalaryCost.class))).thenReturn(restSuccess(new ValidationMessages()));

        target.autoSave(field, value, applicationId, user);

        verify(financeRowRestService, times(1)).update(costArgumentCaptor.capture());

        assertNotNull(costArgumentCaptor);
        assertEquals(1, costArgumentCaptor.getAllValues().size());

        AssociateSalaryCost associateSalaryCostToSave = (AssociateSalaryCost) costArgumentCaptor.getAllValues().get(0);
        assertNotNull(associateSalaryCostToSave);
        assertEquals(FinanceRowType.ASSOCIATE_SALARY_COSTS, associateSalaryCostToSave.getCostType());
        assertEquals(new BigInteger(value), associateSalaryCostToSave.getCost());
        assertEquals(new BigInteger(value), associateSalaryCostToSave.getTotal().toBigInteger());
    }
}
