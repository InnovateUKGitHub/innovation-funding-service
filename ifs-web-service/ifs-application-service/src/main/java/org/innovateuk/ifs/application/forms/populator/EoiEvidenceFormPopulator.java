package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.form.EoiEvidenceForm;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EoiEvidenceFormPopulator {

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    public EoiEvidenceForm populate(long applicationId) {
        EoiEvidenceForm eoiEvidenceForm = new EoiEvidenceForm();

        Optional<ApplicationEoiEvidenceResponseResource> eoiEvidence = applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId).getSuccess();
        if (eoiEvidence.isPresent() && (eoiEvidence.get().getFileEntryId() != null)) {
            FileEntryResource file = fileEntryRestService.findOne(eoiEvidence.get().getFileEntryId()).getSuccess();
            eoiEvidenceForm.setEvidenceFileEntryName(file.getName());
        }
        return eoiEvidenceForm;
    }
}
