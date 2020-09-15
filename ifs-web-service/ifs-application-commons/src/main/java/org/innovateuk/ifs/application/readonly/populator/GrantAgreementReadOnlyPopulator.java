package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GrantAgreementReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class GrantAgreementReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<GrantAgreementReadOnlyViewModel> {

    private final EuGrantTransferRestService grantTransferRestService;

    public GrantAgreementReadOnlyPopulator(EuGrantTransferRestService grantTransferRestService) {
        this.grantTransferRestService = grantTransferRestService;
    }

    @Override
    public GrantAgreementReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        Optional<FileEntryResource> grantAgreement = grantTransferRestService.findGrantAgreement(data.getApplication().getId()).getOptionalSuccessObject();

        return new GrantAgreementReadOnlyViewModel(data, question,
                grantAgreement.map(FileEntryResource::getName).orElse(null));
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.GRANT_AGREEMENT);
    }
}
