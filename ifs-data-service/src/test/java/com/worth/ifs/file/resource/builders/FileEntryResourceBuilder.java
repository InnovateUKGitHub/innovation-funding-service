package com.worth.ifs.file.resource.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.resource.FileEntryResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FileEntryResourceBuilder extends BaseBuilder<FileEntryResource, FileEntryResourceBuilder> {

    private FileEntryResourceBuilder(List<BiConsumer<Integer, FileEntryResource>> multiActions) {
        super(multiActions);
    }

    public static FileEntryResourceBuilder newFileEntryResource() {
        return new FileEntryResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FileEntryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FileEntryResource>> actions) {
        return new FileEntryResourceBuilder(actions);
    }

    @Override
    protected FileEntryResource createInitial() {
        return new FileEntryResource();
    }
}