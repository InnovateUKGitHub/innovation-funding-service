package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GrantOfferLetterModelTest {

    @Test
    public void testShowSubmitButtonWhenSentSignedAndNotSubmittedAsProjectManager() {

        boolean projectManager = true;
        FileDetailsViewModel signedGrantOfferLetterFile = new FileDetailsViewModel("signed-grant-offer", 1000L);
        ZonedDateTime submittedDate = null;

        GrantOfferLetterModel showSubmitButton = createShowSubmitButtonModel(projectManager,
                signedGrantOfferLetterFile, submittedDate);

        assertThat(showSubmitButton.isShowSubmitButton(), is(true));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenNotProjectManager() {

        boolean projectManager = false;
        FileDetailsViewModel signedGrantOfferLetterFile = new FileDetailsViewModel("signed-grant-offer", 1000L);
        ZonedDateTime submittedDate = null;

        GrantOfferLetterModel showSubmitButton = createShowSubmitButtonModel(projectManager,
                signedGrantOfferLetterFile, submittedDate);

        assertThat(showSubmitButton.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenSignedLetterNotYetUploaded() {

        boolean projectManager = true;
        FileDetailsViewModel signedGrantOfferLetterFile = null;
        ZonedDateTime submittedDate = null;

        GrantOfferLetterModel showSubmitButton = createShowSubmitButtonModel(projectManager,
                signedGrantOfferLetterFile, submittedDate);

        assertThat(showSubmitButton.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowSubmitButtonNotShownWhenSignedLetterAlreadySubmitted() {

        boolean projectManager = true;
        FileDetailsViewModel signedGrantOfferLetterFile = new FileDetailsViewModel("signed-grant-offer", 1000L);
        ZonedDateTime submittedDate = ZonedDateTime.now();

        GrantOfferLetterModel showSubmitButton = createShowSubmitButtonModel(projectManager,
                signedGrantOfferLetterFile, submittedDate);

        assertThat(showSubmitButton.isShowSubmitButton(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageWhenProjectManagerAndGrantOfferIsRejected() {

        boolean projectManager = true;
        boolean grantOfferLetterRejected = true;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterRejectedMessageModel(projectManager, grantOfferLetterRejected);

        assertThat(showMessage.isShowGrantOfferLetterRejectedMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageNotShownWhenNotProjectManager() {

        boolean projectManager = false;
        boolean grantOfferLetterRejected = true;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterRejectedMessageModel(projectManager, grantOfferLetterRejected);

        assertThat(showMessage.isShowGrantOfferLetterRejectedMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterRejectedMessageNotShownWhenGrantOfferLetterNotRejected() {

        boolean projectManager = true;
        boolean grantOfferLetterRejected = false;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterRejectedMessageModel(projectManager, grantOfferLetterRejected);

        assertThat(showMessage.isShowGrantOfferLetterRejectedMessage(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferIfUploadedAndNotSubmittedAndLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferLetterUploaded = true;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(true));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfNotLeadPartnerOrProjectManager() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean signedGrantOfferLetterUploaded = true;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfNotUploaded() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferLetterUploaded = false;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfSubmittedToInnovate() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferLetterUploaded = true;
        boolean submittedToInnovate = true;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferIfRejectedAndProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferLetterUploaded = true;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = true;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(true));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfRejectedButNotProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean signedGrantOfferLetterUploaded = true;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = true;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testAbleToRemoveSignedGrantOfferNotAllowedIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean signedGrantOfferLetterUploaded = true;
        boolean submittedToInnovate = true;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = true;

        GrantOfferLetterModel showMessage = createAbleToRemoveSignedGrantOfferModel(leadPartner, projectManager, signedGrantOfferLetterUploaded, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isAbleToRemoveSignedGrantOffer(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageIfSubmittedToInnovate() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean submittedToInnovate = true;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterReceivedByInnovateMessageModel(leadPartner, projectManager, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageIfRejectedAndNotLeadPartnerOrProjectManager() {

        boolean leadPartner = false;
        boolean projectManager = false;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = true;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterReceivedByInnovateMessageModel(leadPartner, projectManager, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isShowGrantOfferLetterReceivedByInnovateMessage(), is(true));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfRejectedAndLeadPartner() {

        boolean leadPartner = true;
        boolean projectManager = false;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = true;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterReceivedByInnovateMessageModel(leadPartner, projectManager, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfRejectedAndProjectManager() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = true;
        boolean grantOfferLetterApproved = false;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterReceivedByInnovateMessageModel(leadPartner, projectManager, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    @Test
    public void testShowGrantOfferLetterReceivedByInnovateMessageNotAllowedIfApproved() {

        boolean leadPartner = true;
        boolean projectManager = true;
        boolean submittedToInnovate = false;
        boolean grantOfferLetterRejected = false;
        boolean grantOfferLetterApproved = true;

        GrantOfferLetterModel showMessage = createShowGrantOfferLetterReceivedByInnovateMessageModel(leadPartner, projectManager, submittedToInnovate, grantOfferLetterRejected, grantOfferLetterApproved);

        assertThat(showMessage.isShowGrantOfferLetterReceivedByInnovateMessage(), is(false));
    }

    private GrantOfferLetterModel createShowSubmitButtonModel(
            boolean projectManager,
            FileDetailsViewModel signedGrantOfferLetterFile,
            ZonedDateTime submittedDate) {

        boolean grantOfferLetterSent = true;
        boolean leadPartner = true;
        FileDetailsViewModel grantOfferLetterFile = new FileDetailsViewModel("grant-offer", 1000L);
        FileDetailsViewModel additionalContractFile = new FileDetailsViewModel("additional-contracts", 1000L);
        boolean grantOfferLetterApproved = false;
        boolean grantOfferLetterRejected = false;

        return new GrantOfferLetterModel(123L, "Project name", leadPartner,
                grantOfferLetterFile, signedGrantOfferLetterFile, additionalContractFile,
                submittedDate, projectManager, grantOfferLetterApproved, grantOfferLetterSent,
                grantOfferLetterRejected);
    }

    private GrantOfferLetterModel createShowGrantOfferLetterRejectedMessageModel(
            boolean projectManager,
            boolean grantOfferLetterRejected) {

        boolean grantOfferLetterSent = true;
        boolean leadPartner = true;
        FileDetailsViewModel grantOfferLetterFile = new FileDetailsViewModel("grant-offer", 1000L);
        FileDetailsViewModel additionalContractFile = new FileDetailsViewModel("additional-contracts", 1000L);
        boolean grantOfferLetterApproved = false;
        FileDetailsViewModel signedGrantOfferLetterFile = new FileDetailsViewModel("signed-grant-offer", 1000L);
        ZonedDateTime submittedDate = grantOfferLetterRejected ? null : ZonedDateTime.now();

        return new GrantOfferLetterModel(123L, "Project name", leadPartner,
                grantOfferLetterFile, signedGrantOfferLetterFile, additionalContractFile,
                submittedDate, projectManager, grantOfferLetterApproved, grantOfferLetterSent,
                grantOfferLetterRejected);
    }

    private GrantOfferLetterModel createAbleToRemoveSignedGrantOfferModel(
            boolean leadPartner,
            boolean projectManager,
            boolean signedGrantOfferLetterUploaded,
            boolean submittedToInnovate,
            boolean grantOfferLetterRejected,
            boolean grantOfferLetterApproved) {

        boolean grantOfferLetterSent = true;
        FileDetailsViewModel grantOfferLetterFile = new FileDetailsViewModel("grant-offer", 1000L);
        FileDetailsViewModel additionalContractFile = new FileDetailsViewModel("additional-contracts", 1000L);
        FileDetailsViewModel signedGrantOfferLetterFile = signedGrantOfferLetterUploaded ?
                new FileDetailsViewModel("signed-grant-offer", 1000L) :
                null;
        ZonedDateTime submittedDate = submittedToInnovate ? ZonedDateTime.now() : null;

        return new GrantOfferLetterModel(123L, "Project name", leadPartner,
                grantOfferLetterFile, signedGrantOfferLetterFile, additionalContractFile,
                submittedDate, projectManager, grantOfferLetterApproved, grantOfferLetterSent,
                grantOfferLetterRejected);
    }

    private GrantOfferLetterModel createShowGrantOfferLetterReceivedByInnovateMessageModel(
            boolean leadPartner,
            boolean projectManager,
            boolean submittedToInnovate,
            boolean grantOfferLetterRejected,
            boolean grantOfferLetterApproved) {

        boolean grantOfferLetterSent = true;
        FileDetailsViewModel grantOfferLetterFile = new FileDetailsViewModel("grant-offer", 1000L);
        FileDetailsViewModel additionalContractFile = new FileDetailsViewModel("additional-contracts", 1000L);
        FileDetailsViewModel signedGrantOfferLetterFile = new FileDetailsViewModel("signed-grant-offer", 1000L);
        ZonedDateTime submittedDate = submittedToInnovate ? ZonedDateTime.now() : null;

        return new GrantOfferLetterModel(123L, "Project name", leadPartner,
                grantOfferLetterFile, signedGrantOfferLetterFile, additionalContractFile,
                submittedDate, projectManager, grantOfferLetterApproved, grantOfferLetterSent,
                grantOfferLetterRejected);
    }
}
