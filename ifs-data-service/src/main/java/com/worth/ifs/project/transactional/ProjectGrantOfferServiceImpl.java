package com.worth.ifs.project.transactional;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.ResourceLoaderUserAgent;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.mapper.FileEntryMapper;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.BasicFileAndContents;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.file.service.GOLTemplateRenderer;
import com.worth.ifs.file.transactional.FileService;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.io.File.separator;

@Service
public class ProjectGrantOfferServiceImpl extends BaseTransactionalService implements ProjectGrantOfferService{

    static final String GOL_TEMPLATES_PATH = "grantoffer" + separator + "grant_offer_letter.html";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private GOLTemplateRenderer renderer;

    @Override
    public ServiceResult<FileAndContents> getSignedGrantOfferLetterFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getSignedGrantOfferLetter();

            return getFileAndContentsResult(fileEntry);
        });
    }

    @Override
    public ServiceResult<FileAndContents> getGrantOfferLetterFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getGrantOfferLetter();

            return getFileAndContentsResult(fileEntry);
        });
    }

    private FailingOrSucceedingResult<FileAndContents, ServiceFailure> getFileAndContentsResult(FileEntry fileEntry) {
        if (fileEntry == null) {
            return serviceFailure(notFoundError(FileEntry.class));
        }

        ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(fileEntry.getId());
        return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), inputStream));
    }

    @Override
    public ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getAdditionalContractFile();

            return getFileAndContentsResult(fileEntry);
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getSignedGrantOfferLetter();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });

    }

    @Override
    public ServiceResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getGrantOfferLetter();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });
    }

    @Override
    public ServiceResult<FileEntryResource> getAdditionalContractFileEntryDetails(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {

            FileEntry fileEntry = project.getAdditionalContractFile();

            if (fileEntry == null) {
                return serviceFailure(notFoundError(FileEntry.class));
            }

            return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
        });

    }

    @Override
    public ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true)));

    }

    @Override
    public ServiceResult<FileEntryResource>  createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)));
    }

    @Override
    public ServiceResult<FileEntryResource> generateGrantOfferLetter(Long projectId) {
        Map<String, Object> templateReplacements = new HashMap<>();
        FileEntryResource fileEntryResource = new FileEntryResource(null, "grant_offer_letter", MediaType.valueOf("pdf").getType(), 1024);
        return getProject(projectId).
                andOnSuccess(project -> renderer.renderTemplate(getTemplatePath(), templateReplacements).
                            andOnSuccess(htmlFile -> convertHtmlToPdf(htmlFile, () -> new ByteArrayInputStream(StringUtils.getBytesUtf8(htmlFile))).
                                    andOnSuccess(inputStreamSupplier -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                                            andOnSuccessReturn(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, false)))));
    }

    private ServiceResult<Supplier<InputStream>> convertHtmlToPdf(String htmlFile, Supplier<InputStream> inputStreamSupplier) {
        return null;
    }


    public static Supplier<InputStream> createPDF(String url, Supplier<InputStream> inputStreamSupplier)
            throws IOException, DocumentException {

        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            ITextRenderer renderer = new ITextRenderer();
            ResourceLoaderUserAgent callback = new ResourceLoaderUserAgent(renderer.getOutputDevice());
            callback.setSharedContext(renderer.getSharedContext());
            renderer.getSharedContext ().setUserAgentCallback(callback);

            Document doc = XMLResource.load(new InputSource(inputStreamSupplier.get())).getDocument();

            PdfWriter writer = renderer.getWriter();;
            writer.setPDFXConformance(PdfWriter.PDFA1A);
            renderer.setDocument(doc, url);
            renderer.layout();
            renderer.createPDF(os);

            return () -> new ByteArrayInputStream(os.toByteArray());
        }
    }

    private FileEntryResource linkGrantOfferLetterFileToProject(Project project, Pair<File, FileEntry> fileDetails, boolean signed) {
        FileEntry fileEntry = fileDetails.getValue();

        if (signed) {
            project.setSignedGrantOfferLetter(fileEntry);
        } else {
            project.setGrantOfferLetter(fileEntry);
        }

        return fileEntryMapper.mapToResource(fileEntry);
    }



    @Override
    public ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileDetails -> linkAdditionalContractFileToProject(project, fileDetails)));

    }

    @Override
    public ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return getProject(projectId).
                andOnSuccess(project -> fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturnVoid(fileDetails -> linkGrantOfferLetterFileToProject(project, fileDetails, true)));

    }

    @Override
    public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
        return getProject(projectId).andOnSuccess(project -> {
            if (project.getSignedGrantOfferLetter() == null) {
                return serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT);
            }
            project.setOfferSubmittedDate(LocalDateTime.now());
            return serviceSuccess();
        });
    }

    private FileEntryResource linkAdditionalContractFileToProject(Project project, Pair<File, FileEntry> fileDetails) {
        FileEntry fileEntry = fileDetails.getValue();
        project.setAdditionalContractFile(fileEntry);
        return fileEntryMapper.mapToResource(fileEntry);
    }

    private ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findOne(projectId), notFoundError(Project.class, projectId));
    }

    private String getTemplatePath() {
        return GOL_TEMPLATES_PATH;
    }


}
