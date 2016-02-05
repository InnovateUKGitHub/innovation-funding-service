package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.security.NotSecured;

public interface FileEntryService {
    @NotSecured("TODO")
    FileEntry findOne(Long id);
}