package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import org.junit.Test;

import java.io.IOException;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
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

    @Override
    protected BaseFileStorageStrategy createFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        return new FlatFolderFileStorageStrategy(pathToStorageBase, containingFolder);
    }
}