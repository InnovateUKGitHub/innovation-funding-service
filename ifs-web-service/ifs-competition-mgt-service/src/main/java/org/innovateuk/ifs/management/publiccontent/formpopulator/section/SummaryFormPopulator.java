package org.innovateuk.ifs.management.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.form.section.SummaryForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.AbstractContentGroupFormPopulator;
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
