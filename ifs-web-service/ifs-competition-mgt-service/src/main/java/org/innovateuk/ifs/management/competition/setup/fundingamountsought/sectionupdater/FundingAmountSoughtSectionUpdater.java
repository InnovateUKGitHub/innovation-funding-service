package org.innovateuk.ifs.management.competition.setup.fundingamountsought.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL_PLACES;

@Service
public class FundingAmountSoughtSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser) {
        FundingAmountSoughtForm form = (FundingAmountSoughtForm) competitionSetupForm;
        CompetitionApplicationConfigResource competitionApplicationConfigResource = competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();

        boolean isFundingAmountSoughtApplicable = form.getFundingAmountSoughtApplicable();

        setMaximumFundingSoughtEnabled(form, competitionApplicationConfigResource);

        if (isFundingAmountSoughtApplicable) {
            setMaximumFundingSought(form, competitionApplicationConfigResource);
        }

        return competitionApplicationConfigRestService.update(competition.getId(), competitionApplicationConfigResource).toServiceResult().andOnSuccessReturnVoid();
    }

    private void setMaximumFundingSought(FundingAmountSoughtForm form, CompetitionApplicationConfigResource competitionApplicationConfigResource) {
        competitionApplicationConfigResource.setMaximumFundingSought(ofNullable(form.getFundingAmountSought()).map(v -> v.setScale(MAX_DECIMAL_PLACES, HALF_UP)).orElse(BigDecimal.ZERO));
    }

    private void setMaximumFundingSoughtEnabled(FundingAmountSoughtForm form, CompetitionApplicationConfigResource competitionApplicationConfigResource) {
        competitionApplicationConfigResource.setMaximumFundingSoughtEnabled(form.getFundingAmountSoughtApplicable());
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return FundingAmountSoughtForm.class.equals(clazz);
    }
}
