package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationAcademicCostFormPopulator extends AbstractAcademicCostFormPopulator<ApplicationFinanceResource> {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ApplicationFinanceRowRestService applicationFinanceRowRestService;

    public void populate(AcademicCostForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        populate(form, finance);
    }

    protected FinanceRowRestService financeRowRestService() {
        return applicationFinanceRowRestService;
    }

    protected Long getFileEntryId(ApplicationFinanceResource finance) {
        return finance.getFinanceFileEntry();
    }
}
