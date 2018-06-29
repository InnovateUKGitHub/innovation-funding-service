package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.validator.ApplicationMarkAsCompleteValidator;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.AcademicJesValidator;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
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
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private FinanceRowCostsService financeRowCostsService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ApplicationValidationUtil applicationValidationUtil;

    @Autowired
    private AcademicJesValidator academicJesValidator;

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
            results.add(applicationValidationUtil.addValidation(application, new ApplicationMarkAsCompleteValidator()));
        }

        return results;
    }

    @Override
    public BindingResult validateFormInputResponse(Application application, Long formInputId, Long markedAsCompleteById) {
        FormInputResponse response = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId);
        BindingResult result = applicationValidationUtil.validateResponse(response, false);

        validateFileUploads(application, formInputId).forEach(objectError -> result.addError(objectError));

        return result;
    }


    @Override
    public List<ValidationMessages> validateCostItem(Long applicationId, Question question, Long markedAsCompleteById) {
        return getProcessRole(markedAsCompleteById).andOnSuccess(role ->
                financeService.financeDetails(applicationId, role.getOrganisationId()).andOnSuccess(financeDetails ->
                        financeRowCostsService.getCostItems(financeDetails.getId(), question.getId()).andOnSuccessReturn(costItems ->
                                applicationValidationUtil.validateCostItem(costItems, question)
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

    private List<ObjectError> validateFileUploads(Application application, Long formInputId) {
        List<ObjectError> errors = new ArrayList<>();
        FormInput formInput = formInputRepository.findById(formInputId).get();

        if(FormInputType.FINANCE_UPLOAD.equals(formInput.getType()) && isResearchUser()) {
            errors.addAll(applicationValidationUtil.addValidation(application, academicJesValidator).getAllErrors());
        }

        return errors;
    }

    private boolean isResearchUser() {
        Optional<User> userResult = getCurrentlyLoggedInUser().getOptionalSuccessObject();
        if(userResult.isPresent()) {
            Optional<OrganisationResource> organisationResult = organisationService.getPrimaryForUser(userResult.get().getId()).getOptionalSuccessObject();
            if(organisationResult.isPresent()) {
                return OrganisationTypeEnum.RESEARCH.getId().equals(organisationResult.get().getOrganisationType());
            }
        }

        return false;
    }
}
