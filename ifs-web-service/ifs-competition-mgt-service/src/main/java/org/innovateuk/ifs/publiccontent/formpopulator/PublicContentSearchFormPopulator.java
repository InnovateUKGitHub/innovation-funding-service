package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.SearchForm;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class PublicContentSearchFormPopulator extends AbstractPublicContentFormPopulator<SearchForm> implements PublicContentFormPopulator<SearchForm> {

    @Override
    protected SearchForm createInitial() {
        return new SearchForm();
    }

    @Override
    protected void populateSection(SearchForm form, PublicContentResource publicContentResource) {
        form.setEligibilitySummary(publicContentResource.getEligibilitySummary());
        form.setProjectFundingRange(publicContentResource.getProjectFundingRange());
        form.setShortDescription(publicContentResource.getShortDescription());
        form.setKeywords(publicContentResource.getKeywords().stream().collect(Collectors.joining(",")));
    }
}
