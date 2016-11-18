package com.worth.ifs;


import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.InjectMocks;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static java.io.File.separator;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * This is the base class for testing Services with mock components.
 *
 */
public abstract class BaseServiceUnitTest<ServiceType> extends BaseUnitTestMocksTest {

    static final String GOL_TEMPLATES_PATH = "grantoffer" + separator + "grant_offer_letter.html";

    @InjectMocks
    protected ServiceType service = supplyServiceUnderTest();

    protected abstract ServiceType supplyServiceUnderTest();

    protected void assertGetFileContents(Consumer<FileEntry> fileSetter, Supplier<ServiceResult<FileAndContents>> getFileContentsFn) {

        FileEntry fileToGet = newFileEntry().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntryResource fileResourceToGet = newFileEntryResource().build();

        fileSetter.accept(fileToGet);
        when(fileServiceMock.getFileByFileEntryId(fileToGet.getId())).thenReturn(serviceSuccess(inputStreamSupplier));
        when(fileEntryMapperMock.mapToResource(fileToGet)).thenReturn(fileResourceToGet);

        ServiceResult<FileAndContents> result = getFileContentsFn.get();
        assertTrue(result.isSuccess());
        assertEquals(fileResourceToGet, result.getSuccessObject().getFileEntry());
        assertEquals(inputStreamSupplier, result.getSuccessObject().getContentsSupplier());
    }

    protected void assertCreateFile(Supplier<FileEntry> fileGetter, BiFunction<FileEntryResource, Supplier<InputStream>, ServiceResult<FileEntryResource>> createFileFn) {

        FileEntryResource fileToCreate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry createdFile = newFileEntry().build();
        FileEntryResource createdFileResource = newFileEntryResource().build();

        when(fileServiceMock.createFile(fileToCreate, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(new File("blah"), createdFile)));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(createdFileResource);

        ServiceResult<FileEntryResource> result = createFileFn.apply(fileToCreate, inputStreamSupplier);
        assertTrue(result.isSuccess());
        assertEquals(createdFileResource, result.getSuccessObject());
        assertEquals(createdFile, fileGetter.get());
    }

    protected void assertGenerateFile(Function<FileEntryResource, ServiceResult<FileEntryResource>> generateFileFn) {

        FileEntryResource fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        FileEntry createdFile = newFileEntry().build();
        Pair<File, FileEntry> fileEntryPair = Pair.of(new File("blah"), createdFile);

        Map<String, Object> templateReplacements = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder();
        String htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        when(rendererMock.renderTemplate(GOL_TEMPLATES_PATH, templateReplacements)).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);

        ServiceResult<FileEntryResource> result = generateFileFn.apply(fileEntryResource);
        assertTrue(result.isSuccess());
        assertEquals(fileEntryResource, result.getSuccessObject());
        assertEquals(result.getSuccessObject().getName(), "grant_offer_letter");
    }
    protected void assertGenerateFileFails(Function<FileEntryResource, ServiceResult<FileEntryResource>> generateFileFn) {

        FileEntryResource fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        FileEntry createdFile = newFileEntry().build();
        Pair<File, FileEntry> fileEntryPair = Pair.of(new File("blah"), createdFile);

        Map<String, Object> templateReplacements = new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder();
        String htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        when(rendererMock.renderTemplate(GOL_TEMPLATES_PATH, templateReplacements)).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);

        ServiceResult<FileEntryResource> result = generateFileFn.apply(fileEntryResource);
        assertFalse(result.isSuccess());
        assertEquals(fileEntryResource, result.getSuccessObject());
        assertEquals(result.getSuccessObject().getName(), "grant_offer_letter");
    }


    protected void assertGetFileDetails(Consumer<FileEntry> fileSetter, Supplier<ServiceResult<FileEntryResource>> getFileDetailsFn) {
        FileEntry fileToGet = newFileEntry().build();

        FileEntryResource fileResourceToGet = newFileEntryResource().build();

        fileSetter.accept(fileToGet);
        when(fileEntryMapperMock.mapToResource(fileToGet)).thenReturn(fileResourceToGet);

        ServiceResult<FileEntryResource> result = getFileDetailsFn.get();
        assertTrue(result.isSuccess());
        assertEquals(fileResourceToGet, result.getSuccessObject());
    }

    protected void assertUpdateFile(Supplier<FileEntry> fileGetter, BiFunction<FileEntryResource, Supplier<InputStream>, ServiceResult<Void>> updateFileFn) {
        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        FileEntry updatedFile = newFileEntry().build();
        FileEntryResource updatedFileResource = newFileEntryResource().build();

        when(fileServiceMock.updateFile(fileToUpdate, inputStreamSupplier)).thenReturn(serviceSuccess(Pair.of(new File("blah"), updatedFile)));
        when(fileEntryMapperMock.mapToResource(updatedFile)).thenReturn(updatedFileResource);

        ServiceResult<Void> result = updateFileFn.apply(fileToUpdate, inputStreamSupplier);
        assertTrue(result.isSuccess());
        assertEquals(updatedFile, fileGetter.get());

        verify(fileServiceMock).updateFile(fileToUpdate, inputStreamSupplier);
    }


    protected void setLoggedInUser(UserResource loggedInUser) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(loggedInUser));
    }

}