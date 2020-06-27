package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
}
