package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.ValidationMessages.reject;


/**
 * This class validates the Application finances, it checks if there is a value present.
 */
@Component
public class AcademicJesValidator implements Validator {

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private OrganisationService organisationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Application.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final Application application = (Application) target;

        if (financeFileIsEmpty(application)) {
            reject(errors, "validation.application.jes.upload.required");
        }
    }

    private boolean financeFileIsEmpty(Application application) {
        List<ApplicationFinance> applicationFinances = application.getApplicationFinances();
        Optional<OrganisationResource> organisationOpt = organisationService.getPrimaryForUser(loggedInUserSupplier.get().getId()).getOptionalSuccessObject();

        if (applicationFinances == null || !organisationOpt.isPresent()) {
            return true;
        }

        Optional<ApplicationFinance> applicationFinanceOpt = applicationFinances
                .stream()
                .filter(applicationFinance -> applicationFinance.getOrganisation().getId().equals(organisationOpt.get().getId()))
                .findAny();

        return !applicationFinanceOpt.isPresent() || applicationFinanceOpt.get().getFinanceFileEntry() == null;
    }
}
