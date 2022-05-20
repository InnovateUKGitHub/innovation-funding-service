package org.innovateuk.ifs.management.publiccontent.form.section;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.management.publiccontent.form.AbstractPublicContentForm;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * Form for the Search page on public content setup.
 */
@FieldRequiredIf(required = "shortDescription", argument = "publicSetting", predicate = true, message = "{validation.publiccontent.searchinformationform.shortDescription.required}")
@FieldRequiredIf(required = "projectFundingRange", argument = "publicSetting", predicate = true, message = "{validation.publiccontent.searchinformationform.projectFundingRange.required}")
@FieldRequiredIf(required = "eligibilitySummary", argument = "publicSetting", predicate = true, message = "{validation.publiccontent.searchinformationform.eligibilitySummary.required}")
@FieldRequiredIf(required = "keywords", argument = "publicSetting", predicate = true, message = "{validation.publiccontent.searchinformationform.keywords.required}")
public class SearchInformationForm extends AbstractPublicContentForm {

    private String shortDescription;
    private String projectFundingRange;
    private String eligibilitySummary;
    private String keywords;
    @NotBlank (message="{validation.publiccontent.searchinformationform.publishsetting.required}")
    private String publishSetting;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getProjectFundingRange() {
        return projectFundingRange;
    }

    public void setProjectFundingRange(String projectFundingRange) {
        this.projectFundingRange = projectFundingRange;
    }

    public String getEligibilitySummary() {
        return eligibilitySummary;
    }

    public void setEligibilitySummary(String eligibilitySummary) {
        this.eligibilitySummary = eligibilitySummary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getPublishSetting() {
        return publishSetting;
    }

    public void setPublishSetting(String publishSetting) {
        this.publishSetting = publishSetting;
    }

    public boolean isPublicSetting() {
        return Objects.equals(publishSetting, "public");
    }
}
