package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.SummaryForm;
import org.springframework.stereotype.Service;

/**
 * Saver for the Summary form on public content setup.
 */
@Service
public class SummaryFormSaver extends AbstractPublicContentFormSaver<SummaryForm> implements PublicContentFormSaver<SummaryForm> {

    @Override
    protected void populateResource(SummaryForm form, PublicContentResource publicContentResource) {
        publicContentResource.setSummary(form.getDescription());
        publicContentResource.setFundingType(FundingType.fromDisplayName(form.getFundingType()));
        publicContentResource.setProjectSize(form.getProjectSize());
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SUMMARY;
    }
}
