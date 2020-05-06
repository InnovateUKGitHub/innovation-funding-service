package org.innovateuk.ifs.docusign.transactional;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.EnvelopesApi.ListStatusChangesOptions;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.*;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.sun.jersey.core.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.docusign.api.DocusignApi;
import org.innovateuk.ifs.docusign.domain.DocusignDocument;
import org.innovateuk.ifs.docusign.repository.DocusignDocumentRepository;
import org.innovateuk.ifs.docusign.resource.DocusignRequest;
import org.innovateuk.ifs.docusign.resource.DocusignType;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class DocusignServiceImpl extends RootTransactionalService implements DocusignService {
    private static final Log LOG = LogFactory.getLog(DocusignServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${ifs.docusign.api.account}")
    private String accountId;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private DocusignDocumentRepository docusignDocumentRepository;

    @Autowired
    private FileService fileService;


    @Autowired
    private GrantOfferLetterWorkflowHandler grantOfferLetterWorkflowHandler;

    @Autowired
    private DocusignApi docusignApi;

    @Override
    @Transactional
    public ServiceResult<DocusignDocument> send(DocusignRequest request) {
        try {
            return serviceSuccess(doSend(request));
        } catch (ApiException | IOException e) {
            throw new IFSRuntimeException("Unable to send docusign doc", e);
        }
    }

    @Override
    @Transactional
    public ServiceResult<DocusignDocument> resend(long docusignDocumentId, DocusignRequest request) {
        try {
            docusignDocumentRepository.deleteById(docusignDocumentId);
            return serviceSuccess(doSend(request));
        } catch (ApiException | IOException e) {
            throw new IFSRuntimeException("Unable to send docusign doc", e);
        }
    }

    private DocusignDocument doSend(DocusignRequest request) throws ApiException, IOException {
        DocusignDocument docusignDocument = docusignDocumentRepository.save(new DocusignDocument(request.getRecipientUserId(), request.getDocusignType()));

        byte[] data = ByteStreams.toByteArray(request.getFileAndContents().getContentsSupplier().get());

        Document document = createDocusignDocument(data,
                request.getDocumentName(),
                docusignDocument.getId());

        Signer signer = createDocusignSigner(request.getEmail(),
                request.getName(),
                request.getRecipientUserId(),
                webBaseUrl + request.getRedirectUrl());

        Tabs tabs = createDefaultTabs(data, document, signer);
        signer.setTabs(tabs);

        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign " + request.getDocumentName());
        envelopeDefinition.setDocuments(asList(document));
        Recipients recipients = new Recipients();
        recipients.setSigners(asList(signer));
        envelopeDefinition.setRecipients(recipients);
        envelopeDefinition.setStatus("sent");
        envelopeDefinition.setEmailBlurb("<p>Here is some stuff</p><p>Some more</p><p>With a new line \n After new line </p>");

        EnvelopesApi envelopesApi = new EnvelopesApi(docusignApi.getApiClient());
        EnvelopeSummary results = envelopesApi.createEnvelope(accountId, envelopeDefinition);

        docusignDocument.setEnvelopeId(results.getEnvelopeId());
        return docusignDocument;
    }

    private Signer createDocusignSigner(String email, String name, long recipientUserId, String redirectUrl) {
        return new Signer()
                .email(email)
                .name(name)
                .recipientId(String.valueOf(recipientUserId))
                .clientUserId(String.valueOf(recipientUserId))
                .embeddedRecipientStartURL(redirectUrl);
    }

    private Document createDocusignDocument(byte[] data, String documentName, Long documentId) {
        String docBase64 = new String(Base64.encode(data));
        return new Document().documentBase64(docBase64)
                .name(documentName)
                .fileExtension("pdf")
                .documentId(String.valueOf(documentId));
    }

    private Tabs createDefaultTabs(byte[] data, Document document, Signer signer) throws IOException {
        PDDocument doc = PDDocument.load(data);
        List<InitialHere> initialHereList = new ArrayList<>();
        int i = 0;
        for (PDPage page : doc.getPages()) {
            i++;
            InitialHere initialHere = new InitialHere();
            initialHere.setDocumentId(document.getDocumentId());
            initialHere.setPageNumber(String.valueOf(i));
            initialHere.setRecipientId(signer.getRecipientId());
            initialHere.setTabLabel("Initial here");
            initialHere.setXPosition(String.valueOf((int) page.getMediaBox().getWidth() - 50));
            initialHere.setYPosition("20");
            initialHereList.add(initialHere);
        }
        doc.close();

        SignHere anchor = new SignHere();
        anchor.setAnchorString("Signed: _");
        anchor.setAnchorHorizontalAlignment("right");
        anchor.setAnchorYOffset("-8");
        anchor.setAnchorIgnoreIfNotPresent("false");

        FullName print = new FullName();
        print.anchorString("Print name: _");
        print.setAnchorHorizontalAlignment("right");
        print.setAnchorYOffset("-8");

        DateSigned date = new DateSigned();
        date.anchorString("Date: _");
        date.setAnchorHorizontalAlignment("right");
        date.setAnchorYOffset("-8");

        Text projectStartDate = new Text();
        projectStartDate.anchorString("Project start date _");
        projectStartDate.setAnchorHorizontalAlignment("right");
        projectStartDate.setWidth("200");
        projectStartDate.setAnchorYOffset("-8");
        projectStartDate.setTabLabel("start date");

        Text projectEndDate = new Text();
        projectEndDate.anchorString("Project end date _");
        projectEndDate.setAnchorHorizontalAlignment("right");
        projectEndDate.setWidth("200");
        projectEndDate.setAnchorYOffset("-8");
        projectEndDate.setTabLabel("end date");

        // Add the tabs to the signer object
        // The Tabs object wants arrays of the different field/tab types
        Tabs tabs = new Tabs();
        tabs.setSignHereTabs(singletonList(anchor));
        tabs.setFullNameTabs(singletonList(print));
        tabs.setDateSignedTabs(singletonList(date));
        tabs.setInitialHereTabs(initialHereList);
        tabs.setTextTabs(asList(projectStartDate, projectEndDate));
        return tabs;
    }

    @Override
    @Transactional
    public void downloadFileIfSigned() throws ApiException, IOException {
        LOG.info("Starting import of docusign documents.");
        EnvelopesApi envelopesApi = new EnvelopesApi(docusignApi.getApiClient());
        ListStatusChangesOptions options = envelopesApi.new ListStatusChangesOptions();
        LocalDate date = LocalDate.now().minusDays(1);
        options.setFromDate(DATE_FORMATTER.format(date));
        options.setFromToStatus("completed");
        EnvelopesInformation envelopesInformation = envelopesApi.listStatusChanges(accountId, options);

        for (Envelope envelope : envelopesInformation.getEnvelopes()) {
            Optional<DocusignDocument> document = docusignDocumentRepository.findByEnvelopeId(envelope.getEnvelopeId());
            document = document.filter(d -> d.getSignedDocumentImported() == null);
            if (document.isPresent()) {
                importDocument(document.get());
            }
        }
    }

    @Override
    public String getDocusignUrl(String envelopeId, long userId, String name, String email, String redirect) {
        try {
            Optional<DocusignDocument> document = docusignDocumentRepository.findByEnvelopeId(envelopeId);
            if (document.isPresent()) {
                RecipientViewRequest viewRequest = new RecipientViewRequest();
                viewRequest.setReturnUrl(webBaseUrl + redirect);
                viewRequest.setAuthenticationMethod("none");
                viewRequest.setEmail(email);
                viewRequest.setUserName(name);
                viewRequest.recipientId(String.valueOf(userId));
                viewRequest.setPingUrl(webBaseUrl);
                viewRequest.setPingFrequency("600");
                viewRequest.clientUserId(String.valueOf(userId));

                EnvelopesApi envelopesApi = new EnvelopesApi(docusignApi.getApiClient());
                ViewUrl viewUrl = envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);
                return viewUrl.getUrl();
            }
            return null;
        } catch (ApiException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public ServiceResult<Void> importDocument(String envelopeId) {
        try {
            EnvelopesApi envelopesApi = new EnvelopesApi(docusignApi.getApiClient());
            Envelope envelope = envelopesApi.getEnvelope(accountId, envelopeId);
            if (envelope.getStatus().equals("completed")) {
                Optional<DocusignDocument> document = docusignDocumentRepository.findByEnvelopeId(envelopeId);
                document = document.filter(d -> d.getSignedDocumentImported() == null);
                if (document.isPresent()) {
                    importDocument(document.get());
                }
            }
            return serviceSuccess();
        } catch (ApiException| IOException e) {
            throw new IFSRuntimeException(e);
        }
    }

    private void importDocument(DocusignDocument docusignDocument) throws ApiException, IOException {
        LOG.info("importing docusign document " + docusignDocument.getEnvelopeId());

        EnvelopesApi envelopesApi = new EnvelopesApi(docusignApi.getApiClient());
        byte[] results = envelopesApi.getDocument(accountId, docusignDocument.getEnvelopeId(), String.valueOf(docusignDocument.getId()));

        if (docusignDocument.getType().equals(DocusignType.SIGNED_GRANT_OFFER_LETTER)) {
            linkGrantOfferLetterFileToProject(results, docusignDocument.getProject());
        }
        //Add other document types here.

        docusignDocument.setSignedDocumentImported(ZonedDateTime.now());
    }

    private void linkGrantOfferLetterFileToProject(byte[] results, Project project) throws IOException {
        InputStream stream = ByteSource.wrap(results).openStream();
        FileEntryResource fileEntryResource = new FileEntryResource(project.getName() + " signed grant offer letter", MediaType.APPLICATION_PDF.toString(), results.length);

        fileService.createFile(fileEntryResource, () -> stream)
                .andOnSuccessReturnVoid(fileDetails -> {
                    FileEntry fileEntry = fileDetails.getValue();
                    project.setSignedGrantOfferLetter(fileEntry);
                    project.setOfferSubmittedDate(ZonedDateTime.now());
                    grantOfferLetterWorkflowHandler.sign(project);
                });
    }
}
