package org.innovateuk.ifs.finance.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.ApplicationFinanceFileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;


@Service
public class FinanceFileEntryServiceImpl extends BaseTransactionalService implements FinanceFileEntryService {

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ApplicationFinanceFileEntryService fileEntryService;

    @Autowired
    private FileEntryMapper fileEntryMapper;

    @Autowired
    private FinanceRowCostsService financeRowCostsService;

    @Autowired
    private ApplicationFinanceMapper applicationFinanceMapper;

    @Autowired
    private FinanceService financeService;

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> createFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findById(applicationFinanceId).get();
        return getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(app ->
                fileService.createFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileResults -> linkFileEntryToApplicationFinance(applicationFinance, fileResults))
        );
    }

    @Override
    @Transactional
    public ServiceResult<FileEntryResource> updateFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findById(applicationFinanceId).get();
        return getOpenApplication(applicationFinance.getApplication().getId()).andOnSuccess(app ->
                fileService.updateFile(fileEntryResource, inputStreamSupplier).
                        andOnSuccessReturn(fileResults -> linkFileEntryToApplicationFinance(applicationFinance, fileResults))
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteFinanceFileEntry(long applicationFinanceId) {
        Application application = applicationFinanceRepository.findById(applicationFinanceId).get().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app ->
                financeService.getApplicationFinanceById(applicationFinanceId).
                        andOnSuccess(finance -> fileService.deleteFileIgnoreNotFound(finance.getFinanceFileEntry()).
                                andOnSuccess(() -> removeFileEntryFromApplicationFinance(finance))).
                        andOnSuccessReturnVoid()
        );
    }

    @Override
    public ServiceResult<FileAndContents> getFileContents(long applicationFinanceId) {
        return fileEntryService.getFileEntryByApplicationFinanceId(applicationFinanceId)
                .andOnSuccess(fileEntry -> fileService.getFileByFileEntryId(fileEntry.getId())
                        .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream)));
    }

    private ServiceResult<ApplicationFinanceResource> removeFileEntryFromApplicationFinance(ApplicationFinanceResource applicationFinanceResource) {
        Application application = applicationFinanceRepository.findById(applicationFinanceResource.getId()).get().getApplication();
        return getOpenApplication(application.getId()).andOnSuccess(app -> {
            applicationFinanceResource.setFinanceFileEntry(null);
            return financeRowCostsService.updateApplicationFinance(applicationFinanceResource.getId(), applicationFinanceResource);
        });
    }

    private FileEntryResource linkFileEntryToApplicationFinance(ApplicationFinance applicationFinance, Pair<File, FileEntry> fileResults) {
        FileEntry fileEntry = fileResults.getValue();

        ApplicationFinanceResource applicationFinanceResource = applicationFinanceMapper.mapToResource(applicationFinance);

        if (applicationFinanceResource != null) {
            applicationFinanceResource.setFinanceFileEntry(fileEntry.getId());
            financeRowCostsService.updateApplicationFinance(applicationFinanceResource.getId(), applicationFinanceResource);
        }

        return fileEntryMapper.mapToResource(fileEntry);
    }
}
