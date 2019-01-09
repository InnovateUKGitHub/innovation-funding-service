package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.isMarkSectionAsCompleteRequest;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.isMarkSectionAsIncompleteRequest;

/**
 * This Saver will handle save all finance sections that are related to the application.
 */
@Service
public class ApplicationSectionFinanceSaver extends AbstractApplicationSaver {

    public void handleStateAid(Map<String, String[]> params, ApplicationResource application, ApplicationForm form, SectionResource selectedSection) {
        if (isMarkSectionAsCompleteRequest(params)) {
            application.setStateAidAgreed(form.isStateAidAgreed());
        } else if (isMarkSectionAsIncompleteRequest(params) && selectedSection.getType() == SectionType.FINANCE) {
            application.setStateAidAgreed(Boolean.FALSE);
        }
    }
}