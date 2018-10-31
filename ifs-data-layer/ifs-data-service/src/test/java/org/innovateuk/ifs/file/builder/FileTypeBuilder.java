package org.innovateuk.ifs.file.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.domain.ProjectDocument;
import org.innovateuk.ifs.file.domain.FileType;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link FileType}s.
 */
public class FileTypeBuilder extends BaseBuilder<FileType, FileTypeBuilder> {

    public static FileTypeBuilder newFileType() {
        return new FileTypeBuilder(emptyList()).with(uniqueIds());
    }

    private FileTypeBuilder(List<BiConsumer<Integer, FileType>> multiActions) {
        super(multiActions);
    }

    @Override
    protected FileTypeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FileType>> actions) {
        return new FileTypeBuilder(actions);
    }

    @Override
    protected FileType createInitial() {
        return createDefault(FileType.class);
    }

    public FileTypeBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public FileTypeBuilder withName(String... names) {
        return withArray((name, p) -> setField("name", name, p), names);
    }

    public FileTypeBuilder withExtension(String... extensions) {
        return withArray((extension, p) -> setField("extension", extension, p), extensions);
    }
}



