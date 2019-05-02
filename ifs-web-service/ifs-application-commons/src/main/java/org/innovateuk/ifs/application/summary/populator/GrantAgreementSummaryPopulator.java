package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.NewGrantAgreementSummaryViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class GrantAgreementSummaryPopulator implements QuestionSummaryViewModelPopulator<NewGrantAgreementSummaryViewModel> {

    private final EuGrantTransferRestService grantTransferRestService;

    public GrantAgreementSummaryPopulator(EuGrantTransferRestService grantTransferRestService) {
        this.grantTransferRestService = grantTransferRestService;
    }

    @Override
    public NewGrantAgreementSummaryViewModel populate(QuestionResource question, ApplicationSummaryData data) {
        Optional<FileEntryResource> grantAgreement = grantTransferRestService.findGrantAgreement(data.getApplication().getId()).getOptionalSuccessObject();

        return new NewGrantAgreementSummaryViewModel(data, question,
                grantAgreement.map(FileEntryResource::getName).orElse(null));
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.GRANT_AGREEMENT);
    }
}
