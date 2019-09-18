package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.validator.ApplicationDetailsMarkAsCompleteValidator;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.ValidationMessages.fromBindingResult;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Class to validate several objects
 */
@Service
public class ApplicationValidatorServiceImpl extends BaseTransactionalService implements ApplicationValidatorService {

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private ApplicationFinanceRowService financeRowCostsService;

    @Autowired
    private ApplicationFinanceService financeService;

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ApplicationValidationUtil applicationValidationUtil;

    @Autowired
    private FinanceValidationUtil financeValidationUtil;

    @Autowired
    private OrganisationService organisationService;

    @Override
    public List<BindingResult> validateFormInputResponse(Long applicationId, Long formInputId) {
        List<BindingResult> results = new ArrayList<>();
        List<FormInputResponse> responses = formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId);
        if (!responses.isEmpty()) {
            results.addAll(responses.stream().map(formInputResponse -> applicationValidationUtil.validateResponse(formInputResponse, false)).collect(Collectors.toList()));
        } else {
            FormInputResponse emptyResponse = new FormInputResponse();
            emptyResponse.setFormInput(formInputRepository.findById(formInputId).orElse(null));
            results.add(applicationValidationUtil.validateResponse(emptyResponse, false));
        }

        FormInput formInput = formInputRepository.findById(formInputId).get();
        if (formInput.getType().equals(FormInputType.APPLICATION_DETAILS)) {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            results.add(applicationValidationUtil.addValidation(application, new ApplicationDetailsMarkAsCompleteValidator()));
        }

        return results;
    }

    @Override
    public ValidationMessages validateFormInputResponse(Application application, long formInputId, long markedAsCompleteById) {
        FormInputResponse response = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId);
        BindingResult result = applicationValidationUtil.validateResponse(response, false);

        ValidationMessages validationMessages = fromBindingResult(result);
        validationMessages.addAll(validateFileUploads(application, formInputId));

        return validationMessages;
    }


    @Override
    public List<ValidationMessages> validateCostItem(Long applicationId, FinanceRowType type, Long markedAsCompleteById) {
        return getProcessRole(markedAsCompleteById).andOnSuccess(role ->
                financeService.financeDetails(applicationId, role.getOrganisationId()).andOnSuccess(financeDetails ->
                        financeRowCostsService.getCostItems(financeDetails.getId(), type).andOnSuccessReturn(costItems ->
                                financeValidationUtil.validateCostItem(costItems)
                        )
                )
        ).getSuccess();
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowItem costItem) {
        return financeRowCostsService.getCostHandler(costItem.getId());
    }

    @Override
    public FinanceRowHandler getProjectCostHandler(FinanceRowItem costItem) {
        return projectFinanceRowService.getCostHandler(costItem);
    }

    private ValidationMessages validateFileUploads(Application application, Long formInputId) {
        FormInput formInput = formInputRepository.findById(formInputId).get();

        if(FormInputType.FINANCE_UPLOAD.equals(formInput.getType()) && jesFinances(application)) {
            if (financeFileIsNotPresent(application)) {
                Error error = fieldError("jesFileUpload", null, "validation.application.jes.upload.required");
                return new ValidationMessages(error);
            }
        }

        return noErrors(formInputId);
    }

    //This method is duplicating work in FinanceUtil
    private boolean jesFinances(Application application) {
        Optional<User> userResult = getCurrentlyLoggedInUser().getOptionalSuccessObject();
        if(userResult.isPresent()) {
            OrganisationResource organisationResource = organisationService.getByUserAndApplicationId(userResult.get().getId(), application.getId()).getSuccess();
            return application.getCompetition().getIncludeJesForm() && OrganisationTypeEnum.isResearch(organisationResource.getOrganisationType());
        }

        return false;
    }

    private boolean financeFileIsNotPresent(Application application) {
        List<ApplicationFinance> applicationFinances = application.getApplicationFinances();
        Optional<User> user = getCurrentlyLoggedInUser().getOptionalSuccessObject();
        Optional<OrganisationResource> organisation = organisationService.getByUserAndApplicationId(user.get().getId(), application.getId()).getOptionalSuccessObject();

        if (applicationFinances == null || !organisation.isPresent()) {
            return true;
        }

        Optional<ApplicationFinance> applicationFinance =
                simpleFindFirst(applicationFinances, af -> af.getOrganisation().getId().equals(organisation.get().getId()));

        return applicationFinance.map(af -> af.getFinanceFileEntry() == null).orElse(true);
    }
}