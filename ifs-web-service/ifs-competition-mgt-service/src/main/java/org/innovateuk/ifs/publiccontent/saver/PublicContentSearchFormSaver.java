package org.innovateuk.ifs.publiccontent.saver;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;
import org.innovateuk.ifs.publiccontent.form.SearchForm;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PublicContentSearchFormSaver extends AbstractPublicContentFormSaver<SearchForm> implements PublicContentFormSaver<SearchForm> {

    @Override
    protected void populateResource(SearchForm form, PublicContentResource publicContentResource) {
        publicContentResource.setShortDescription(form.getShortDescription());
        publicContentResource.setProjectFundingRange(form.getProjectFundingRange());
        publicContentResource.setEligibilitySummary(form.getEligibilitySummary());
        publicContentResource.setKeywords(Lists.newArrayList(Splitter.on(",").split(form.getKeywords()))
            .stream().map(StringUtils::normalizeSpace).collect(Collectors.toList()));
    }

    @Override
    protected PublicContentSection getType() {
        return PublicContentSection.SEARCH;
    }
}
