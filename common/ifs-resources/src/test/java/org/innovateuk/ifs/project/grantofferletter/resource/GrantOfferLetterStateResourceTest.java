package org.innovateuk.ifs.project.grantofferletter.resource;

import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForPartnersView;

public class GrantOfferLetterStateResourceTest {

    @Test
    public void testGeneratedGrantOfferLetterAlreadySentToProjectTeam() {

        stream(GrantOfferLetterState.values()).forEach(state -> {

            GrantOfferLetterStateResource partnerView = stateInformationForPartnersView(state, null);
            GrantOfferLetterStateResource nonPartnerView = stateInformationForNonPartnersView(state, null);

            if (GrantOfferLetterState.PENDING.equals(state)) {
                assertThat(partnerView.isGeneratedGrantOfferLetterAlreadySentToProjectTeam()).isFalse();
                assertThat(nonPartnerView.isGeneratedGrantOfferLetterAlreadySentToProjectTeam()).isFalse();
            } else {
                assertThat(partnerView.isGeneratedGrantOfferLetterAlreadySentToProjectTeam()).isTrue();
                assertThat(nonPartnerView.isGeneratedGrantOfferLetterAlreadySentToProjectTeam()).isTrue();
            }
        });
    }

    @Test
    public void testGeneratedGrantOfferLetterAbleToBeSentToProjectTeam() {

        stream(GrantOfferLetterState.values()).forEach(state -> {

            GrantOfferLetterStateResource partnerView = stateInformationForPartnersView(state, null);
            GrantOfferLetterStateResource nonPartnerView = stateInformationForNonPartnersView(state, null);

            if (GrantOfferLetterState.PENDING.equals(state)) {
                assertThat(partnerView.isGeneratedGrantOfferLetterAbleToBeSentToProjectTeam()).isTrue();
                assertThat(nonPartnerView.isGeneratedGrantOfferLetterAbleToBeSentToProjectTeam()).isTrue();
            } else {
                assertThat(partnerView.isGeneratedGrantOfferLetterAbleToBeSentToProjectTeam()).isFalse();
                assertThat(nonPartnerView.isGeneratedGrantOfferLetterAbleToBeSentToProjectTeam()).isFalse();
            }
        });
    }

    @Test
    public void testSignedGrantOfferLetterReceivedByInternalTeam() {

        stream(GrantOfferLetterState.values()).forEach(state -> {

            GrantOfferLetterStateResource partnerView = stateInformationForPartnersView(state, null);
            GrantOfferLetterStateResource nonPartnerView = stateInformationForNonPartnersView(state, null);

            if (asList(GrantOfferLetterState.READY_TO_APPROVE, GrantOfferLetterState.APPROVED).contains(state)) {
                assertThat(partnerView.isSignedGrantOfferLetterReceivedByInternalTeam()).isTrue();
                assertThat(nonPartnerView.isSignedGrantOfferLetterReceivedByInternalTeam()).isTrue();
            } else {
                assertThat(partnerView.isSignedGrantOfferLetterReceivedByInternalTeam()).isFalse();
                assertThat(nonPartnerView.isSignedGrantOfferLetterReceivedByInternalTeam()).isFalse();
            }
        });
    }

    @Test
    public void testSignedGrantOfferLetterApproved() {

        stream(GrantOfferLetterState.values()).forEach(state -> {

            GrantOfferLetterStateResource partnerView = stateInformationForPartnersView(state, null);
            GrantOfferLetterStateResource nonPartnerView = stateInformationForNonPartnersView(state, null);

            if (GrantOfferLetterState.APPROVED.equals(state)) {
                assertThat(partnerView.isSignedGrantOfferLetterApproved()).isTrue();
                assertThat(nonPartnerView.isSignedGrantOfferLetterApproved()).isTrue();
            } else {
                assertThat(partnerView.isSignedGrantOfferLetterApproved()).isFalse();
                assertThat(nonPartnerView.isSignedGrantOfferLetterApproved()).isFalse();
            }
        });
    }

    @Test
    public void testSignedGrantOfferLetterRejected() {

        stream(GrantOfferLetterState.values()).forEach(state -> {

            GrantOfferLetterStateResource partnerView = stateInformationForPartnersView(state, GrantOfferLetterEvent.SIGNED_GOL_REJECTED);
            GrantOfferLetterStateResource nonPartnerView = stateInformationForNonPartnersView(state, GrantOfferLetterEvent.SIGNED_GOL_REJECTED);

            if (GrantOfferLetterState.SENT.equals(state)) {

                // note here that partners are not able to see the rejection, but rather the signed grant offer still
                // appears to be with the internal team
                assertThat(partnerView.isSignedGrantOfferLetterRejected()).isFalse();
                assertThat(partnerView.isSignedGrantOfferLetterReceivedByInternalTeam()).isTrue();
                assertThat(nonPartnerView.isSignedGrantOfferLetterRejected()).isTrue();
            } else {
                assertThat(partnerView.isSignedGrantOfferLetterRejected()).isFalse();
                assertThat(nonPartnerView.isSignedGrantOfferLetterRejected()).isFalse();
            }
        });
    }
}
