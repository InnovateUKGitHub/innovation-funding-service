package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.BaseOtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.ExcludedCostCategory;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.ListUtils.union;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class YourFundingFormPopulatorTest extends BaseServiceUnitTest<YourFundingFormPopulator> {
    private static final long APPLICATION_ID = 1L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    private OrganisationResource organisation =  newOrganisationResource().build();

    private GrantClaimPercentage grantClaim;
    private OtherFunding otherFunding;
    private List<OtherFunding> otherFundingRows;
    private ExcludedCostCategory grantClaimCategory;
    private OtherFundingCostCategory otherFundingCategory;
    private ApplicationFinanceResource finance;
    private CompetitionResource competition;

    @Override
    protected YourFundingFormPopulator supplyServiceUnderTest() {
        return new YourFundingFormPopulator();
    }

    @Before
    public void setup() {

        grantClaim = newGrantClaimPercentage()
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .build();

        grantClaimCategory = newExcludedCostCategory()
                .withCosts(singletonList(grantClaim))
                .build();

        otherFunding = new OtherFunding(1L);
        otherFunding.setFundingSource(OtherFundingCostCategory.OTHER_FUNDING);
        otherFunding.setOtherPublicFunding("Yes");

        otherFundingRows = newOtherFunding()
                .withFundingAmount(new BigDecimal(123))
                .withSecuredDate("12-MMM")
                .withFundingSource("someSource")
                .build(1);

        otherFundingCategory = newOtherFundingCostCategory()
                .withCosts(union(singletonList(otherFunding), otherFundingRows))
                .build();

        finance = newApplicationFinanceResource()
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.FINANCE, grantClaimCategory,
                        FinanceRowType.OTHER_FUNDING, otherFundingCategory
                ))
                .build();
        competition = newCompetitionResource().withFinanceRowTypes(emptyList()).build();

        when(competitionRestService.getCompetitionForApplication(APPLICATION_ID)).thenReturn(restSuccess(competition));
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, organisation.getId())).thenReturn(restSuccess(finance));
    }

    @Test
    public void populate() {
        YourFundingPercentageForm form = (YourFundingPercentageForm) service.populateForm(APPLICATION_ID, organisation.getId());

        assertEquals(form.getRequestingFunding(), true);
        assertEquals(form.getGrantClaimPercentage(), BigDecimal.valueOf(100));

        assertEquals(form.getOtherFunding(), true);
        assertEquals(form.getOtherFundingRows().size(), 2);

        long costId = otherFundingRows.get(0).getId();
        BaseOtherFundingRowForm row =  form.getOtherFundingRows().get(String.valueOf(costId));
        assertEquals(row.getFundingAmount(), new BigDecimal(123));
        assertEquals(row.getCostId(), (Long) costId);
        assertEquals(row.getDate(), "12-MMM");
        assertEquals(row.getSource(), "someSource");

        String unsavedRowId = form.getOtherFundingRows().keySet().stream().filter(id -> id.startsWith(AbstractCostRowForm.UNSAVED_ROW_PREFIX)).findFirst().get();
        BaseOtherFundingRowForm emptyRow = form.getOtherFundingRows().get(unsavedRowId);
        assertNull(emptyRow.getFundingAmount());
        assertNull(emptyRow.getCostId());
        assertNull(emptyRow.getDate());
        assertNull(emptyRow.getSource());
    }

    @Test
    public void populate_defaultValues() {
        grantClaim.setPercentage(null);
        otherFunding.setOtherPublicFunding(null);

        YourFundingPercentageForm form = (YourFundingPercentageForm) service.populateForm(APPLICATION_ID, organisation.getId());

        assertNull(form.getRequestingFunding());
        assertNull(form.getOtherFunding());
    }

    @Test
    public void populate_withOrgId() {
        grantClaim.setPercentage(null);
        otherFunding.setOtherPublicFunding(null);

        YourFundingPercentageForm form = (YourFundingPercentageForm) service.populateForm(APPLICATION_ID, organisation.getId());

        assertNull(form.getRequestingFunding());
        assertNull(form.getOtherFunding());
    }
}
