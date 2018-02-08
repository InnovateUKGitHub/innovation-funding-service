package org.innovateuk.ifs.project.grantofferletter.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.SIGNED_GOL_REJECTED;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;

/**
 * A helper class to gather information about the current state of a Grant Offer Letter, based on a combination of
 * current state, previous events and user permissions
 */
public class GrantOfferLetterStateResource {

    private static final List<GrantOfferLetterState> SIGNED_GRANT_OFFER_LETTER_WITH_INTERNAL_TEAM_STATES = asList(READY_TO_APPROVE, APPROVED);

    private GrantOfferLetterState state;
    private GrantOfferLetterEvent lastEvent;

    private GrantOfferLetterStateResource(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {
        this.state = state;
        this.lastEvent = lastEvent;
    }

    public static GrantOfferLetterStateResource stateInformationForPartnersView(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {

        if (isRejectedInternal(state, lastEvent)) {
            return new GrantOfferLetterStateResource(READY_TO_APPROVE, GrantOfferLetterEvent.GOL_SIGNED);
        }

        return new GrantOfferLetterStateResource(state, lastEvent);
    }

    public static GrantOfferLetterStateResource stateInformationForNonPartnersView(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {
        return new GrantOfferLetterStateResource(state, lastEvent);
    }

    // for json marshalling
    @SuppressWarnings("unused")
    GrantOfferLetterStateResource() {
    }

    // for json marshalling
    public GrantOfferLetterState getState() {
        return state;
    }

    // for json marshalling
    @SuppressWarnings("unused")
    public void setState(GrantOfferLetterState state) {
        this.state = state;
    }

    // for json marshalling
    public GrantOfferLetterEvent getLastEvent() {
        return lastEvent;
    }

    // for json marshalling
    @SuppressWarnings("unused")
    public void setLastEvent(GrantOfferLetterEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    @JsonIgnore
    public boolean isGeneratedGrantOfferLetterAlreadySentToProjectTeam() {
        return !PENDING.equals(state);
    }

    @JsonIgnore
    public boolean isGeneratedGrantOfferLetterAbleToBeSentToProjectTeam() {
        return !isGeneratedGrantOfferLetterAlreadySentToProjectTeam();
    }

    @JsonIgnore
    public boolean isSignedGrantOfferLetterRejected() {
        return isRejectedInternal(state, lastEvent);
    }

    @JsonIgnore
    public boolean isSignedGrantOfferLetterApproved() {
        return APPROVED.equals(state);
    }

    @JsonIgnore
    public boolean isSignedGrantOfferLetterReceivedByInternalTeam() {
        return SIGNED_GRANT_OFFER_LETTER_WITH_INTERNAL_TEAM_STATES.contains(state);
    }

    @JsonIgnore
    private static boolean isRejectedInternal(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {
        return SENT.equals(state) && SIGNED_GOL_REJECTED.equals(lastEvent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantOfferLetterStateResource that = (GrantOfferLetterStateResource) o;

        return new EqualsBuilder()
                .append(state, that.state)
                .append(lastEvent, that.lastEvent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(state)
                .append(lastEvent)
                .toHashCode();
    }
}
