package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionFunderResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.model.Funder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Form populator for the additional info competition setup section.
 */
@Service
public class AdditionalInfoFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    CompetitionService competitionService;

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm();

		competitionSetupForm.setActivityCode(competitionResource.getActivityCode());
		competitionSetupForm.setInnovateBudget(competitionResource.getInnovateBudget());

		competitionSetupForm.setCompetitionCode(competitionResource.getCode());
		competitionSetupForm.setPafNumber(competitionResource.getPafCode());
		competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

		competitionResource.getFunders().forEach(funderResource ->  {
			Funder funder = new Funder();
			funder.setFunder(funderResource.getFunder());
			funder.setFunderBudget(funderResource.getFunderBudget());
			funder.setCoFunder(funderResource.getCoFunder());
			competitionSetupForm.getFunders().add(funder);
		});

        if(competitionResource.getFunders().isEmpty()) {
            Funder funder = new Funder();
            funder.setCoFunder(false);
            competitionSetupForm.setFunders(asList(funder));

            initFirstFunder(competitionResource);
        }

        return competitionSetupForm;
	}

    public void initFirstFunder(CompetitionResource competitionResource) {
        CompetitionFunderResource competitionFunderResource = new CompetitionFunderResource();
        competitionFunderResource.setCoFunder(false);
	    competitionResource.setFunders(asList(competitionFunderResource));

        competitionService.update(competitionResource);
    }

}
