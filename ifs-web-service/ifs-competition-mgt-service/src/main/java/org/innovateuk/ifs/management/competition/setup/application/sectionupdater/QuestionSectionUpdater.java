package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class QuestionSectionUpdater extends AbstractApplicationSectionUpdater implements CompetitionSetupSubsectionUpdater {

	@Override
	public CompetitionSetupSubsection subsectionToSave() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return QuestionForm.class.equals(clazz);
	}

    @Override
    protected void mapGuidanceRows(AbstractQuestionForm abstractQuestionForm) {
        QuestionForm form = (QuestionForm) abstractQuestionForm;
        form.getQuestion().setGuidanceRows(new ArrayList<>());
        form.getGuidanceRows().forEach(guidanceRow -> {
            GuidanceRowResource guidanceRowResource = new GuidanceRowResource();
            guidanceRowResource.setJustification(guidanceRow.getJustification());
            guidanceRowResource.setSubject(guidanceRow.getScoreFrom() + "," + guidanceRow.getScoreTo());
            form.getQuestion().getGuidanceRows().add(guidanceRowResource);
        });
    }

    private String modifySubject(String subject, String value1, String value2) {
        // Initialise subject for newly created guidance rows as will be null
        if (subject == null) {
            subject = "";
        }
        String[] splitSubject = subject.split(",");
        if (value2 == null) {
            return value1 + "," + (splitSubject.length > 1 ? splitSubject[1] : 0);
        } else {
            return (splitSubject.length > 1 ? splitSubject[0] : 0) + "," + value2;
        }
    }

}
