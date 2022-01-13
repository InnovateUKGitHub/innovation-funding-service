package org.innovateuk.ifs.file.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.file.resource.FileTypeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class FileTypeResourceBuilder extends BaseBuilder<FileTypeResource, FileTypeResourceBuilder> {

    private FileTypeResourceBuilder(List<BiConsumer<Integer, FileTypeResource>> multiActions) {
        super(multiActions);
    }

    public static FileTypeResourceBuilder newFileTypeResource() {
        return new FileTypeResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FileTypeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FileTypeResource>> actions) {
        return new FileTypeResourceBuilder(actions);
    }

    @Override
    protected FileTypeResource createInitial() {
        return new FileTypeResource();
    }

    public FileTypeResourceBuilder withId(Long... ids) {
        return withArray((id, fileTypeResource) -> setField("id", id, fileTypeResource), ids);
    }

    public FileTypeResourceBuilder withName(String... names) {
        return withArray((name, fileTypeResource) -> setField("name", name, fileTypeResource), names);
    }

    public FileTypeResourceBuilder withExtension(String... extensions) {
        return withArray((extension, fileTypeResource) -> setField("extension", extension, fileTypeResource), extensions);
    }
}


