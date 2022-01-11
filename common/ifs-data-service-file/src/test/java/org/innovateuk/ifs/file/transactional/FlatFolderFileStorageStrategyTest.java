package org.innovateuk.ifs.file.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertEquals;

/**
 * Test the storage strategy of ByFileIdFileStorageStrategy
 */
public class FlatFolderFileStorageStrategyTest extends BaseFileStorageStrategyTest {

    @Test
    public void testGetAbsoluteFilePathAndName() {

        FlatFolderFileStorageStrategy strategy = new FlatFolderFileStorageStrategy(tempFolderPathAsString, "BaseFolder");

        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        assertEquals(tempFolderPathSegmentsWithBaseFolder, strategy.getAbsoluteFilePathAndName(fileEntry).getKey());
        assertEquals("123", strategy.getAbsoluteFilePathAndName(fileEntry).getValue());
    }

    @Test
    public void testCreateFile() throws IOException {
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        doTestCreateFile(fileEntry, combineLists(tempFolderPathSegmentsWithBaseFolder, "123"));
    }

    @Test
    public void testMoveFile() throws IOException {
        FileEntry fileEntry = newFileEntry().with(id(123L)).build();
        doTestMoveFile(fileEntry, combineLists(tempFolderPathSegmentsWithBaseFolder, "123"));
    }

    @Test
    public void testGetAll() throws IOException {
        List<Pair<FileEntry, Pair<List<String>, String>>> fileEntriesAndExpectedPaths = new ArrayList<>();
        fileEntriesAndExpectedPaths.add(Pair.of(newFileEntry().with(id(123L)).build(), Pair.of(tempFolderPathSegmentsWithBaseFolder, "123")));
        fileEntriesAndExpectedPaths.add(Pair.of(newFileEntry().with(id(124L)).build(), Pair.of(tempFolderPathSegmentsWithBaseFolder, "124")));
        fileEntriesAndExpectedPaths.add(Pair.of(newFileEntry().with(id(125L)).build(), Pair.of(tempFolderPathSegmentsWithBaseFolder, "125")));
        doTestGetAll(fileEntriesAndExpectedPaths);
    }

    @Test
    public void testGetAllNoFolders() throws IOException {
        List<Pair<FileEntry, Pair<List<String>, String>>> fileEntriesAndExpectedPaths = new ArrayList<>();
        doTestGetAll(fileEntriesAndExpectedPaths);
    }

    @Override
    protected BaseFileStorageStrategy createFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        return new FlatFolderFileStorageStrategy(pathToStorageBase, containingFolder);
    }


}
