package org.innovateuk.ifs.publiccontent.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

/**
 * View model for the Summary section.
 */
public class SummaryViewModel extends AbstractPublicContentViewModel {

    static private FundingType[] fundingTypes = FundingType.values();

    static public FundingType[] getFundingTypes() {
        return fundingTypes;
    }

}
