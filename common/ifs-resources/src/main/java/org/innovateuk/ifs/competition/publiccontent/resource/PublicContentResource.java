package org.innovateuk.ifs.competition.publiccontent.resource;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * The resource for competition public content.
 */
@Setter
@Getter
public class PublicContentResource {

    private Long id;

    private Long competitionId;

    private ZonedDateTime publishDate;

    private String shortDescription;

    private String projectFundingRange;

    private String eligibilitySummary;

    private String projectSize;

    private String summary;

    private Boolean inviteOnly;

    private List<PublicContentSectionResource> contentSections;

    private List<ContentEventResource> contentEvents;

    private List<String> keywords;

    private String hash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PublicContentResource resource = (PublicContentResource) o;

        return new EqualsBuilder()
                .append(id, resource.id)
                .append(competitionId, resource.competitionId)
                .append(publishDate, resource.publishDate)
                .append(shortDescription, resource.shortDescription)
                .append(projectFundingRange, resource.projectFundingRange)
                .append(eligibilitySummary, resource.eligibilitySummary)
                .append(projectSize, resource.projectSize)
                .append(summary, resource.summary)
                .append(contentSections, resource.contentSections)
                .append(contentEvents, resource.contentEvents)
                .append(keywords, resource.keywords)
                .append(hash, resource.hash)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(competitionId)
                .append(publishDate)
                .append(shortDescription)
                .append(projectFundingRange)
                .append(eligibilitySummary)
                .append(projectSize)
                .append(summary)
                .append(contentSections)
                .append(keywords)
                .append(hash)
                .toHashCode();
    }
}
