package org.innovateuk.ifs.application.forms.sections.yourfunding.validator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        YourFundingForm form =  new YourFundingForm();
        form.setRequestingFunding(true);
        form.setGrantClaimPercentage(0);

        form.setOtherFunding(true);
        OtherFundingRowForm emptyRow = new OtherFundingRowForm(new OtherFunding(null, null, "Valid", "01-2019", new BigDecimal(123)));
        OtherFundingRowForm existingRow = new OtherFundingRowForm(new OtherFunding(20L, null, null, "InvalidPattern", new BigDecimal("012345678901234567890")));

        form.setOtherFundingRows(asMap(
                unsavedId, emptyRow,
                "20", existingRow
        ));

        form.setTermsAgreed(false);

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

        assertTrue(bindingResult.hasFieldErrors("termsAgreed"));




    }
}
