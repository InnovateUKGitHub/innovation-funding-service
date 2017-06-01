package org.innovateuk.ifs.publiccontent.saver.section;


import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.SearchInformationForm;
import org.innovateuk.ifs.publiccontent.saver.AbstractPublicContentFormSaver;
import org.innovateuk.ifs.publiccontent.saver.PublicContentFormSaver;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Saver for the SearchInformationForm form.
 */
@Service
public class SearchInformationFormSaver extends AbstractPublicContentFormSaver<SearchInformationForm> implements PublicContentFormSaver<SearchInformationForm> {
    private static final int MAXIMUM_KEYWORD_LENGTH = 50;


    @Override
    protected List<Error> populateResource(SearchInformationForm form, PublicContentResource publicContentResource) {
        publicContentResource.setShortDescription(form.getShortDescription());
        publicContentResource.setProjectFundingRange(form.getProjectFundingRange());
        publicContentResource.setEligibilitySummary(form.getEligibilitySummary());
        publicContentResource.setKeywords(splitAndNormaliseKeywords(form.getKeywords()));
        publicContentResource.setInviteOnly(convertToInviteBoolean(form.getPublishSetting()));
        return validateKeywords(publicContentResource.getKeywords());
    }

    private boolean convertToInviteBoolean(String publishSetting) {
        return null != publishSetting && publishSetting.equals("invite");
    }

    private List<Error> validateKeywords(List<String> keywords) {
        return keywords.stream()
                .filter(keyword -> keyword.length() > MAXIMUM_KEYWORD_LENGTH)
                .findAny()
                .map(invalidKeyword -> singletonList(new Error(CommonFailureKeys.PUBLIC_CONTENT_KEYWORD_TOO_LONG, invalidKeyword)))
                .orElse(emptyList());
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
