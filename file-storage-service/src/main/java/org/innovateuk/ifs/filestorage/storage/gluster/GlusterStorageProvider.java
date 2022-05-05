package org.innovateuk.ifs.filestorage.storage.gluster;

import org.innovateuk.ifs.filestorage.storage.ReadableStorageProvider;

import java.io.IOException;
import java.util.Optional;

public class GlusterStorageProvider implements ReadableStorageProvider {

    @Override
    public Optional<byte[]> readFile(String uuid) throws IOException {
        return Optional.empty();
    }

    @Override
    public boolean fileExists(String uuid) {
        return false;
    }
}
