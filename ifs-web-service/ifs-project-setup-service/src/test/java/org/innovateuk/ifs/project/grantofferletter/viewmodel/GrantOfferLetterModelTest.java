package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForPartnersView;
import static org.junit.Assert.assertThat;

public class GrantOfferLetterModelTest {

    public static final GrantOfferLetterStateResource stateForPmGeneratedOfferSentToProjectTeam = stateInformationForNonPartnersView(SENT, GOL_SENT);
    public static final GrantOfferLetterStateResource stateForPartnerGeneratedOfferSentToProjectTeam = stateInformationForPartnersView(SENT, GOL_SENT);
    public static final GrantOfferLetterStateResource stateForPmSignedGrantOfferSubmittedToInternalTeam = stateInformationForNonPartnersView(READY_TO_APPROVE, GOL_SIGNED);
    public static final GrantOfferLetterStateResource stateForPmSignedGrantOfferRejected = stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED);
    public static final GrantOfferLetterStateResource stateForPartnerSignedGrantOfferRejected = stateInformationForPartnersView(SENT, SIGNED_GOL_REJECTED);
    public static final GrantOfferLetterStateResource stateForPartnerSignedGrantOfferSubmittedToInternalTeam = stateInformationForPartnersView(READY_TO_APPROVE, GOL_SIGNED);
    public static final GrantOfferLetterStateResource stateForPmSignedGrantOfferApproved = stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED);
    public static final GrantOfferLetterStateResource stateForPmGeneratedGrantOfferNotYetSentToProjectTeam = stateInformationForNonPartnersView(PENDING, PROJECT_CREATED);

    @Test
    public void testShowSubmitButtonWhenSentSignedAndNotSubmittedAsProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;
        
        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowSubmitButton(), is(true));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenSignedLetterNotYetUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenSignedLetterAlreadySubmitted() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageWhenProjectManagerAndGrantOfferIsRejected() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferRejected);

        assertThat(model.isShowGrantOfferLetterRejectedMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageNotShownWhenNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerSignedGrantOfferRejected);

        assertThat(model.isShowGrantOfferLetterRejectedMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageNotShownWhenGrantOfferLetterNotRejected() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isShowGrantOfferLetterRejectedMessage(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferIfUploadedAndNotSubmittedAndLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(true));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfNotLeadPartnerOrProjectManager() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfNotUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedOfferSentToProjectTeam);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfSubmittedToInnovate() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferIfRejectedAndProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferRejected);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(true));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfRejectedButNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerSignedGrantOfferRejected);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferApproved);

        assertThat(model.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageIfSubmittedToInnovate() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageIfRejectedAndNotLeadPartnerOrProjectManager() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerSignedGrantOfferRejected);

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageShownIfRejectedAndLeadPartnerButNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerSignedGrantOfferRejected);

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfRejectedAndProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferRejected);

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferApproved);

        assertThat(model.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageIfGrantOfferLetterSentButNotYetSignedAndNotLeadPartner() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(true));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfGrantOfferNotYetSent() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedGrantOfferNotYetSentToProjectTeam);

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfSigned() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowAwaitingSignatureFromLeadPartnerMessageNotAllowedIfGrantOfferLetterRejectedAndNotLeadPartner() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerSignedGrantOfferRejected);

        assertThat(model.isShowAwaitingSignatureFromLeadPartnerMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterApprovedByInnovateMessageIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferApproved);

        assertThat(model.isShowGrantOfferLetterApprovedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterApprovedByInnovateMessageNotAllowedIfNotApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isShowGrantOfferLetterApprovedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowDisabledSubmitButtonIfProjectManagerAndSignedGrantOfferLetterNotYetUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowDisabledSubmitButton(), is(true));
    }

    @Test
    public void testShowDisabledSubmitButtonNotAllowedIfLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowDisabledSubmitButton(), is(false));
    }

    @Test
    public void testShowDisabledSubmitButtonNotAllowedIfNonLeadPartner() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferUploaded = false;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPartnerGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowDisabledSubmitButton(), is(false));
    }

    @Test
    public void testShowDisabledSubmitButtonNotAllowedIfSignedGrantOfferLetterUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmGeneratedOfferSentToProjectTeam);

        assertThat(model.isShowDisabledSubmitButton(), is(false));
    }

    @Test
    public void testShowDisabledSubmitButtonNotAllowedIfSignedGrantOfferLetterSubmitted() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferUploaded = true;

        GrantOfferLetterModel model = createGrantOfferLetterModel(leadPartner, projectManager, signedGrantOfferUploaded,
                stateForPmSignedGrantOfferSubmittedToInternalTeam);

        assertThat(model.isShowDisabledSubmitButton(), is(false));
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
        
        return new GrantOfferLetterModel(123L, "Project name", leadPartner,
                grantOfferLetterFile, signedGrantOfferLetterFile, additionalContractFile, projectManager, state);
    }
}
