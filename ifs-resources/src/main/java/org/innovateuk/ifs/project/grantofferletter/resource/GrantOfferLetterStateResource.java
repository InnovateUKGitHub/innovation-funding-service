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
    private String lastProcessEvent;

    private GrantOfferLetterStateResource(GrantOfferLetterState state, String lastProcessEvent) {
        this.state = state;
        this.lastProcessEvent = lastProcessEvent;
    }

    public static GrantOfferLetterStateResource forPartnerView(GrantOfferLetterState state, String lastProcessEvent) {

        if (isRejectedInternal(state, lastProcessEvent)) {
            return new GrantOfferLetterStateResource(SENT, null);
        }

        return new GrantOfferLetterStateResource(state, lastProcessEvent);
    }

    public static GrantOfferLetterStateResource forNonPartnerView(GrantOfferLetterState state, String lastProcessEvent) {
        return new GrantOfferLetterStateResource(state, lastProcessEvent);
    }

    // for json marshalling
    @SuppressWarnings("unused")
    GrantOfferLetterStateResource() {
    }

    public GrantOfferLetterState getState() {
        return state;
    }

    // for json marshalling
    @SuppressWarnings("unused")
    void setState(GrantOfferLetterState state) {
        this.state = state;
    }

    public String getLastProcessEvent() {
        return lastProcessEvent;
    }

    // for json marshalling
    @SuppressWarnings("unused")
    void setLastProcessEvent(String lastProcessEvent) {
        this.lastProcessEvent = lastProcessEvent;
    }

    public boolean isGeneratedGrantOfferLetterAlreadySentToProjectTeam() {
        return !PENDING.equals(state);
    }

    public boolean isGeneratedGrantOfferLetterAbleToBeSent() {
        return PENDING.equals(state);
    }

    public boolean isSignedGrantOfferLetterRejected() {
        return isRejectedInternal(state, lastProcessEvent);
    }

    public boolean isSignedGrantOfferLetterApproved() {
        return APPROVED.equals(state);
    }

    public boolean isSignedGrantOfferLetterReceivedByInternalTeam() {
        return SIGNED_GRANT_OFFER_LETTER_WITH_INTERNAL_TEAM_STATES.contains(state);
    }

    private static boolean isRejectedInternal(GrantOfferLetterState state, String lastProcessEvent) {
        return SENT.equals(state) && SIGNED_GOL_REJECTED.getType().equalsIgnoreCase(lastProcessEvent);
    }

}
