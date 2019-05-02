package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.GrantAgreementReadOnlyViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class GrantAgreementReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<GrantAgreementReadOnlyViewModel> {

    private final EuGrantTransferRestService grantTransferRestService;

    public GrantAgreementReadOnlyPopulator(EuGrantTransferRestService grantTransferRestService) {
        this.grantTransferRestService = grantTransferRestService;
    }

    @Override
    public GrantAgreementReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data) {
        Optional<FileEntryResource> grantAgreement = grantTransferRestService.findGrantAgreement(data.getApplication().getId()).getOptionalSuccessObject();

        return new GrantAgreementReadOnlyViewModel(data, question,
                grantAgreement.map(FileEntryResource::getName).orElse(null));
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.GRANT_AGREEMENT);
    }
}
