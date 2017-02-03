package org.innovateuk.ifs.publiccontent.transactional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.io.InputStream;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupBuilder.newContentGroup;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContentGroupServiceImplTest extends BaseServiceUnitTest<ContentGroupServiceImpl> {

    @Override
    protected ContentGroupServiceImpl supplyServiceUnderTest() {
        return new ContentGroupServiceImpl();
    }

    @Mock
    private ContentGroupRepository contentGroupRepository;

    @Test
    public void testUploadFile() {
        long contentGroupId = 1L;
        FileEntryResource fileEntryResource = mock(FileEntryResource.class);
        Supplier<InputStream> inputStreamSupplier = mock(Supplier.class);
        FileEntry fileEntry = mock(FileEntry.class);
        ContentGroup group = mock(ContentGroup.class);

        when(fileServiceMock.createFile(fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(new ImmutablePair<>(null, fileEntry)));
        when(contentGroupRepository.findOne(contentGroupId)).thenReturn(group);

        service.uploadFile(contentGroupId, fileEntryResource, inputStreamSupplier);


        verify(group).setFileEntry(fileEntry);
        verify(fileServiceMock).createFile(fileEntryResource, inputStreamSupplier);
    }

    @Test
    public void testRemoveFile() {
        long contentGroupId = 1L;
        FileEntry fileEntry = mock(FileEntry.class);
        ContentGroup group = mock(ContentGroup.class);
        long fileEntryId = 2L;

        when(contentGroupRepository.findOne(contentGroupId)).thenReturn(group);
        when(group.getFileEntry()).thenReturn(fileEntry);
        when(fileEntry.getId()).thenReturn(fileEntryId);
        when(fileServiceMock.deleteFile(fileEntryId)).thenReturn(serviceSuccess(fileEntry));

        service.removeFile(contentGroupId);

        verify(fileServiceMock).deleteFile(fileEntryId);
    }

    @Test
    public void testGetFileDetails() {
        long contentGroupId = 1L;
        FileEntry fileEntry = mock(FileEntry.class);
        FileEntryResource fileEntryResource = mock(FileEntryResource.class);
        ContentGroup group = mock(ContentGroup.class);

        when(contentGroupRepository.findOne(contentGroupId)).thenReturn(group);
        when(group.getFileEntry()).thenReturn(fileEntry);
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(fileEntryResource);

        ServiceResult<FileEntryResource> result = service.getFileDetails(contentGroupId);

        assertThat(result.getSuccessObjectOrThrowException(), equalTo(fileEntryResource));
    }

    @Test
    public void testGetFileContents() {
        long contentGroupId = 1L;
        long fileEntryId = 2L;
        FileEntry fileEntry = mock(FileEntry.class);
        FileEntryResource fileEntryResource = mock(FileEntryResource.class);
        ContentGroup group = mock(ContentGroup.class);
        Supplier<InputStream> inputStreamSupplier = mock(Supplier.class);

        when(contentGroupRepository.findOne(contentGroupId)).thenReturn(group);
        when(group.getFileEntry()).thenReturn(fileEntry);
        when(fileEntryMapperMock.mapToResource(fileEntry)).thenReturn(fileEntryResource);
        when(fileEntry.getId()).thenReturn(fileEntryId);
        when(fileServiceMock.getFileByFileEntryId(fileEntryId)).thenReturn(serviceSuccess(inputStreamSupplier));

        ServiceResult<FileAndContents> result = service.getFileContents(contentGroupId);

        assertThat(result.getSuccessObjectOrThrowException().getFileEntry(), equalTo(fileEntryResource));
        assertThat(result.getSuccessObjectOrThrowException().getContentsSupplier(), equalTo(inputStreamSupplier));
    }

    @Test
    public void testSaveContentGroups() {
        ContentGroupResource newContentGroupResource = newContentGroupResource().withId(null).withContent("NewContent").withHeading("NewHeading").build();
        ContentGroupResource toUpdateContentGroupResource = newContentGroupResource().withContent("UpdateContent").withHeading("UpdateHeading").build();

        ContentGroup toUpdateContentGroup = newContentGroup().withHeading("OldHeading").withContent("OldContent").build();
        toUpdateContentGroup.setId(toUpdateContentGroupResource.getId());

        FileEntry toDelete = mock(FileEntry.class);
        long fileEntryId = 2L;
        when(toDelete.getId()).thenReturn(fileEntryId);
        ContentGroup toDeleteContentGroup = newContentGroup().withFileEntry(toDelete).build();

        PublicContentSectionType type = PublicContentSectionType.SCOPE;

        PublicContentResource publicContentResource = newPublicContentResource()
                .withContentSections(
                        newPublicContentSectionResource().withType(type).withContentGroups(
                                asList(newContentGroupResource, toUpdateContentGroupResource)
                        ).build(1)).build();

        PublicContent publicContent = newPublicContent()
                .withContentSections(
                        newContentSection().withType(type).withContentGroups(
                                asList(toUpdateContentGroup, toDeleteContentGroup)
                        ).build(1)).build();

        when(contentGroupRepository.findOne(toDeleteContentGroup.getId())).thenReturn(toDeleteContentGroup);
        when(fileServiceMock.deleteFile(fileEntryId)).thenReturn(serviceSuccess(toDelete));

        service.saveContentGroups(publicContentResource, publicContent, type).getSuccessObjectOrThrowException();

        verify(fileServiceMock).deleteFile(fileEntryId);
        verify(contentGroupRepository).delete(toDeleteContentGroup.getId());
        verify(contentGroupRepository).save(contentGroupMatcher(newContentGroupResource.getHeading(), newContentGroupResource.getContent()));

        assertThat(toUpdateContentGroup.getContent(), equalTo(toUpdateContentGroupResource.getContent()));
        assertThat(toUpdateContentGroup.getHeading(), equalTo(toUpdateContentGroupResource.getHeading()));
    }


    private static ContentGroup contentGroupMatcher(String heading, String content) {
        return createLambdaMatcher(entity -> entity.getContent().equals(content) && entity.getHeading().equals(heading));
    }

}
