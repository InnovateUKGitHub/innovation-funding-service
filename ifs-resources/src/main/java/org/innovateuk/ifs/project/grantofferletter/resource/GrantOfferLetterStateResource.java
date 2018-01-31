package org.innovateuk.ifs.project.grantofferletter.resource;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.SIGNED_GOL_REJECTED;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;

/**
 * A helper class to gather information about the current state of a Grant Offer Letter, based on a combination of
 * current state, previous events and user permissions
 */
public class GrantOfferLetterStateResource {

    public static final List<GrantOfferLetterState> SIGNED_GRANT_OFFER_LETTER_WITH_INTERNAL_TEAM_STATES = asList(READY_TO_APPROVE, APPROVED);
    private GrantOfferLetterState state;
    private GrantOfferLetterEvent lastEvent;
    private boolean hideRejection;

    private GrantOfferLetterStateResource(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent, boolean hideRejection) {
        this.state = state;
        this.lastEvent = lastEvent;
        this.hideRejection = hideRejection;
    }

    public static GrantOfferLetterStateResource forPartnerView(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {

        if (isRejectedInternal(state, lastEvent)) {
            return new GrantOfferLetterStateResource(SENT, lastEvent, true);
        }

        return new GrantOfferLetterStateResource(state, lastEvent, false);
    }

    public static GrantOfferLetterStateResource forNonPartnerView(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {
        return new GrantOfferLetterStateResource(state, lastEvent, false);
    }

    // for json marshalling
    @SuppressWarnings("unused")
    GrantOfferLetterStateResource() {
    }

    // for json marshalling
    GrantOfferLetterState getState() {
        return state;
    }

    // for json marshalling
    @SuppressWarnings("unused")
    void setState(GrantOfferLetterState state) {
        this.state = state;
    }

    // for json marshalling
    GrantOfferLetterEvent getLastEvent() {
        return lastEvent;
    }

    // for json marshalling
    @SuppressWarnings("unused")
    void setLastEvent(GrantOfferLetterEvent lastEvent) {
        this.lastEvent = lastEvent;
    }

    public boolean isGeneratedGrantOfferLetterAlreadySentToProjectTeam() {
        return !PENDING.equals(state);
    }

    public boolean isGeneratedGrantOfferLetterAbleToBeSentToProjectTeam() {
        return !isGeneratedGrantOfferLetterAlreadySentToProjectTeam();
    }

    public boolean isSignedGrantOfferLetterRejected() {
        return !hideRejection && isRejectedInternal(state, lastEvent);
    }

    public boolean isSignedGrantOfferLetterApproved() {
        return APPROVED.equals(state);
    }

    public boolean isSignedGrantOfferLetterReceivedByInternalTeam() {
        return hideRejection || SIGNED_GRANT_OFFER_LETTER_WITH_INTERNAL_TEAM_STATES.contains(state);
    }

    private static boolean isRejectedInternal(GrantOfferLetterState state, GrantOfferLetterEvent lastEvent) {
        return SENT.equals(state) && SIGNED_GOL_REJECTED.equals(lastEvent);
    }
}
