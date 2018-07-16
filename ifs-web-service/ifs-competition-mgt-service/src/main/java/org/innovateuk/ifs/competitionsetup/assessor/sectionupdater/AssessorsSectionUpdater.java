package org.innovateuk.ifs.competitionsetup.assessor.sectionupdater;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.competitionsetup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

/**
 * Competition setup section saver for the assessor section.
 */
@Service
public class AssessorsSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

	@Autowired
	private AssessorCountOptionsRestService assessorCountOptionsRestService;

	@Autowired
	private CompetitionSetupRestService competitionSetupRestService;
	
	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

		AssessorsForm assessorsForm = (AssessorsForm) competitionSetupForm;

		if(!sectionToSave().preventEdit(competition)) {


			List<AssessorCountOptionResource> assessorCountOptions = assessorCountOptionsRestService.findAllByCompetitionType(competition.getCompetitionType()).getSuccess();
			if(assessorCountOptions.stream().anyMatch(assessorOption -> assessorsForm.getAssessorCount().equals(assessorOption.getOptionValue()))) {
                setFieldsDisallowedFromChangeAfterSetupAndLive(competition, assessorsForm);
                setFieldsAllowedFromChangeAfterSetupAndLive(competition, assessorsForm);

                return competitionSetupRestService.update(competition).toServiceResult();
            } else {
			    return serviceFailure(fieldError("assessorCount",
                        assessorsForm.getAssessorCount(),
                        "competition.setup.invalid.assessor.count", (Object) null));
            }
		}
		else {
			return serviceFailure(singletonList(new Error("COMPETITION_NOT_EDITABLE", HttpStatus.INTERNAL_SERVER_ERROR)));
		}
	}

	private void setFieldsDisallowedFromChangeAfterSetupAndLive(CompetitionResource competition, AssessorsForm assessorsForm) {
		if(!competition.isSetupAndLive()) {
			competition.setAssessorPay(assessorsForm.getAssessorPay());
		}
	}

	private void setFieldsAllowedFromChangeAfterSetupAndLive(CompetitionResource competition, AssessorsForm assessorsForm) {
		competition.setAssessorCount(assessorsForm.getAssessorCount());
		competition.setHasAssessmentPanel(assessorsForm.getHasAssessmentPanel());
		competition.setHasInterviewStage(assessorsForm.getHasInterviewStage());
		competition.setAssessorFinanceView(assessorsForm.getAssessorFinanceView());
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AssessorsForm.class.equals(clazz);
	}

}
