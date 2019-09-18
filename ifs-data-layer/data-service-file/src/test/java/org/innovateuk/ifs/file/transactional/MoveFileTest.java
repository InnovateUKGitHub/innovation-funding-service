package org.innovateuk.ifs.file.transactional;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.reverse;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.file.util.FileFunctions.pathElementsToFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test the moving of lots of files
 */
@Ignore("INFUND-7743 - ignoring for now to investigate Bamboo test failures on a different branch than development")
public class MoveFileTest {

    private File tempFolder;
    private String rootFolder = "/tmp/path/to/containing/folder/";
    private String rootFolderTo = rootFolder + "to";
    private String rootFolderFrom = rootFolder + "from";
    private FileStorageStrategy from;
    private FileStorageStrategy to;
    private File tempFileWithContents;


    @Before
    public void setupTempFolders() throws IOException {
        tempFolder = Files.createTempDir();
        from = new FlatFolderFileStorageStrategy(rootFolderFrom, "BaseFolder");
        to = new FlatFolderFileStorageStrategy(rootFolderTo, "BaseFolder");
        tempFileWithContents = File.createTempFile("tempfilefortesting", "suffix", tempFolder);
    }

    @After
    public void teardownTempFolder() throws IOException {
        tempFileWithContents.delete();
        assertTrue(tempFolder.delete());
        FileUtils.deleteDirectory(pathElementsToFile(combineLists(rootFolderFrom, "BaseFolder")));
        FileUtils.deleteDirectory(pathElementsToFile(combineLists(rootFolderTo, "BaseFolder")));

    }

    @Test
    public void testMoveFileNormal() throws IOException {

        from.createFile(newFileEntry().with(id(123l)).build(), tempFileWithContents).getSuccess();
        from.createFile(newFileEntry().with(id(124l)).build(), tempFileWithContents).getSuccess();
        from.createFile(newFileEntry().with(id(125l)).build(), tempFileWithContents).getSuccess();

        final List<String> namesFrom = from.all().stream().map(path -> path.getValue()).collect(toList());
        assertEquals(3, namesFrom.size());
        final ServiceResult<List<File>> listServiceResult = MoveFiles.moveAllFiles(from, to, false);
        assertTrue(listServiceResult.isSuccess());
        assertEquals(3, listServiceResult.getSuccess().size());
        final List<String> namesTo = to.all().stream().map(path -> path.getValue()).collect(toList());
        assertEquals(namesFrom, namesTo);
    }

    @Test
    public void testMoveFileAlreadyExists() throws IOException {

        from.createFile(newFileEntry().with(id(123l)).build(), tempFileWithContents).getSuccess();
        to.createFile(newFileEntry().with(id(123l)).build(), tempFileWithContents).getSuccess();

        final ServiceResult<List<File>> listServiceResult = MoveFiles.moveAllFiles(from, to, false);
        assertTrue(listServiceResult.isFailure());
        assertTrue(listServiceResult.getFailure().is(CommonFailureKeys.FILES_DUPLICATE_FILE_MOVED));
    }

    @Test
    public void testMoveAlreadyMovedFileWhenNotExpected() throws IOException {
        try {

            to.createFile(newFileEntry().with(id(123l)).build(), tempFileWithContents).getSuccess();

            final FileStorageStrategy reportsNonExistentFile = new AllWaysReportsFile123EvenWhenItDoesNotExist(rootFolder + "reportsNonExistentFile", "BaseFolder");
            final ServiceResult<List<File>> listServiceResult = MoveFiles.moveAllFiles(reportsNonExistentFile, to, false);
            assertTrue(listServiceResult.isFailure());
            assertTrue(listServiceResult.getFailure().is(FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT));
        } finally {
            FileUtils.deleteDirectory(pathElementsToFile(combineLists(rootFolder + "reportsNonExistentFile", "BaseFolder")));
        }
    }

    @Test
    public void testMoveAlreadyMovedFileWhenExpected() throws IOException {
        // To represent an already move file we generate a file in the to location and get the from location to
        // report a file to move over to that location that does not exist. This simulates a move already having occurred
        // We also so a normal copy to ensure that that continues to work.
        to.createFile(newFileEntry().with(id(123l)).build(), tempFileWithContents).getSuccess();

        final FileStorageStrategy reportsNonExistentFile = new AllWaysReportsFile123EvenWhenItDoesNotExist(rootFolder + "reportsNonExistentFile", "BaseFolder");
        reportsNonExistentFile.createFile(newFileEntry().with(id(124l)).build(), tempFileWithContents); // A legit file
        final ServiceResult<List<File>> listServiceResult = MoveFiles.moveAllFiles(reportsNonExistentFile , to, true); // Ignore already moved errors
        assertTrue(listServiceResult.isSuccess());
        assertEquals(1, listServiceResult.getSuccess().size());
        assertEquals("124", listServiceResult.getSuccess().get(0).getName());
    }

    private final class AllWaysReportsFile123EvenWhenItDoesNotExist extends FlatFolderFileStorageStrategy {
        public AllWaysReportsFile123EvenWhenItDoesNotExist(String pathToStorageBase, String containingFolder) {
            super(pathToStorageBase, containingFolder);
        }
        @Override
        public List<Pair<Long, Pair<List<String>, String>>> allWithIds() {
            final List<Pair<Long, Pair<List<String>, String>>> base = super.allWithIds();
            base.add(Pair.of(123L, Pair.of(getAbsolutePathToFileUploadFolder(), "123")));
            // Make sure the not existing file comes first to be extra sure we handle legitimate cases when there are errors.
            reverse(base);
            return base;
        }

    }


}
