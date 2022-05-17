package org.innovateuk.ifs.file.resource;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * An interface for an object that contains file details and a supplier for content
 */
public interface FileAndContents {

    FileEntryResource getFileEntry();

    Supplier<InputStream> getContentsSupplier();
}
