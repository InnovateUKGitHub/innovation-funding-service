package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class FileServiceTransactionHelper {

    @Autowired
    private FileEntryRepository fileEntryRepository;

    @Transactional
    public FileEntry persistInitial() {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileUuid(UUID.randomUUID().toString());
        return fileEntryRepository.save(fileEntry);
    }

    @Transactional
    public FileEntry updateExisting(Long id) throws NoSuchElementException {
        FileEntry fileEntry = fileEntryRepository.findById(id).orElseThrow(NoSuchElementException::new);
        fileEntry.setFileUuid(UUID.randomUUID().toString());
        return fileEntryRepository.save(fileEntry);
    }

    @Transactional
    public FileEntry updateResponse(Long id, String md5Checksum, String name, String mediaType, long fileSize) throws NoSuchElementException {
        FileEntry fileEntry = fileEntryRepository.findById(id).orElseThrow(NoSuchElementException::new);
        fileEntry.setMd5Checksum(md5Checksum);
        fileEntry.setName(name);
        fileEntry.setMediaType(mediaType);
        fileEntry.setFilesizeBytes(fileSize);
        return fileEntryRepository.save(fileEntry);
    }

    @Transactional(readOnly = true)
    public FileEntry find(Long id) throws NoSuchElementException {
        return fileEntryRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void delete(long fileEntryId) {
        fileEntryRepository.deleteById(fileEntryId);
    }
}
