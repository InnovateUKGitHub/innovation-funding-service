package org.innovateuk.ifs.application.forms.yourfunding.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.GrantClaimCategory;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.ListUtils.union;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaim;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class YourFundingFormPopulatorTest extends BaseServiceUnitTest<YourFundingFormPopulator> {
    private static final long APPLICATION_ID = 1L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private ApplicationService applicationService;

    private UserResource user = newUserResource().build();
    private OrganisationResource organisation =  newOrganisationResource().build();

    private GrantClaim grantClaim;
    private OtherFunding otherFunding;
    private List<OtherFunding> otherFundingRows;
    private GrantClaimCategory grantClaimCategory;
    private OtherFundingCostCategory otherFundingCategory;
    private ApplicationFinanceResource finance;
    private ApplicationResource application;
    private QuestionResource otherFundingQuestion;

    @Override
    protected YourFundingFormPopulator supplyServiceUnderTest() {
        return new YourFundingFormPopulator();
    }

    @Before
    public void setup() {
        super.setup();

        grantClaim = newGrantClaim()
                .withGrantClaimPercentage(100)
                .build();

        grantClaimCategory = newGrantClaimCostCategory()
                .withCosts(asList(grantClaim))
                .build();

        otherFunding = new OtherFunding();
        otherFunding.setFundingSource(OtherFundingCostCategory.OTHER_FUNDING);
        otherFunding.setOtherPublicFunding("Yes");

        otherFundingRows = newOtherFunding()
                .withFundingAmount(new BigDecimal(123))
                .withSecuredDate("12-MMM")
                .withFundingSource("someSource")
                .build(1);


        otherFundingCategory = newOtherFundingCostCategory()
                .withCosts(union(asList(otherFunding), otherFundingRows))
                .build();

        finance = newApplicationFinanceResource()
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.FINANCE, grantClaimCategory,
                        FinanceRowType.OTHER_FUNDING, otherFundingCategory
                ))
                .build();
        application = newApplicationResource()
                .withCompetition(2L)
                .build();
        otherFundingQuestion = newQuestionResource().build();

        when(organisationRestService.getByUserAndApplicationId(user.getId(), APPLICATION_ID)).thenReturn(restSuccess(organisation));
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, organisation.getId())).thenReturn(restSuccess(finance));
        when(applicationService.getById(APPLICATION_ID)).thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndFormInputType(application.getCompetition(), FormInputType.OTHER_FUNDING)).thenReturn(restSuccess(otherFundingQuestion));
    }

    @Test
    public void populate() {
        YourFundingForm form = new YourFundingForm();

        service.populateForm(form, APPLICATION_ID, user);

        assertEquals(form.getRequestingFunding(), true);
        assertEquals(form.getGrantClaimPercentage(), (Integer) 100);

        assertEquals(form.getOtherFunding(), true);
        assertEquals(form.getOtherFundingRows().size(), 2);

        long costId = otherFundingRows.get(0).getId();
        OtherFundingRowForm row = form.getOtherFundingRows().get(String.valueOf(costId));
        assertEquals(row.getFundingAmount(), new BigDecimal(123));
        assertEquals(row.getCostId(), (Long) costId);
        assertEquals(row.getDate(), "12-MMM");
        assertEquals(row.getSource(), "someSource");

        OtherFundingRowForm emptyRow = form.getOtherFundingRows().get(YourFundingForm.EMPTY_ROW_ID);
        assertNull(emptyRow.getFundingAmount());
        assertNull(emptyRow.getCostId());
        assertNull(emptyRow.getDate());
        assertNull(emptyRow.getSource());
    }

    @Test
    public void populate_defaultValues() {
        YourFundingForm form = new YourFundingForm();
        grantClaim.setGrantClaimPercentage(null);
        otherFunding.setOtherPublicFunding(null);

        service.populateForm(form, APPLICATION_ID, user);

        assertNull(form.getRequestingFunding());
        assertNull(form.getOtherFunding());
    }
}
