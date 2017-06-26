package org.innovateuk.ifs.file.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.file.domain.FileEntry;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class FileEntryBuilder extends BaseBuilder<FileEntry, FileEntryBuilder> {

    private FileEntryBuilder(List<BiConsumer<Integer, FileEntry>> multiActions) {
        super(multiActions);
    }

    public static FileEntryBuilder newFileEntry() {
        return new FileEntryBuilder(emptyList()).with(BaseBuilderAmendFunctions.uniqueIds()).withMediaType("text/plain");
    }

    @Override
    protected FileEntryBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FileEntry>> actions) {
        return new FileEntryBuilder(actions);
    }

    public FileEntryBuilder withMediaType(String mediaType) {
        return with(file -> file.setMediaType(mediaType));
    }

    public FileEntryBuilder withFilesizeBytes(long filesizeBytes) {
        return with(resource -> resource.setFilesizeBytes(filesizeBytes));
    }

    @Override
    protected FileEntry createInitial() {
        return new FileEntry();
    }
}
