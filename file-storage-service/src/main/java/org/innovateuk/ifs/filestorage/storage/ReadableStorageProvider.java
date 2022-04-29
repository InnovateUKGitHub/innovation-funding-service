package org.innovateuk.ifs.filestorage.storage;

import java.io.IOException;
import java.util.Optional;

public interface ReadableStorageProvider {

    Optional<byte[]> readFile(String uuid) throws IOException;

    boolean fileExists(String uuid) throws IOException;

}
