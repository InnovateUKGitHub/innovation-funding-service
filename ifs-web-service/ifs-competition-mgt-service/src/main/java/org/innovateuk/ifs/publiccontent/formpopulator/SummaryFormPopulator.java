package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.SummaryForm;
import org.springframework.stereotype.Service;


@Service
public class SummaryFormPopulator extends AbstractPublicContentFormPopulator<SummaryForm> implements PublicContentFormPopulator<SummaryForm> {

    @Override
    protected SummaryForm createInitial() {
        return new SummaryForm();
    }

    @Override
    protected void populateSection(SummaryForm form, PublicContentResource publicContentResource) {
        form.setDescription(publicContentResource.getSummary());

        //TODO: I think this can be improved...
        FundingType type = publicContentResource.getFundingType();
        if (type != null) {
            form.setFundingType(publicContentResource.getFundingType().getDisplayName());
        } else {
            form.setFundingType("");
        }

        form.setProjectSize(publicContentResource.getProjectSize());
    }
}
