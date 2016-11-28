package com.worth.ifs.file.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.resource.FileEntryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

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

    public FileEntryResourceBuilder withFilesizeBytes(long filesizeBytes) {
        return with(resource -> resource.setFilesizeBytes(filesizeBytes));
    }

    @Override
    protected FileEntryResource createInitial() {
        return new FileEntryResource();
    }
}