package org.innovateuk.ifs.file.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class FileEntryResourceBuilder extends BaseBuilder<FileEntryResource, FileEntryResourceBuilder> {

    private FileEntryResourceBuilder(List<BiConsumer<Integer, FileEntryResource>> multiActions) {
        super(multiActions);
    }

    public static FileEntryResourceBuilder newFileEntryResource() {
        return new FileEntryResourceBuilder(emptyList()).with(uniqueIds()).withMediaType("text/plain");
    }

    @Override
    protected FileEntryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FileEntryResource>> actions) {
        return new FileEntryResourceBuilder(actions);
    }

    public FileEntryResourceBuilder withMediaType(String mediaType) {
        return with(resource -> resource.setMediaType(mediaType));
    }

    public FileEntryResourceBuilder withName(String name) {
        return with(resource -> resource.setName(name));
    }

    public FileEntryResourceBuilder withId(Long id) {
        return with(resource -> resource.setId(id));
    }


    public FileEntryResourceBuilder withFilesizeBytes(long filesizeBytes) {
        return with(resource -> resource.setFilesizeBytes(filesizeBytes));
    }

    @Override
    protected FileEntryResource createInitial() {
        return new FileEntryResource();
    }
}
