package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.FunderRowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;

/**
 * Form populator for the additional info competition setup section.
 */
@Service
public class AdditionalInfoFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
	private CompetitionSetupRestService competitionSetupRestService;

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm();

		competitionSetupForm.setActivityCode(competitionResource.getActivityCode());
		competitionSetupForm.setCompetitionCode(competitionResource.getCode());
		competitionSetupForm.setPafNumber(competitionResource.getPafCode());
		competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

		competitionResource.getFunders().forEach(funderResource ->  {
			FunderRowForm funder = new FunderRowForm(funderResource);
			competitionSetupForm.getFunders().add(funder);
		});

        if(competitionResource.getFunders().isEmpty()) {
			CompetitionFunderResource competitionFunderResource = initFirstFunder();
			competitionSetupForm.setFunders(asList(new FunderRowForm(competitionFunderResource)));
			competitionResource.setFunders(asList(competitionFunderResource));
            competitionSetupRestService.update(competitionResource);
        }

        return competitionSetupForm;
	}

    public CompetitionFunderResource initFirstFunder() {
        CompetitionFunderResource competitionFunderResource = new CompetitionFunderResource();
        competitionFunderResource.setCoFunder(false);

		return competitionFunderResource;
    }

}
