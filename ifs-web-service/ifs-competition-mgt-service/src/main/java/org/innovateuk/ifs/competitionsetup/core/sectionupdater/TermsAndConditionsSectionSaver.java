package org.innovateuk.ifs.competitionsetup.core.sectionupdater;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.TermsAndConditionsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Competition setup section saver for the terms and conditions section.
 */
@Service
public class TermsAndConditionsSectionSaver extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    private static final Log LOG = LogFactory.getLog(TermsAndConditionsSectionSaver.class);

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return TermsAndConditionsForm.class.equals(clazz);
    }

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.TERMS_AND_CONDITIONS;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {
        TermsAndConditionsForm form = (TermsAndConditionsForm) competitionSetupForm;

        return competitionRestService.updateTermsAndConditionsForCompetition(
                competitionResource.getId(),
                form.getTermsAndConditionsId()
        ).toServiceResult();
    }
}
