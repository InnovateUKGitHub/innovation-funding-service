package org.innovateuk.ifs.registration.validator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.form.OrganisationSelectionForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class OrganisationSelectionFormValidator implements Validator {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @Override
    public boolean supports(Class<?> clazz) {
        return OrganisationSelectionForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Optional<Long> competitionId = registrationCookieService.getCompetitionIdCookieValue(request);
    if (competitionId.isPresent()) {
            OrganisationSelectionForm form = (OrganisationSelectionForm) target;
            CompetitionResource competition = competitionService.getById(competitionId.get());
            OrganisationResource organisation = organisationService.getOrganisationById(form.getSelectedOrganisationId());
            if (!competition.getLeadApplicantTypes().contains(organisation.getOrganisationType())) {
                errors.rejectValue("selectedOrganisationId", "validation.standard.organisation.lead.invalid");
            }
        }
    }
}
