package org.innovateuk.ifs.management.competition.setup.fundingamountsought.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundingAmountSoughtSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser) {
        FundingAmountSoughtForm form = (FundingAmountSoughtForm) competitionSetupForm;
        competition.getCompetitionApplicationConfigResource().setMaximumFundingSoughtEnabled(form.getFundingAmountSoughtApplicable());
        return competitionSetupRestService.update(competition).toServiceResult();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return FundingAmountSoughtForm.class.equals(clazz);
    }
}
