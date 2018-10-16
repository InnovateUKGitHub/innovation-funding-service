package org.innovateuk.ifs.application.forms.yourfunding.populator;

import org.innovateuk.ifs.application.forms.yourfunding.form.OtherFundingRowForm;
import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
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

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

@Component
public class YourFundingFormPopulator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private DefaultFinanceRowRestService financeRowRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private ApplicationService applicationService;


    public void populateForm(YourFundingForm form, Long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
        ApplicationResource application = applicationService.getById(applicationId);
        QuestionResource grantClaimQuestion = questionRestService.getQuestionByCompetitionIdAndFormInputType(application.getCompetition(), FormInputType.FINANCE).getSuccess();
        QuestionResource otherFundingQuestion = questionRestService.getQuestionByCompetitionIdAndFormInputType(application.getCompetition(), FormInputType.OTHER_FUNDING).getSuccess();

        Optional<Integer> claimPercentage = ofNullable(finance.getGrantClaim()).map(GrantClaim::getGrantClaimPercentage).filter(percentage -> percentage != 0);
        Integer fundingLevel = null;
        if (claimPercentage.isPresent()) {
            fundingLevel = claimPercentage.get();
        }

        OtherFundingCostCategory otherFundingCategory = (OtherFundingCostCategory) finance.getFinanceOrganisationDetails(FinanceRowType.OTHER_FUNDING);
        boolean otherFundingSet = otherFundingCategory.otherFundingSet();

        Map<Long, OtherFundingRowForm> rows = otherFundingCategory.getCosts().stream().map(cost -> {
            OtherFunding otherFunding = (OtherFunding) cost;
            OtherFundingRowForm row = new OtherFundingRowForm();

            row.setCostId(cost.getId());
            row.setDate(otherFunding.getSecuredDate());
            row.setFundingAmount(otherFunding.getFundingAmount());
            row.setSource(otherFunding.getFundingSource());

            return row;
        }).collect(toLinkedMap(OtherFundingRowForm::getCostId, Function.identity()));

        if (!rows.containsKey(null)) {
            rows.put(null, new OtherFundingRowForm());
        }

        form.setRequestingFunding(claimPercentage.isPresent());
        form.setGrantClaimPercentage(fundingLevel);
        form.setOtherFunding(otherFundingSet);
        form.setOtherFundingRows(rows);
        form.setOtherFundingQuestionId(otherFundingQuestion.getId());
        form.setGrantClaimQuestionId(grantClaimQuestion.getId());
    }
}
