package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.IntFunction;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static java.io.File.separator;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.leftPad;

/**
 * This class represents a file system storage strategy that, based upon the id of a file, will store that file in a
 * folder structure that is partitioned by id ranges, at 3 levels of depth, and in such a way as to only have a maximum
 * of 1,000 files or folders within any given folder, for efficiency.
 *
 * So a file with id 50 would be stored as file "50" within an owning partition of "000_999", and this partition within
 * the owning partition "000000_999999", and this within "000000000_999999999".
 */
@Component
public class ByFileIdFileStorageStrategy extends BaseFileStorageStrategy {

    private int maxFilesInBottomLevelPartition = 1000;
    private int partitionLevels = 3;

    @Autowired
    public ByFileIdFileStorageStrategy(@Value("${ifs.data.service.file.storage.base}") String pathToStorageBase,
                                       @Value("${ifs.data.service.file.storage.containing.folder}") String containingFolder) {
        super(pathToStorageBase, containingFolder);
    }

    @Override
    public Pair<List<String>, String> getAbsoluteFilePathAndName(FileEntry file) {
        return getFilePathAndName(file.getId());
    }

    Pair<List<String>, String> getFilePathAndName(Long id) {

        IntFunction<String> folderNameFunction = depth -> {

            int powerOfMaxFilesAtThisLevel = partitionLevels - depth;

            long maxFilesAtThisLevel = (long) Math.pow(maxFilesInBottomLevelPartition, powerOfMaxFilesAtThisLevel);
            long startForThisPartition = (id / maxFilesAtThisLevel) * maxFilesAtThisLevel;
            long endForThisPartition = startForThisPartition + maxFilesAtThisLevel - 1;

            String startOfPartitionPadded = leftPad(startForThisPartition + "", (endForThisPartition + "").length(), '0');

            return startOfPartitionPadded + "_" + endForThisPartition;
        };

        return Pair.of(getFullFolderPathToFileAsSegments(folderNameFunction), id + "");
    }

    private List<String> getFullFolderPathToFileAsSegments(IntFunction<String> folderNameFunction) {

        List<String> fullPathToContainingFolder = getAbsolutePathToFileUploadFolder();
        List<String> folderPathToFileWithinContainingFolder = range(0, partitionLevels).mapToObj(folderNameFunction).collect(toList());
        return combineLists(fullPathToContainingFolder, folderPathToFileWithinContainingFolder);
    }

    private List<String> getAbsolutePathToFileUploadFolder() {
        return getAbsolutePathToFileUploadFolder(separator);
    }

    List<String> getAbsolutePathToFileUploadFolder(String fileSeparator) {

        // get an absolute path to the file upload folder, split into segments
        String separatorToUseInSplit = separatorForSplit(fileSeparator);
        List<String> pathToStorageBaseAsSegments = asList(pathToStorageBase.split(separatorToUseInSplit));
        List<String> fullPathToContainingFolderWithEmptyElements = combineLists(pathToStorageBaseAsSegments, asList(containingFolder));
        List<String> fullPathWithNoEmptyElements = simpleFilterNot(fullPathToContainingFolderWithEmptyElements, StringUtils::isBlank);

        // then if using a *nix path that starts with file separator e.g. /tmp/path, ensure that we retain the leading "/"
        if (pathToStorageBase.startsWith(fileSeparator)) {
            fullPathWithNoEmptyElements.set(0, fileSeparator + fullPathWithNoEmptyElements.get(0));
        }

        return fullPathWithNoEmptyElements;
    }

    private String separatorForSplit(String fileSeparator) {
        if (fileSeparator.equals("\\")) {
            return "\\\\";
        }
        return fileSeparator;
    }
}
