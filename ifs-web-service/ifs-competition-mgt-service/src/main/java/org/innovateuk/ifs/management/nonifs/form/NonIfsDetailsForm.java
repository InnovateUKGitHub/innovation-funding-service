package org.innovateuk.ifs.management.nonifs.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Form for the Non-IFS competition details page.
 */
public class NonIfsDetailsForm {

    @NotBlank(message = "{validation.standard.title.required}")
    private String title;

    @NotNull
    private Long innovationSectorCategoryId;

    @NotNull
    private Long innovationAreaCategoryId;

    @NotNull(message="{validation.initialdetailsform.fundingType.required}")
    private FundingType fundingType;

    @Valid
    @NotNull(message = "{validation.nonifs.detailsform.opendate.required}")
    private MilestoneRowForm openDate;

    @Valid
    @NotNull(message = "{validation.nonifs.detailsform.closedate.required}")
    private MilestoneRowForm closeDate;

    @Valid
    @NotNull(message = "{validation.nonifs.detailsform.registrationclosedate.required}")
    private MilestoneRowForm registrationCloseDate;

    @Valid
    private MilestoneOrEmptyRowForm applicantNotifiedDate;

    @NotBlank(message= "{validation.nonifs.detailsform.url.required}")
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getInnovationSectorCategoryId() {
        return innovationSectorCategoryId;
    }

    public void setInnovationSectorCategoryId(Long innovationSectorCategoryId) {
        this.innovationSectorCategoryId = innovationSectorCategoryId;
    }

    public Long getInnovationAreaCategoryId() {
        return innovationAreaCategoryId;
    }

    public void setInnovationAreaCategoryId(Long innovationAreaCategoryId) {
        this.innovationAreaCategoryId = innovationAreaCategoryId;
    }

    public MilestoneRowForm getOpenDate() {
        return openDate;
    }

    public void setOpenDate(MilestoneRowForm openDate) {
        this.openDate = openDate;
    }

    public MilestoneRowForm getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(MilestoneRowForm closeDate) {
        this.closeDate = closeDate;
    }

    public MilestoneOrEmptyRowForm getApplicantNotifiedDate() {
        return applicantNotifiedDate;
    }

    public void setApplicantNotifiedDate(MilestoneOrEmptyRowForm applicantNotifiedDate) {
        this.applicantNotifiedDate = applicantNotifiedDate;
    }

    public MilestoneRowForm getRegistrationCloseDate() {
        return registrationCloseDate;
    }

    public void setRegistrationCloseDate(MilestoneRowForm registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public NonIfsDetailsForm setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NonIfsDetailsForm that = (NonIfsDetailsForm) o;

        return new EqualsBuilder()
                .append(title, that.title)
                .append(innovationSectorCategoryId, that.innovationSectorCategoryId)
                .append(innovationAreaCategoryId, that.innovationAreaCategoryId)
                .append(fundingType, that.fundingType)
                .append(openDate, that.openDate)
                .append(closeDate, that.closeDate)
                .append(registrationCloseDate, that.registrationCloseDate)
                .append(applicantNotifiedDate, that.applicantNotifiedDate)
                .append(url, that.url)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(innovationSectorCategoryId)
                .append(innovationAreaCategoryId)
                .append(fundingType)
                .append(openDate)
                .append(closeDate)
                .append(registrationCloseDate)
                .append(applicantNotifiedDate)
                .append(url)
                .toHashCode();
    }
}
