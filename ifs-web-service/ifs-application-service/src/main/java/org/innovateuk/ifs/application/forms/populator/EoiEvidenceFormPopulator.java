package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.form.EoiEvidenceForm;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EoiEvidenceFormPopulator {

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    public EoiEvidenceForm populate(EoiEvidenceForm form, long applicationId) {

//        Optional<MultipartFile> file = Optional.of(form.getEoiEvidenceFile());
        Optional<ApplicationEoiEvidenceResponseResource> eoiEvidence = applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId).getSuccess();
        if (eoiEvidence.isPresent()) {
//            String fileName = fileEntryRestService.findOne(eoiEvidence.get().getFileEntryId()).getSuccess().getName();
//            form.setEvidenceFileEntryId(eoiEvidence.get().getFileEntryId());
            form.setEvidenceFileEntryName(form.getEvidenceFileEntryName());
//            form.setEvidenceFileEntryId();
            return form;
        } else {
            return new EoiEvidenceForm();
        }
    }
}
