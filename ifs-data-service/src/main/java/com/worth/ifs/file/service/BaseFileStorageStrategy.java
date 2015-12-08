package com.worth.ifs.file.service;

/**
 * Represents a component that, given a BaseFile to store, decides how best to store it and stores it on the filesystem.
 * This base class is for a storage mechanism that expects to have a pointer to a base folder that all storage is performed
 * within, as well as a containing folder within that storage base that this strategy will put all files into (and create
 * if it doesn't yet exist)
 */
abstract class BaseFileStorageStrategy implements FileStorageStrategy {

    protected String pathToStorageBase;
    protected String containingFolder;

    public BaseFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        this.pathToStorageBase = pathToStorageBase;
        this.containingFolder = containingFolder;
    }
}