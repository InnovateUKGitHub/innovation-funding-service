package org.innovateuk.ifs.publiccontent.viewmodel.section;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentGroupViewModel;

/**
 * View model for the Summary section.
 */
public class SummaryViewModel extends AbstractPublicContentGroupViewModel {

    static private FundingType[] fundingTypes = FundingType.values();

    static public FundingType[] getFundingTypes() {
        return fundingTypes;
    }

}
