package org.innovateuk.ifs.file.resource;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * An object for holding both file entry details and a supplier to access the file contents
 */
public class BasicFileAndContents implements FileAndContents {

    private FileEntryResource fileEntry;
    private Supplier<InputStream> contentsSupplier;

    public BasicFileAndContents(FileEntryResource fileEntry, Supplier<InputStream> contentsSupplier) {
        this.fileEntry = fileEntry;
        this.contentsSupplier = contentsSupplier;
    }

    @Override
    public FileEntryResource getFileEntry() {
        return fileEntry;
    }

    @Override
    public Supplier<InputStream> getContentsSupplier() {
        return contentsSupplier;
    }
}
