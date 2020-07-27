package org.innovateuk.ifs.application.forms.sections.yourfunding.validator;

import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.AbstractYourFundingForm;
import org.innovateuk.ifs.application.forms.sections.yourfunding.form.YourFundingAmountForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Component
public class YourFundingFormValidator extends AbstractYourFundingFormValidator {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    public void validate(AbstractYourFundingForm form, Errors errors, UserResource user, long applicationId, BigDecimal maximumFundingSought) {

        ApplicationResource applicationResource = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionApplicationConfigResource competitionApplicationConfigResource
                = competitionApplicationConfigRestService.findOneByCompetitionId(applicationResource.getCompetition()).getSuccess();

        Supplier<BaseFinanceResource> financeSupplier = () -> {
            OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
            return applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
        };
        validate(form, errors, financeSupplier, competitionApplicationConfigResource.getMaximumFundingSought());

        if (form instanceof YourFundingAmountForm ) {
            validateLessThanCosts((YourFundingAmountForm) form, errors, financeSupplier);
        }
    }

    private void validateLessThanCosts(YourFundingAmountForm form, Errors errors, Supplier<BaseFinanceResource> financeSupplier) {
        if (form.getAmount() != null ) {
            BaseFinanceResource finance = financeSupplier.get();
            BigDecimal roundedCosts = finance.getTotal().setScale(0, BigDecimal.ROUND_HALF_EVEN); //Same as thymeleaf
            if (form.getAmount().compareTo(roundedCosts) > 0) {
                errors.rejectValue("amount", "validation.finance.funding.sought.more.than.costs");
            }
        }
    }
}
