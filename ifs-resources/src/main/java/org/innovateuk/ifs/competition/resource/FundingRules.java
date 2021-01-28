package org.innovateuk.ifs.competition.resource;

import com.google.common.base.CaseFormat;

public enum FundingRules {

    SUBSIDY_CONTROL("Subsidy control"),
    STATE_AID("State aid"),
    NOT_AID("Non-aid");

    private String displayName;

    FundingRules(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String toUrl() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, this.name());
    }

    public static FundingRules fromUrl(String url) {
        return FundingRules.valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, url));
    }
}
