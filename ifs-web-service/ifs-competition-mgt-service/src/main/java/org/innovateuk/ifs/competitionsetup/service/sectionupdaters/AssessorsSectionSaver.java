package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Competition setup section saver for the assessor section.
 */
@Service
public class AssessorsSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

	@Autowired
	private CompetitionService competitionService;
	
	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public ServiceResult<Void> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
		AssessorsForm assessorsForm = (AssessorsForm) competitionSetupForm;
		
		competition.setAssessorCount(assessorsForm.getAssessorCount());
		competition.setAssessorPay(assessorsForm.getAssessorPay());

        return competitionService.update(competition);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AssessorsForm.class.equals(clazz);
	}

}
