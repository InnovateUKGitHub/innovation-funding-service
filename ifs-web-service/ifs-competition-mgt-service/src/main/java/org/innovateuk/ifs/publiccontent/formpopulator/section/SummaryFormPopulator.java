package org.innovateuk.ifs.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.SummaryForm;
import org.innovateuk.ifs.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
import org.innovateuk.ifs.publiccontent.formpopulator.PublicContentFormPopulator;
import org.springframework.stereotype.Service;

/**
 * Populates the form for the public content Summary screen.
 */

@Service
public class SummaryFormPopulator extends AbstractContentGroupFormPopulator<SummaryForm> implements PublicContentFormPopulator<SummaryForm> {

    @Override
    protected SummaryForm createInitial() {
        return new SummaryForm();
    }

    @Override
    protected void populateSection(SummaryForm form, PublicContentResource publicContentResource) {
        form.setDescription(publicContentResource.getSummary());
        form.setProjectSize(publicContentResource.getProjectSize());
        super.populateSection(form, publicContentResource);
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
