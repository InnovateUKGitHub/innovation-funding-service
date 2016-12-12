package org.innovateuk.ifs.file.resource;

import org.innovateuk.ifs.file.domain.FileEntry;

/**
 * Convert between a FileEntry entity and a FileEntryResource.  This is a standin for a true Hateoas resource assembler
 */
public final class FileEntryResourceAssembler {

	private FileEntryResourceAssembler(){}
	
    public static FileEntry valueOf(FileEntryResource resource) {
        return new FileEntry(resource.getId(), resource.getName(), resource.getMediaType(), resource.getFilesizeBytes());
    }

    public static FileEntryResource valueOf(FileEntry resource) {
        return new FileEntryResource(resource.getId(), resource.getName(), resource.getMediaType(), resource.getFilesizeBytes());
    }
}
