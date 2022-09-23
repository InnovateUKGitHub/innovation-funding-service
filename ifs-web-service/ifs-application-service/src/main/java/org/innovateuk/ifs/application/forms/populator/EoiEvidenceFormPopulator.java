package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.form.EoiEvidenceForm;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EoiEvidenceFormPopulator {

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    public EoiEvidenceForm populate(EoiEvidenceForm form, long applicationId) {

        Optional<ApplicationEoiEvidenceResponseResource> eoiEvidence = applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId).getSuccess();
        if (eoiEvidence.isPresent()) {
            form.setEvidenceFileEntryName(form.getEvidenceFileEntryName());
            return form;
        } else {
            return new EoiEvidenceForm();
        }
    }
}
