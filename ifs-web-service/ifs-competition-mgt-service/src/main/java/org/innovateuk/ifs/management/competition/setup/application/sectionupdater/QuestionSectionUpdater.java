package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.management.competition.setup.application.form.AbstractQuestionForm;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class QuestionSectionUpdater extends AbstractApplicationSectionUpdater implements CompetitionSetupSubsectionUpdater {

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

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

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        QuestionForm questionForm = (QuestionForm) competitionSetupForm;
        QuestionResource question = questionRestService.getQuestionByCompetitionIdAndFormInputType(competition.getId(), FormInputType.FILEUPLOAD).getSuccess();

        CompetitionSetupQuestionResource questionResource = questionSetupCompetitionRestService.getByQuestionId(question.getId()).getSuccess();
        questionResource.setAppendixCount(questionForm.getAppendixCount());
        if(questionForm.getAppendixCount() == 0) {
            questionResource.setAppendix(false);
        }
        questionResource.setAppendix(true);
        return  questionSetupCompetitionRestService.save(questionResource).toServiceResult();
    }
}
