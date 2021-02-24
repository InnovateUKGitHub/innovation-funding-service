package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisPartnerViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class SubsidyBasisReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    public final ApplicationSubsidyBasisPartnerViewModel applicationSubsidyBasisPartnerViewModel;
    public final ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel;

    public SubsidyBasisReadOnlyViewModel(ApplicationReadOnlyData data,
                                         QuestionResource question,
                                         ApplicationSubsidyBasisPartnerViewModel applicationSubsidyBasisPartnerViewModel,
                                         ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel
    ) {
        super(data, question);
        this.applicationSubsidyBasisPartnerViewModel = applicationSubsidyBasisPartnerViewModel;
        this.applicationSubsidyBasisViewModel = applicationSubsidyBasisViewModel;
    }

    @Override
    public String getFragment() {
        return "subsidy-basis";
    }

    @Override
    public boolean shouldDisplayActions() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return applicationSubsidyBasisViewModel.isSubsidyBasisCompletedByAllOrganisations();
    }


}

