package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for handling the overhead calculation file upload & linking it to the correct over
 */
@Service
public class OverheadFileServiceImpl extends BaseTransactionalService implements OverheadFileService {

    private static String fileMetaFieldType = "file_entry";

    @Autowired
    ApplicationFinanceRowRepository financeRowRepository;

    @Autowired
    FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    public ServiceResult<FileEntryResource> createFileEntry(@P("overheadId") long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        BasicFileAndContents fileAndContents = new BasicFileAndContents(fileEntryResource, inputStreamSupplier);

        return findFinanceRow(overheadId).
                andOnSuccess(overheadFinanceRow -> createOrUpdateFileByMetaField(overheadFinanceRow, fileAndContents));
    }

    public ServiceResult<FileAndContents> getFileEntryContents(long overheadId) {
        return findMetaValueByFinanceRow(overheadId).andOnSuccess(metaValue ->
                find(fileEntryRepository.findOne(Long.valueOf(metaValue.getValue())), notFoundError(FileEntry.class, metaValue.getValue())).andOnSuccess(fileEntry ->
                        fileService.getFileByFileEntryId(fileEntry.getId()).andOnSuccessReturn(fileContentsResult ->
                                new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), fileContentsResult))));
    }

    public ServiceResult<FileEntryResource> getFileEntryDetails(long overheadId) {
        return findMetaValueByFinanceRow(overheadId).andOnSuccess(metaValue ->
                find(fileEntryRepository.findOne(Long.valueOf(metaValue.getValue())), notFoundError(FileEntry.class, metaValue.getValue())).andOnSuccess(fileEntry ->
                        serviceSuccess(fileEntryMapper.mapToResource(fileEntry))));
    }

    public ServiceResult<FileEntryResource> updateFileEntry(long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return createFileEntry(overheadId, fileEntryResource, inputStreamSupplier);
    }

    public ServiceResult<Void> deleteFileEntry(long overheadId) {
        return findMetaValueByFinanceRow(overheadId).andOnSuccess(metaValue -> deleteMetaValueAndFileByMetaValue(metaValue));
    }

    private ServiceResult<Void> deleteMetaValueAndFileByMetaValue(FinanceRowMetaValue metaValue) {
        return fileService.deleteFile(Long.valueOf(metaValue.getValue())).
                andOnSuccessReturnVoid(() -> financeRowMetaValueRepository.delete(metaValue.getId()));
    }
    private ServiceResult<FileEntryResource> createOrUpdateFileByMetaField(FinanceRow overheadFinanceRow, BasicFileAndContents fileAndContents) {
        return findMetaRowField().
                andOnSuccess(metaField -> findMetaRowValue(overheadFinanceRow, metaField).
                        andOnSuccess(
                                metaValue -> updateFile(metaValue, fileAndContents)).
                        andOnFailure(
                        () -> createFileAndMetaValue(overheadFinanceRow, metaField, fileAndContents)));
    }

    private ServiceResult<FileEntryResource> updateFile(FinanceRowMetaValue financeRowMetaValue, BasicFileAndContents fileAndContents) {
        FileEntryResource fileEntryResource = fileAndContents.getFileEntry();
        fileEntryResource.setId(Long.valueOf(financeRowMetaValue.getValue()));

        return fileService.updateFile(fileEntryResource, fileAndContents.getContentsSupplier()).
                andOnSuccess(updatedFileEntry -> serviceSuccess(fileEntryMapper.mapToResource(updatedFileEntry.getValue())));
    }

    private ServiceResult<FileEntryResource> createFileAndMetaValue(FinanceRow overheadFinanceRow, FinanceRowMetaField metaField, BasicFileAndContents fileAndContents) {
        return fileService.createFile(fileAndContents.getFileEntry(), fileAndContents.getContentsSupplier()).
                andOnSuccess(fileEntry -> {
                    financeRowMetaValueRepository.save(
                            new FinanceRowMetaValue(overheadFinanceRow, metaField, fileEntry.getValue().getId().toString())
                    );
                    return serviceSuccess(fileEntryMapper.mapToResource(fileEntry.getValue()));
                });
    }

    private ServiceResult<FinanceRowMetaValue> findMetaValueByFinanceRow(long overheadId) {
        return findFinanceRow(overheadId).andOnSuccess(overheadFinanceRow ->
                findMetaRowField().andOnSuccess(metaField ->
                        findMetaRowValue(overheadFinanceRow, metaField)));
    }

    private ServiceResult<FinanceRow> findFinanceRow(long overheadId) {
        return find(financeRowRepository.findById(overheadId), notFoundError(FinanceRow.class, overheadId));
    }

    private ServiceResult<FinanceRowMetaValue> findMetaRowValue(FinanceRow overheadFinanceRow, FinanceRowMetaField metaField) {
        return find(financeRowMetaValueRepository.financeRowIdAndFinanceRowMetaFieldId(overheadFinanceRow.getId(), metaField.getId()), notFoundError(FinanceRow.class, overheadFinanceRow.getId()));
    }

    private ServiceResult<FinanceRowMetaField> findMetaRowField() {
        return find(financeRowMetaFieldRepository.findByTitle(fileMetaFieldType), notFoundError(FinanceRow.class, fileMetaFieldType));
    }
}
