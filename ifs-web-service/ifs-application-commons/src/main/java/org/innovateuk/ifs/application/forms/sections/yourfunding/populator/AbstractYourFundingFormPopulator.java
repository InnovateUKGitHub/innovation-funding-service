package org.innovateuk.ifs.application.forms.sections.yourfunding.populator;

import org.innovateuk.ifs.application.forms.sections.yourfunding.form.*;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.BaseOtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.category.PreviousFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;

import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

public abstract class AbstractYourFundingFormPopulator {

    protected AbstractYourFundingForm populateForm(BaseFinanceResource finance, CompetitionResource competitionResource) {

        AbstractYourFundingForm form = getForm(finance);

        populateOtherFunding(form, finance, competitionResource);
        return form;
    }

    private void populateOtherFunding(AbstractYourFundingForm form, BaseFinanceResource finance, CompetitionResource competitionResource) {
        BaseOtherFundingCostCategory otherFundingCategory;
        Map<String, BaseOtherFundingRowForm> rows;
        if (competitionResource.isKtp()) {
            otherFundingCategory = (PreviousFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.PREVIOUS_FUNDING);
            rows = otherFundingCategory.getCosts().stream().map(cost -> {
                PreviousFunding previousFunding = (PreviousFunding) cost;
                return new PreviousFundingRowForm(previousFunding);
            }).collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            rows.put(generateUnsavedRowId(), new PreviousFundingRowForm());
        } else {
            otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
            rows = otherFundingCategory.getCosts().stream().map(cost -> {
                OtherFunding otherFunding = (OtherFunding) cost;
                return new OtherFundingRowForm(otherFunding);
            }).collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            rows.put(generateUnsavedRowId(), new OtherFundingRowForm());
        }

        Boolean otherFundingSet = isOtherFundingSet(otherFundingCategory);

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

    private Boolean isOtherFundingSet(BaseOtherFundingCostCategory otherFundingCategory) {
        if (otherFundingCategory.getOtherFunding() == null ||
                isNullOrEmpty(otherFundingCategory.getOtherFunding().getOtherPublicFunding())) {
            return null;
        } else {
            return otherFundingCategory.otherFundingSet();
        }
    }
}
