package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.repository.FileEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileEntryServiceImpl implements FileEntryService {
    @Autowired
    private FileEntryRepository repository;

    @Override
    public FileEntry findOne(Long id) {
        return repository.findOne(id);
    }
}