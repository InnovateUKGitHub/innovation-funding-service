package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class for populating the base functions, used by questions and sections
 */
public abstract class BaseModelPopulator {

    @Autowired
    private OrganisationService organisationService;

    protected ApplicationForm initializeApplicationForm(ApplicationForm form) {
        if(null == form){
            form = new ApplicationForm();
        }

        return form;
    }

    protected void addApplicationFormDetailInputs(ApplicationResource application, Form form) {
        Map<String, String> formInputs = form.getFormInput();
        formInputs.put("application_details-title", application.getName());
        formInputs.put("application_details-duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() == null){
            formInputs.put("application_details-startdate_day", "");
            formInputs.put("application_details-startdate_month", "");
            formInputs.put("application_details-startdate_year", "");
        }else{
            formInputs.put("application_details-startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            formInputs.put("application_details-startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            formInputs.put("application_details-startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
        form.setFormInput(formInputs);
    }

    protected Boolean isApplicationInViewMode(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        if(!application.isOpen() || !userOrganisation.isPresent()){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    protected Optional<OrganisationResource> getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {
        return userApplicationRoles.stream()
                .filter(uar -> uar.getUser().equals(userId))
                .map(uar -> organisationService.getOrganisationById(uar.getOrganisationId()))
                .findFirst();
    }

    protected Long getUserOrganisationId(Optional<OrganisationResource> userOrganisation) {
        if(userOrganisation.isPresent()) {
            return userOrganisation.get().getId();
        }

        return null;
    }



}
