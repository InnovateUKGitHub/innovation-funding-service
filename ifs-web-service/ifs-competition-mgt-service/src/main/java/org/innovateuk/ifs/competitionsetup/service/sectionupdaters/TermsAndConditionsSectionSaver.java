package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.TermsAndConditionsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Competition setup section saver for the terms and conditions section.
 */
@Service
public class TermsAndConditionsSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

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

        try {
            competitionRestService.updateTermsAndConditionsForCompetition(
                    competitionResource.getId(),
                    form.getTermsAndConditionsId()
            ).getSuccess();
        } catch (RuntimeException e) {
            LOG.error("Competition object not available");
            return serviceFailure(asList(new Error("competition.setup.autosave.should.be.completed", HttpStatus.BAD_REQUEST)));
        }

        return serviceSuccess();
    }
}
