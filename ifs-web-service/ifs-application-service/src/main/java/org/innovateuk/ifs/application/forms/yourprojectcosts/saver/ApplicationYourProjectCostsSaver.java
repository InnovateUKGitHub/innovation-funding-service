package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Component
public class ApplicationYourProjectCostsSaver extends AbstractYourProjectCostsSaver {
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private DefaultFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(YourProjectCostsForm form, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        try {
            return save(form, applicationId, organisation.getId());
        } catch (ExecutionException |InterruptedException e) {
            throw new IFSRuntimeException(e, Collections.emptyList());
        }
    }

    @Override
    protected BaseFinanceResource getFinanceResource(long applicationId, long organisationId) {
        return applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
    }

    @Override
    protected FinanceRowRestService getFinanceRowService() {
        return financeRowRestService;
    }
}
