package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.BasicFileAndContents;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileAndContents;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ApplicationFinanceRowRepository financeRowRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Autowired
    private FinanceRowMetaValueRepository financeRowMetaValueRepository;

    @Autowired
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepository;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createFileEntry(@P("overheadId") long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        BasicFileAndContents fileAndContents = new BasicFileAndContents(fileEntryResource, inputStreamSupplier);

        return findFinanceRow(overheadId).
                andOnSuccess(overheadFinanceRow -> createOrUpdateFileByMetaField(overheadFinanceRow, fileAndContents));
    }

    @Override
    public ServiceResult<FileAndContents> getFileEntryContents(long overheadId) {
        return getApplicationFileEntryContents(overheadId);
    }

    @Override
    public ServiceResult<FileAndContents> getProjectFileEntryContents(long overheadId) {
        ProjectFinanceRow financeRow = projectFinanceRowRepository.findById(overheadId).get();
        return getApplicationFileEntryContents(financeRow.getApplicationRowId());
    }

    private ServiceResult<FileAndContents> getApplicationFileEntryContents(long overheadId){
        return findMetaValueByFinanceRow(overheadId).andOnSuccess(metaValue ->
                find(fileEntryRepository.findById(Long.valueOf(metaValue.getValue())), notFoundError(FileEntry.class, metaValue.getValue())).andOnSuccess(fileEntry ->
                        fileService.getFileByFileEntryId(fileEntry.getId()).andOnSuccessReturn(fileContentsResult ->
                                new BasicFileAndContents(fileEntryMapper.mapToResource(fileEntry), fileContentsResult))));
    }

    @Override
    public ServiceResult<FileEntryResource> getFileEntryDetails(long overheadId) {
        return getApplicationFileEntryDetails(overheadId);
    }

    @Override
    public ServiceResult<FileEntryResource> getProjectFileEntryDetails(long overheadId) {
        ProjectFinanceRow financeRow = projectFinanceRowRepository.findById(overheadId).get();
        return getApplicationFileEntryDetails(financeRow.getApplicationRowId());
    }

    private ServiceResult<FileEntryResource> getApplicationFileEntryDetails(long overheadId){
        return findMetaValueByFinanceRow(overheadId).andOnSuccess(metaValue ->
                find(fileEntryRepository.findById(Long.valueOf(metaValue.getValue())), notFoundError(FileEntry.class, metaValue.getValue())).andOnSuccess(fileEntry ->
                        serviceSuccess(fileEntryMapper.mapToResource(fileEntry))));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> updateFileEntry(long overheadId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        return createFileEntry(overheadId, fileEntryResource, inputStreamSupplier);
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteFileEntry(long overheadId) {
        return findMetaValueByFinanceRow(overheadId).andOnSuccess(this::deleteMetaValueAndFileByMetaValue);
    }

    private ServiceResult<Void> deleteMetaValueAndFileByMetaValue(FinanceRowMetaValue metaValue) {
        return fileService.deleteFileIgnoreNotFound(Long.valueOf(metaValue.getValue())).
                andOnSuccessReturnVoid(() -> financeRowMetaValueRepository.deleteById(metaValue.getId()));
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
                andOnSuccess(updatedFileEntry -> serviceSuccess(fileEntryMapper.mapToResource(updatedFileEntry)));
    }

    private ServiceResult<FileEntryResource> createFileAndMetaValue(FinanceRow overheadFinanceRow, FinanceRowMetaField metaField, BasicFileAndContents fileAndContents) {
        return fileService.createFile(fileAndContents.getFileEntry(), fileAndContents.getContentsSupplier()).
                andOnSuccess(fileEntry -> {
                    financeRowMetaValueRepository.save(
                            new FinanceRowMetaValue(overheadFinanceRow, metaField, fileEntry.getId().toString())
                    );
                    return serviceSuccess(fileEntryMapper.mapToResource(fileEntry));
                });
    }

    private ServiceResult<FinanceRowMetaValue> findMetaValueByFinanceRow(long overheadId) {
        return findFinanceRow(overheadId).andOnSuccess(overheadFinanceRow ->
                findMetaRowField().andOnSuccess(metaField ->
                        findMetaRowValue(overheadFinanceRow, metaField)));
    }

    private ServiceResult<FinanceRow> findFinanceRow(long overheadId) {
        return find(financeRowRepository.findById(overheadId).orElse(null), notFoundError(FinanceRow.class, overheadId));
    }

    private ServiceResult<FinanceRowMetaValue> findMetaRowValue(FinanceRow overheadFinanceRow, FinanceRowMetaField metaField) {
        return find(financeRowMetaValueRepository.financeRowIdAndFinanceRowMetaFieldId(overheadFinanceRow.getId(), metaField.getId()), notFoundError(FinanceRow.class, overheadFinanceRow.getId()));
    }

    private ServiceResult<FinanceRowMetaField> findMetaRowField() {
        return find(financeRowMetaFieldRepository.findByTitle(fileMetaFieldType), notFoundError(FinanceRow.class, fileMetaFieldType));
    }
}
