package org.innovateuk.ifs.application.forms.sections.yourfeccosts.form;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

/**
 * A populator to build a YourFECCostsFormPopulator
 */
@Component
public class YourFECModelFormPopulator {

    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    YourFECModelFormPopulator(ApplicationFinanceRestService applicationFinanceRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
    }

    public YourFECModelForm populate(YourFECModelForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance =
                applicationFinanceRestService.getApplicationFinance(applicationId, organisationId).getSuccess();

        Boolean fecModelEnabled = applicationFinance.getFecModelEnabled();
        form.setFecModelEnabled(fecModelEnabled);
        Long fecFileEntryId = applicationFinance.getFecFileEntry();
        form.setFecFileEntryId(fecFileEntryId);
        String fecCertificateFileName = ofNullable(fecFileEntryId)
                .map(fileEntryRestService::findOne)
                .flatMap(RestResult::getOptionalSuccessObject)
                .map(FileEntryResource::getName)
                .orElse(null);

        form.setFecCertificateFileName(fecCertificateFileName);
        return form;
    }
}
