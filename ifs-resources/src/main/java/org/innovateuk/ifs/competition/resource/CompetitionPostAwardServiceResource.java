package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompetitionPostAwardServiceResource {
    private Long competitionId;
    private PostAwardService postAwardService;

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public PostAwardService getPostAwardService() {
        return postAwardService;
    }

    public void setPostAwardService(PostAwardService postAwardService) {
        this.postAwardService = postAwardService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final CompetitionPostAwardServiceResource that = (CompetitionPostAwardServiceResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(postAwardService, that.postAwardService)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(postAwardService)
                .toHashCode();
    }
}
