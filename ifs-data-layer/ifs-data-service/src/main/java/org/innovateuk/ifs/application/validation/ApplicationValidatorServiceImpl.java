package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.validator.ApplicationDetailsMarkAsCompleteValidator;
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
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
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
        if (formInput.getQuestion().getQuestionSetupType().equals(QuestionSetupType.APPLICATION_DETAILS)) {
            Application application = applicationRepository.findById(applicationId).orElse(null);
            results.add(applicationValidationUtil.addValidation(application, new ApplicationDetailsMarkAsCompleteValidator()));
        }

        return results;
    }

    @Override
    public ValidationMessages validateFormInputResponse(Application application, long formInputId, long markedAsCompleteById) {
        FormInputResponse response = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId);
        BindingResult result = applicationValidationUtil.validateResponse(response, false);

        return fromBindingResult(result);
    }


    @Override
    public List<ValidationMessages> validateCostItem(Long applicationId, FinanceRowType type, Long markedAsCompleteById) {
        return getProcessRole(markedAsCompleteById).andOnSuccess(role ->
                financeService.financeDetails(applicationId, role.getOrganisationId()).andOnSuccessReturn(financeDetails ->
                    financeValidationUtil.validateCostItem(type,
                        financeDetails.getFinanceOrganisationDetails().get(type)
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

    @Override
    public ValidationMessages validateAcademicUpload(Application application, Long markedAsCompleteById) {
        return getProcessRole(markedAsCompleteById).andOnSuccessReturn(role -> {
            OrganisationResource organisation = organisationService.findById(role.getOrganisationId()).getSuccess();
            if (application.getCompetition().applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum())) {
                if (financeFileIsNotPresent(application, organisation)) {
                    return new ValidationMessages(fieldError("jesFileUpload", null, "validation.application.jes.upload.required"));
                }
            }
            return noErrors();
        }).getSuccess();
    }

    private boolean financeFileIsNotPresent(Application application, OrganisationResource organisation) {
        List<ApplicationFinance> applicationFinances = application.getApplicationFinances();

        if (applicationFinances == null) {
            return true;
        }

        Optional<ApplicationFinance> applicationFinance =
                simpleFindFirst(applicationFinances, af -> af.getOrganisation().getId().equals(organisation.getId()));

        return applicationFinance.map(af -> af.getFinanceFileEntry() == null).orElse(true);
    }
}