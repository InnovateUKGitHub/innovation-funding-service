package org.innovateuk.ifs.application.forms.sections.yourfunding.validator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YourFundingFormValidatorTest extends BaseServiceUnitTest<YourFundingFormValidator> {

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Override
    protected YourFundingFormValidator supplyServiceUnderTest() {
        return new YourFundingFormValidator();
    }

    @Test
    public void validate() {
        String unsavedId = generateUnsavedRowId();
        YourFundingPercentageForm form =  new YourFundingPercentageForm();
        form.setRequestingFunding(true);
        form.setGrantClaimPercentage(BigDecimal.valueOf(0));

        form.setOtherFunding(true);
        OtherFundingRowForm emptyRow = new OtherFundingRowForm(new OtherFunding(null, null, "Valid", "01-2019", new BigDecimal(123), 1L));
        OtherFundingRowForm existingRow = new OtherFundingRowForm(new OtherFunding(20L, null, null, "InvalidPattern", new BigDecimal("012345678901234567890"), 1L));

        form.setOtherFundingRows(asMap(
                unsavedId, emptyRow,
                "20", existingRow
        ));

        BindingResult bindingResult = new DataBinder(form).getBindingResult();
        UserResource user = newUserResource().build();
        long applicationId = 2L;

        service.validate(form, bindingResult, user, applicationId);

        assertFalse(bindingResult.hasFieldErrors("requestingFunding"));
        assertTrue(bindingResult.hasFieldErrors("grantClaimPercentage"));

        assertFalse(bindingResult.hasFieldErrors("otherFunding"));
        assertFalse(bindingResult.hasFieldErrors(String.format("otherFundingRows[%s].source", unsavedId)));
        assertFalse(bindingResult.hasFieldErrors(String.format("otherFundingRows[%s].date", unsavedId)));
        assertFalse(bindingResult.hasFieldErrors(String.format("otherFundingRows[%s].fundingAmount", unsavedId)));
        assertTrue(bindingResult.hasFieldErrors("otherFundingRows[20].source"));
        assertTrue(bindingResult.hasFieldErrors("otherFundingRows[20].date"));
        assertTrue(bindingResult.hasFieldErrors("otherFundingRows[20].fundingAmount"));
    }

    @Test
    public void validateYourFundingAmountForm() {
        YourFundingAmountForm form =  new YourFundingAmountForm();
        form.setAmount(new BigDecimal("100"));

        form.setOtherFunding(false);

        BindingResult bindingResult = new DataBinder(form).getBindingResult();
        UserResource user = newUserResource().build();
        long applicationId = 2L;
        OrganisationResource organisation = OrganisationResourceBuilder.newOrganisationResource().build();
        ApplicationFinanceResource baseFinanceResource = mock(ApplicationFinanceResource.class);
        when(organisationRestService.getByUserAndApplicationId(user.getId(), applicationId)).thenReturn(restSuccess(organisation));
        when(applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId())).thenReturn(restSuccess(baseFinanceResource));

        when(baseFinanceResource.getTotal()).thenReturn(new BigDecimal("99.9"));
        service.validate(form, bindingResult, user, applicationId);
        assertFalse(bindingResult.hasErrors());

        when(baseFinanceResource.getTotal()).thenReturn(new BigDecimal("50"));
        service.validate(form, bindingResult, user, applicationId);
        assertTrue(bindingResult.hasErrors());
        assertTrue(bindingResult.hasFieldErrors("amount"));
    }
}