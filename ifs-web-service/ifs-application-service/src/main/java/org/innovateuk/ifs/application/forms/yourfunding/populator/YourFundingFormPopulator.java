package org.innovateuk.ifs.application.forms.yourfunding.populator;

import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

@Component
public class YourFundingFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ApplicationService applicationService;

    public void populateForm(YourFundingForm form, long applicationId, UserResource user, Optional<Long> organisationId) {

        OrganisationResource organisation = organisationId.map(organisationRestService::getOrganisationById).map(RestResult::getSuccess)
                .orElseGet(() -> organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess());
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
        ApplicationResource application = applicationService.getById(applicationId);
        QuestionResource otherFundingQuestion = questionRestService.getQuestionByCompetitionIdAndFormInputType(application.getCompetition(), FormInputType.OTHER_FUNDING).getSuccess();

        Optional<Integer> claimPercentage = ofNullable(finance.getGrantClaim()).map(GrantClaim::getGrantClaimPercentage);

        Boolean requestingFunding = isRequestingFunding(claimPercentage);
        Integer fundingLevel = Boolean.TRUE.equals(requestingFunding) ? claimPercentage.get() : null;

        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        Boolean otherFundingSet = isOtherFundingSet(otherFundingCategory);

        Map<String, OtherFundingRowForm> rows = otherFundingCategory.getCosts().stream().map(cost -> {
            OtherFunding otherFunding = (OtherFunding) cost;
            return new OtherFundingRowForm(otherFunding);
        }).collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
        rows.put(generateUnsavedRowId(), new OtherFundingRowForm());

        form.setRequestingFunding(requestingFunding);
        form.setGrantClaimPercentage(fundingLevel);
        form.setOtherFunding(otherFundingSet);
        form.setOtherFundingRows(rows);
        form.setOtherFundingQuestionId(otherFundingQuestion.getId());
    }

    private Boolean isOtherFundingSet(OtherFundingCostCategory otherFundingCategory) {
        if (otherFundingCategory.getOtherFunding() == null ||
                isNullOrEmpty(otherFundingCategory.getOtherFunding().getOtherPublicFunding())) {
            return null;
        } else {
            return otherFundingCategory.otherFundingSet();
        }
    }

    private Boolean isRequestingFunding(Optional<Integer> claimPercentage) {
        if (!claimPercentage.isPresent()) {
            return null;
        } else {
            return !claimPercentage.get().equals(0);
        }
    }
}
