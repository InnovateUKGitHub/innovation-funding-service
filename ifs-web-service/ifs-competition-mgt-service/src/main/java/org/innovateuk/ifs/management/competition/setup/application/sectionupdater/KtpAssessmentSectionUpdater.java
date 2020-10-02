package org.innovateuk.ifs.management.competition.setup.application.sectionupdater;

import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.management.competition.setup.application.form.KtpAssessmentForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class KtpAssessmentSectionUpdater extends AbstractApplicationSectionUpdater<KtpAssessmentForm> implements CompetitionSetupSubsectionUpdater {

    @Override
    protected void mapAppendix(KtpAssessmentForm form) {
        //nothing to do here. Ktp assessment sections don't have appendices
    }

    @Override
    protected void mapGuidanceRows(KtpAssessmentForm form) {
        form.getQuestion().setGuidanceRows(new ArrayList<>());
        form.getGuidanceRows().forEach(guidanceRow -> {
            GuidanceRowResource guidanceRowResource = new GuidanceRowResource();
            guidanceRowResource.setJustification(guidanceRow.getJustification());
            guidanceRowResource.setSubject(guidanceRow.getScoreFrom() + "," + guidanceRow.getScoreTo());
            form.getQuestion().getGuidanceRows().add(guidanceRowResource);
        });
    }

    @Override
    public CompetitionSetupSubsection subsectionToSave() {
        return CompetitionSetupSubsection.KTP_ASSESSMENT;
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return KtpAssessmentForm.class.equals(clazz);
    }
}
