package org.innovateuk.ifs.application.forms.questions.grantagreement.populator;

import org.innovateuk.ifs.application.forms.questions.grantagreement.model.GrantAgreementViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GrantAgreementViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    public GrantAgreementViewModel populate(long applicationId, long questionId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        return new GrantAgreementViewModel(application.getId(), application.getName());
    }
}
