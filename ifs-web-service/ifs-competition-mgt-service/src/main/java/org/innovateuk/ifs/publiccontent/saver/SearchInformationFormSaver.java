package org.innovateuk.ifs.publiccontent.saver;


import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.SearchInformationForm;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Saver for the SearchInformationForm form.
 */
@Service
public class SearchInformationFormSaver extends AbstractPublicContentFormSaver<SearchInformationForm> implements PublicContentFormSaver<SearchInformationForm> {

    @Override
    protected List<Error> populateResource(SearchInformationForm form, PublicContentResource publicContentResource) {
        publicContentResource.setShortDescription(form.getShortDescription());
        publicContentResource.setProjectFundingRange(form.getProjectFundingRange());
        publicContentResource.setEligibilitySummary(form.getEligibilitySummary());
        publicContentResource.setKeywords(splitAndNormaliseKeywords(form.getKeywords()));
        return Collections.emptyList();
    }


    private List<String> splitAndNormaliseKeywords(String keywords) {
        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(keywords)
                .stream().map(StringUtils::normalizeSpace).collect(Collectors.toList());
    }
    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SEARCH;
    }
}
