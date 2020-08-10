package org.innovateuk.ifs.application.forms.sections.yourfunding.saver;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.BaseOtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class YourFundingSaverTest extends BaseServiceUnitTest<YourFundingSaver> {
    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private ApplicationFinanceRowRestService financeRowRestService;

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
        )).build();

        when(financeRowRestService.update(any())).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.create(any())).thenReturn(restSuccess(mock(FinanceRowItem.class)));
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setRequestingFunding(true);
        form.setGrantClaimPercentage(BigDecimal.valueOf(100));

        form.setOtherFunding(true);

        BaseOtherFundingRowForm<OtherFunding> emptyRow = new BaseOtherFundingRowForm<>(new OtherFunding(null, null, "emptySource", "emptyDate", new BigDecimal(123), finance.getId()));

        BaseOtherFundingRowForm<OtherFunding> existingRow = new BaseOtherFundingRowForm<>(new OtherFunding(20L, null, "existingSource", "existingDate", new BigDecimal(321), finance.getId()));

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
    public void removeOtherFundingRowForm() {
        String rowId = "12";
        YourFundingPercentageForm form = new YourFundingPercentageForm();
        form.setOtherFundingRows(asMap(rowId, new BaseOtherFundingRowForm<OtherFunding>(FinanceRowType.OTHER_FUNDING)));

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

    //TODO Test saving your funding amount.
}
