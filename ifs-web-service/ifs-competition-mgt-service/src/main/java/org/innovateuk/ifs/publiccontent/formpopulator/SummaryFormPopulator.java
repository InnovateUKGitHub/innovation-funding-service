package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.SummaryForm;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class SummaryFormPopulator extends AbstractPublicContentFormPopulator<SummaryForm> implements PublicContentFormPopulator<SummaryForm> {

    @Override
    protected SummaryForm createInitial() {
        return new SummaryForm();
    }

    @Override
    protected void populateSection(SummaryForm form, PublicContentResource publicContentResource) {
        form.setDescription(publicContentResource.getSummary());

        ofNullable(publicContentResource.getFundingType())
                .ifPresent(type ->
                        form.setFundingType(type.getDisplayName()));

        form.setProjectSize(publicContentResource.getProjectSize());
    }
}
