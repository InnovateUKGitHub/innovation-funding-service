package org.innovateuk.ifs.publiccontent.saver.section;


import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.SummaryForm;
import org.innovateuk.ifs.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Saver for the Summary form on public content setup.
 */
@Service
public class SummaryFormSaver extends AbstractContentGroupFormSaver<SummaryForm> implements PublicContentFormSaver<SummaryForm> {

    @Override
    protected List<Error> populateResource(SummaryForm form, PublicContentResource publicContentResource) {
        publicContentResource.setSummary(form.getDescription());
        publicContentResource.setProjectSize(form.getProjectSize());
        return super.populateResource(form, publicContentResource);
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
