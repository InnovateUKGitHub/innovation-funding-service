package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for the Send Invites view.
 */
public class SendInviteViewModel {

    private long competitionId;
    private String competitionName;
    private List<String> recipients;
    private String content;

    public SendInviteViewModel(long competitionId, String competitionName, List<String> recipients, String content) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.recipients = recipients;
        this.content = content;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SendInviteViewModel that = (SendInviteViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(recipients, that.recipients)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(recipients)
                .toHashCode();
    }
}
