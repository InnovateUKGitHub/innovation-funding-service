package com.worth.ifs.file.service;

import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.IntFunction;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
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
    public ByFileIdFileStorageStrategy(@Value("${ifs.data.service.file.storage.base}") String pathToStorageBase, @Value("${ifs.data.service.file.storage.containing.folder}") String containingFolder) {
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

        List<String> path = combineLists(asList(containingFolder), range(0, partitionLevels).mapToObj(folderNameFunction).collect(toList()));
        return Pair.of(path, id + "");
    }
}
