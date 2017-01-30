package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.SearchInformationForm;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class SearchInformationFormPopulator extends AbstractPublicContentFormPopulator<SearchInformationForm> implements PublicContentFormPopulator<SearchInformationForm> {

    @Override
    protected SearchInformationForm createInitial() {
        return new SearchInformationForm();
    }

    @Override
    protected void populateSection(SearchInformationForm form, PublicContentResource publicContentResource) {
        form.setEligibilitySummary(publicContentResource.getEligibilitySummary());
        form.setProjectFundingRange(publicContentResource.getProjectFundingRange());
        form.setShortDescription(publicContentResource.getShortDescription());
        form.setKeywords(publicContentResource.getKeywords().stream().collect(Collectors.joining(",")));
    }
}
