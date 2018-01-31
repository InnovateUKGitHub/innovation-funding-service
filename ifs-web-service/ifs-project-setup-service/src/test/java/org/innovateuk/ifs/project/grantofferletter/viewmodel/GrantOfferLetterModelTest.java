package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForPartnersView;
import static org.junit.Assert.assertThat;

public class GrantOfferLetterModelTest {

    @Test
    public void testShowSubmitButtonWhenSentSignedAndNotSubmittedAsProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;
        
        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded, 
                stateInformationForNonPartnersView(SENT, GOL_SENT));

        assertThat(model.isShowSubmitButton(), is(true));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, GOL_SENT));

        assertThat(model.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenSignedLetterNotYetUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, GOL_SENT));

        assertThat(model.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenSignedLetterAlreadySubmitted() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(READY_TO_APPROVE, GOL_SIGNED));

        assertThat(model.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageWhenProjectManagerAndGrantOfferIsRejected() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isShowGrantOfferLetterRejectedMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageNotShownWhenNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isShowGrantOfferLetterRejectedMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageNotShownWhenGrantOfferLetterNotRejected() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, null));

        assertThat(model.isShowGrantOfferLetterRejectedMessage(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferIfUploadedAndNotSubmittedAndLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, null));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(true));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfNotLeadPartnerOrProjectManager() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, null));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfNotUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, null));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfSubmittedToInnovate() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(READY_TO_APPROVE, null));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferIfRejectedAndProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(true));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfRejectedButNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED));

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageIfSubmittedToInnovate() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(READY_TO_APPROVE, GOL_SIGNED));

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageIfRejectedAndNotLeadPartnerOrProjectManager() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageShownIfRejectedAndLeadPartnerButNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfRejectedAndProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED));

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED));

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageIfGrantOfferLetterSentButNotYetSignedAndNotLeadPartner() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, GOL_SENT));

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(true));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForPartnersView(SENT, GOL_SENT));

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(SENT, GOL_SENT));

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfGrantOfferNotYetSent() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(PENDING, PROJECT_CREATED));

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfSigned() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(READY_TO_APPROVE, GOL_SIGNED));

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterApprovedByInnovateMessageIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED));

        assertThat(model.isShowGrantOfferLetterApprovedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterApprovedByInnovateMessageNotAllowedIfNotApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateInformationForNonPartnersView(READY_TO_APPROVE, GOL_SIGNED));

        assertThat(model.isShowGrantOfferLetterApprovedByInnovateMessage(), is(false));
    }

    private GrantOfferLetterModel createGrantOfferLetterModel(
            boolean leadPartner,
            boolean projectManager,
            boolean signedGrantOfferUploaded,
            GrantOfferLetterStateResource state) {

        FileDetailsViewModel grantOfferLetterFile = 
                state.isGeneratedGrantOfferLetterAlreadySentToProjectTeam() ? 
                        new FileDetailsViewModel("grant-offer", 1000L) : 
                        null;
        
        FileDetailsViewModel additionalContractFile = state.isGeneratedGrantOfferLetterAlreadySentToProjectTeam() ?
                new FileDetailsViewModel("grant-offer", 1000L) :
                null;
        
        FileDetailsViewModel signedGrantOfferLetterFile = signedGrantOfferUploaded ?
                new FileDetailsViewModel("grant-offer", 1000L) :
                null;
        
        ZonedDateTime submittedDate = state.isSignedGrantOfferLetterReceivedByInternalTeam() ? ZonedDateTime.now() : null;

        return new GrantOfferLetterModel(123L, "Project name", leadPartner,
                grantOfferLetterFile, signedGrantOfferLetterFile, additionalContractFile,
                submittedDate, projectManager, state);
    }
}
