package org.innovateuk.ifs.publiccontent.transactional;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of public content.
 */
@Service
public class ContentGroupServiceImpl extends BaseTransactionalService implements ContentGroupService {

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private ContentGroupRepository contentGroupRepository;

    @Override
    @Transactional
    public ServiceResult<Void> uploadFile(long contentGroupId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        BasicFileAndContents fileAndContents = new BasicFileAndContents(fileEntryResource, inputStreamSupplier);

        return fileService.createFile(fileAndContents.getFileEntry(), fileAndContents.getContentsSupplier())
                .andOnSuccess(fileEntry -> find(contentGroupRepository.findOne(contentGroupId), notFoundError(ContentGroup.class, contentGroupId))
                        .andOnSuccessReturnVoid(contentGroup -> contentGroup.setFileEntry(fileEntry.getRight())));
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeFile(Long contentGroupId) {
        return find(contentGroupRepository.findOne(contentGroupId), notFoundError(ContentGroup.class, contentGroupId))
                .andOnSuccess(contentGroup -> {
                    FileEntry fileEntry = contentGroup.getFileEntry();
                    if (fileEntry != null) {
                        contentGroup.setFileEntry(null);
                        return fileService.deleteFile(fileEntry.getId()).andOnSuccessReturnVoid();
                    } else {
                        return serviceSuccess();
                    }
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> saveContentGroups(PublicContentResource resource, PublicContent publicContent, PublicContentSectionType sectionType) {
        Optional<PublicContentSectionResource> optionalResource = CollectionFunctions.simpleFindFirst(resource.getContentSections(),
                contentSectionResource -> sectionType.equals(contentSectionResource.getType()));

        Optional<ContentSection> optionalSection =  CollectionFunctions.simpleFindFirst(publicContent.getContentSections(),
                section -> sectionType.equals(section.getType()));

        if (optionalResource.isPresent() && optionalSection.isPresent()) {
            return saveContentGroups(optionalResource.get(), optionalSection.get());
        } else {
            return serviceFailure(CommonFailureKeys.PUBLIC_CONTENT_NOT_INITIALISED);
        }
    }

    @Override
    public ServiceResult<FileEntryResource> getFileDetails(long contentGroupId) {
        return find(contentGroupRepository.findOne(contentGroupId), notFoundError(ContentGroup.class, contentGroupId))
                .andOnSuccessReturn(contentGroup -> fileEntryMapper.mapToResource(contentGroup.getFileEntry()));
    }

    @Override
    public ServiceResult<FileAndContents> getFileContents(long contentGroupId) {

        return find(contentGroupRepository.findOne(contentGroupId), notFoundError(ContentGroup.class, contentGroupId))
                .andOnSuccess(contentGroup -> {
                    ServiceResult<Supplier<InputStream>> getFileResult = fileService.getFileByFileEntryId(contentGroup.getFileEntry().getId());
                    return getFileResult.andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntryMapper.mapToResource(contentGroup.getFileEntry()), inputStream));
                });
    }

    private ServiceResult<Void> saveContentGroups(PublicContentSectionResource sectionResource, ContentSection section) {

        Set<ContentGroupResource> toAdd = sectionResource.getContentGroups().stream()
                .filter(group -> null == group.getId()).collect(Collectors.toSet());

        Set<ContentGroupResource> toUpdate = Sets.difference(new HashSet<>(sectionResource.getContentGroups()), toAdd);

        Set<Long> resourceIds = sectionResource.getContentGroups().stream().filter(contentGroupResource -> contentGroupResource != null).map(ContentGroupResource::getId).collect(Collectors.toSet());
        Set<Long> entityIds = section.getContentGroups().stream().map(ContentGroup::getId).collect(Collectors.toSet());
        Set<Long> toDeleteIds = Sets.difference(entityIds, resourceIds);

        addNewGroups(toAdd, section);
        updateGroups(toUpdate, section);
        return deleteGroups(toDeleteIds);
    }

    private ServiceResult<Void> deleteGroups(Set<Long> toDeleteIds) {
        ServiceResult<Void> result = serviceSuccess();
        for (Long deleteId : toDeleteIds) {
            result = result.andOnSuccessReturnVoid(() -> {
                removeFile(deleteId);
                contentGroupRepository.delete(deleteId);
            });
        }
        return result;
    }

    private void updateGroups(Set<ContentGroupResource> toUpdate, ContentSection contentSection) {
        toUpdate.forEach(contentGroupResource -> {
            Optional<ContentGroup> optional = CollectionFunctions.simpleFindFirst(contentSection.getContentGroups(),
                    contentGroup -> contentGroupResource.getId().equals(contentGroup.getId()));
            if (optional.isPresent()) {
                optional.get().setContent(contentGroupResource.getContent());
                optional.get().setHeading(contentGroupResource.getHeading());
                optional.get().setPriority(contentGroupResource.getPriority());
            }
        });
    }

    private void addNewGroups(Set<ContentGroupResource> toAdd, ContentSection contentSection) {
        toAdd.forEach(contentGroupResource -> {
            ContentGroup contentGroup = new ContentGroup();
            contentGroup.setHeading(contentGroupResource.getHeading());
            contentGroup.setContent(contentGroupResource.getContent());
            contentGroup.setPriority(contentGroupResource.getPriority());
            contentGroup.setContentSection(contentSection);
            contentGroupRepository.save(contentGroup);
        });
    }
}
