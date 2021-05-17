package org.innovateuk.ifs.application.forms.sections.yourfunding.saver;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.AcademicAndSecretarialSupportBuilder.newAcademicAndSecretarialSupport;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.AssociateSalaryCostBuilder.newAssociateSalaryCost;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.IndirectCostBuilder.newIndirectCost;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class YourFundingSaverTest extends BaseServiceUnitTest<YourFundingSaver> {
    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ApplicationFinanceRowRestService financeRowRestService;

    @Captor
    private ArgumentCaptor<FinanceRowItem> financeRowItemArgumentCaptor;

    @Override
    protected YourFundingSaver supplyServiceUnderTest() {
        return new YourFundingSaver();
    }

    @Test
    public void save() {
        OtherFunding otherFunding = newOtherFunding()
                .withFundingSource(OtherFundingCostCategory.OTHER_FUNDING)
                .withOtherPublicFunding("No")
                .build();
        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withFinanceOrganisationDetails(asMap(
                FinanceRowType.FINANCE,  newExcludedCostCategory()
                    .withCosts(newGrantClaimPercentage().build(1))
                    .build(),
                FinanceRowType.OTHER_FUNDING, newOtherFundingCostCategory()
                    .withCosts(singletonList(otherFunding))
                    .build()
        )).withFecEnabled(null).build();

        when(financeRowRestService.update(any())).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.create(any())).thenReturn(restSuccess(mock(FinanceRowItem.class)));
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setRequestingFunding(true);
        form.setGrantClaimPercentage(BigDecimal.valueOf(100));

        form.setOtherFunding(true);

        OtherFundingRowForm emptyRow = new OtherFundingRowForm(new OtherFunding(null, null, "emptySource", "emptyDate", new BigDecimal(123), finance.getId()));

        OtherFundingRowForm existingRow = new OtherFundingRowForm(new OtherFunding(20L, null, "existingSource", "existingDate", new BigDecimal(321), finance.getId()));

        form.setOtherFundingRows(asMap(
                generateUnsavedRowId(), emptyRow,
                "20", existingRow
                ));

        service.save(APPLICATION_ID, ORGANISATION_ID, form);

        verify(financeRowRestService).update(finance.getGrantClaim());

        OtherFunding expectedOtherFundingSet = new OtherFunding(finance.getId());
        expectedOtherFundingSet.setId(otherFunding.getId());
        expectedOtherFundingSet.setOtherPublicFunding("Yes");
        expectedOtherFundingSet.setFundingSource(OtherFundingCostCategory.OTHER_FUNDING);
        verify(financeRowRestService).update(expectedOtherFundingSet);

        OtherFunding expectedEmptyRow = new OtherFunding(null, null, "emptySource", "emptyDate", new BigDecimal(123), finance.getId());
        verify(financeRowRestService).create(expectedEmptyRow);

        OtherFunding updatedEmptyRow = new OtherFunding(20L, null, "existingSource", "existingDate", new BigDecimal(321), finance.getId());
        verify(financeRowRestService).update(updatedEmptyRow);
    }

    @Test
    public void saveKtpNonFec() {
        Long financeId = 1L;
        Long indirectCostId = 2L;

        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withId(financeId)
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory()
                                .withCosts(newAssociateSalaryCost().withCost(BigInteger.ONE).build(1))
                                .build(),
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, newDefaultCostCategory()
                                .withCosts(newAcademicAndSecretarialSupport().withCost(BigInteger.TEN).build(1))
                                .build(),
                        FinanceRowType.INDIRECT_COSTS, newDefaultCostCategory()
                                .withCosts(newIndirectCost()
                                        .withTargetId(financeId)
                                        .withId(indirectCostId)
                                        .withCost(BigInteger.valueOf(2)).build(1))
                                .build(),
                        FinanceRowType.FINANCE,  newExcludedCostCategory()
                                .withCosts(newGrantClaimPercentage().build(1))
                                .build()
                )).withFecEnabled(false).build();

        when(financeRowRestService.update(any())).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.create(any())).thenReturn(restSuccess(mock(FinanceRowItem.class)));
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setRequestingFunding(true);
        form.setGrantClaimPercentage(BigDecimal.valueOf(100));

        service.save(APPLICATION_ID, ORGANISATION_ID, form);

        verify(financeRowRestService, times(2)).update(financeRowItemArgumentCaptor.capture());

        List<FinanceRowItem> financeRowItems = financeRowItemArgumentCaptor.getAllValues();
        assertEquals(2, financeRowItems.size());

        GrantClaim grantClaim = (GrantClaim) financeRowItems.get(0);
        assertNotNull(grantClaim);
        assertTrue(BigDecimal.valueOf(100).compareTo(grantClaim.getTotal()) == 0);

        IndirectCost indirectCost = (IndirectCost) financeRowItems.get(1);
        assertNotNull(indirectCost);

        assertEquals(FinanceRowType.INDIRECT_COSTS, indirectCost.getCostType());
        assertEquals(financeId, indirectCost.getTargetId());
        assertEquals(indirectCostId, indirectCost.getId());
        assertTrue(BigDecimal.valueOf(5).compareTo(indirectCost.getTotal()) == 0);
    }

    @Test
    public void removeOtherFundingRowForm() {
        String rowId = "12";
        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setOtherFundingRows(asMap(rowId, new OtherFundingRowForm()));

        service.removeOtherFundingRowForm(form, rowId);

        assertTrue(form.getOtherFundingRows().isEmpty());
        verify(financeRowRestService).delete(Long.valueOf(rowId));
    }

    @Test
    public void addOtherFundingRow() {
        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setOtherFundingRows(new LinkedHashMap<>());

        service.addOtherFundingRow(form);

        assertTrue(form.getOtherFundingRows().keySet().stream().anyMatch(key -> key.startsWith(UNSAVED_ROW_PREFIX)));
    }
}
