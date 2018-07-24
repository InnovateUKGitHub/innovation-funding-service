package org.innovateuk.ifs.application;

import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.Optional;

/**
 * Class for populating the base functions, used by questions and sections
 */
public abstract class BaseModelPopulator {

    protected ApplicationForm initializeApplicationForm(ApplicationForm form) {
        if(null == form){
            form = new ApplicationForm();
        }

        return form;
    }



    protected Boolean isApplicationInViewMode(ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        if(!application.isOpen() || !userOrganisation.isPresent()){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
