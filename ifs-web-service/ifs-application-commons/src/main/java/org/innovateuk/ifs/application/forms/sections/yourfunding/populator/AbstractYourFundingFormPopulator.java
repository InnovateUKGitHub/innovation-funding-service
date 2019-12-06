package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingPercentageForm;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;

import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

public abstract class AbstractYourFundingFormPopulator {

    protected AbstractYourFundingForm populateForm(BaseFinanceResource finance) {

        AbstractYourFundingForm form = getForm(finance);

        populateOtherFunding(form, finance);
        return form;
    }

    private void populateOtherFunding(AbstractYourFundingForm form, BaseFinanceResource finance ) {
        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        Boolean otherFundingSet = isOtherFundingSet(otherFundingCategory);

        Map<String, OtherFundingRowForm> rows = otherFundingCategory.getCosts().stream().map(cost -> {
            OtherFunding otherFunding = (OtherFunding) cost;
            return new OtherFundingRowForm(otherFunding);
        }).collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(generateUnsavedRowId(), new OtherFundingRowForm());

        form.setOtherFunding(otherFundingSet);
        form.setOtherFundingRows(rows);
    }

    private AbstractYourFundingForm getForm(BaseFinanceResource finance) {
        GrantClaim grantClaim = finance.getGrantClaim();

        if (grantClaim instanceof GrantClaimPercentage) {
            GrantClaimPercentage grantClaimPercentage = (GrantClaimPercentage) grantClaim;
            YourFundingPercentageForm form = new YourFundingPercentageForm();
            form.setGrantClaimPercentage(grantClaimPercentage.getPercentage());
            form.setRequestingFunding(grantClaimPercentage.getPercentage() == null ? null : finance.isRequestingFunding());
            return form;
        } else if (grantClaim instanceof GrantClaimAmount) {
            GrantClaimAmount grantClaimAmount = (GrantClaimAmount) grantClaim;
            YourFundingAmountForm form = new YourFundingAmountForm();
            form.setAmount(grantClaimAmount.getAmount());
            return form;
        }
        throw new ObjectNotFoundException();

    }

    private Boolean isOtherFundingSet(OtherFundingCostCategory otherFundingCategory) {
        if (otherFundingCategory.getOtherFunding() == null ||
                isNullOrEmpty(otherFundingCategory.getOtherFunding().getOtherPublicFunding())) {
            return null;
        } else {
            return otherFundingCategory.otherFundingSet();
        }
    }
}
