package org.innovateuk.ifs.competitionsetup.fundinginformation.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.competitionsetup.fundinginformation.form.AdditionalInfoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Competition setup section saver for the additional info section.
 */
@Service
public class AdditionalInfoSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

	@Autowired
	private CompetitionSetupRestService competitionSetupRestService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		AdditionalInfoForm additionalInfoForm = (AdditionalInfoForm) competitionSetupForm;
		setFieldsAllowedFromChangeAfterSetupAndLive(competition, additionalInfoForm);

		return competitionSetupRestService.update(competition).toServiceResult();
	}

	private void setFieldsAllowedFromChangeAfterSetupAndLive(CompetitionResource competition, AdditionalInfoForm additionalInfoForm) {
		competition.setBudgetCode(additionalInfoForm.getBudgetCode());
		competition.setPafCode(additionalInfoForm.getPafNumber());
		competition.setActivityCode(additionalInfoForm.getActivityCode());

		competition.setFunders(new ArrayList());
		additionalInfoForm.getFunders().forEach(funder -> {
			CompetitionFunderResource competitionFunderResource = new CompetitionFunderResource();
			competitionFunderResource.setFunder(funder.getFunder());
			competitionFunderResource.setFunderBudget(funder.getFunderBudget());
			competitionFunderResource.setCoFunder(funder.getCoFunder());
			competition.getFunders().add(competitionFunderResource);
		});
	}

    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AdditionalInfoForm.class.equals(clazz);
	}
}