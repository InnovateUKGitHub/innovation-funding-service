package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.form.GuidanceRowForm;
import org.innovateuk.ifs.management.competition.setup.application.form.KtpAssessmentForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KtpAssessmentFormPopulator implements CompetitionSetupSubsectionFormPopulator {

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Override
    public CompetitionSetupSubsection sectionToFill() {
        return CompetitionSetupSubsection.KTP_ASSESSMENT;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {

        KtpAssessmentForm competitionSetupForm = new KtpAssessmentForm();

        if (objectId.isPresent()) {
            CompetitionSetupQuestionResource questionResource = questionSetupCompetitionRestService.getByQuestionId(
                    (objectId.get())).getSuccess();
            competitionSetupForm.setQuestion(questionResource);

            competitionSetupForm.getQuestion().getGuidanceRows().forEach(guidanceRowResource -> {
                GuidanceRowForm grvm = new GuidanceRowForm(guidanceRowResource);
                competitionSetupForm.getGuidanceRows().add(grvm);
            });

        }

        return competitionSetupForm;
    }
}
