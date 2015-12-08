package com.worth.ifs.file.service;

import com.worth.ifs.file.domain.BaseFile;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Represents a component that is able to advise where to store files based upon its strategy.
 */
public interface FileStorageStrategy {

    Pair<List<String>, String> getAbsoluteFilePathAndName(BaseFile file);
}
