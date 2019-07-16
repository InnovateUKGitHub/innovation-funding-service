package org.innovateuk.ifs.management.publiccontent.formpopulator.section;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.formpopulator.PublicContentFormPopulator;
import org.innovateuk.ifs.management.publiccontent.form.section.SearchInformationForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.AbstractPublicContentFormPopulator;
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
        form.setPublishSetting(convertToPublishSettingString(publicContentResource.getInviteOnly()));
    }

    private String convertToPublishSettingString(Boolean inviteOnly) {
        if(null == inviteOnly) {
            return "";
        }
        return inviteOnly ? "invite" : "public";
    }

    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.SEARCH;
    }
}
