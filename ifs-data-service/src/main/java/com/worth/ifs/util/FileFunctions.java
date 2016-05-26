package com.worth.ifs.util;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.Arrays.asList;

/**
 * A collection of helpful methods for dealing with Files and Filesystem concerns
 */
public final class FileFunctions {

	private FileFunctions(){}
	
    static String separator = File.separator;
    
    /**
     * Given a list of path parts, this method will construct a path separated by the file separator.
     *
     * E.g. given ("path", "to", "file"), this will return "path/to/file".
     *
     * @param pathElements
     * @return
     */
    public static final String pathElementsToPathString(List<String> pathElements) {

        if (pathElements == null || pathElements.isEmpty()) {
            return "";
        }

        return pathElements.stream().reduce("",
                (pathSoFar, nextPathSegment) -> pathSoFar + (!pathSoFar.isEmpty() ? separator : "") + nextPathSegment);
    }

    public static final List<String> pathStringToPathElements(final String pathString){
        if (pathString == null || pathString.isEmpty()){
            return new ArrayList<>();
        }
        return asList(pathString.split(Pattern.quote(File.separator)));
    }

    /**
     * Given a list of path parts, this method will construct a path separated by the file separator, and ensures that the
     * path is absolute.
     *
     * E.g. given ("path", "to", "file"), this will return "/path/to/file".  Note the leading "/".
     * E.g. given ("/path", "to", "file"), this will return "/path/to/file".  Note the leading "/" is not duplicated.
     *
     * @param pathElements
     * @return
     */
    public static final String pathElementsToAbsolutePathString(List<String> pathElements, String absolutePathPrefix) {
        String path = pathElementsToPathString(pathElements);
        return path.startsWith(absolutePathPrefix) ? path : absolutePathPrefix + path;
    }

    /**
     * Given a list of path parts, this method will construct a new list of path elements separated by the file separator, and ensures that the
     * path is absolute.
     *
     * E.g. given ("path", "to", "file"), this will return "/path", "/to", "/file".  Note the leading "/".
     * E.g. given ("/path", "to", "file"), this will return "/path/to/file".  Note the leading "/" is not duplicated.
     *
     * @param pathElements
     * @return
     */
    public static final List<String> pathElementsToAbsolutePathElements(List<String> pathElements, String absolutePathPrefix) {

        if (pathElements.get(0).startsWith(absolutePathPrefix)) {
            return pathElements;
        }

        String absoluteFirstSegment = absolutePathPrefix + pathElements.get(0);
        return combineLists(absoluteFirstSegment, pathElements.subList(1, pathElements.size()));
    }

    /**
     * Given a list of path parts, this method will construct a path separated by the file separator.
     *
     * E.g. given ("path", "to", "file"), this will return "path/to/file".
     *
     * @param pathElements
     * @return
     */
    public static final File pathElementsToFile(List<String> pathElements) {
        return new File(pathElementsToPathString(pathElements));
    }

    /**
     * Given a list of path parts, this method will construct a path separated by the file separator.
     *
     * E.g. given ("path", "to", "file"), this will return "path/to/file".
     *
     * @param pathElements
     * @return
     */
    public static final Path pathElementsToPath(List<String> pathElements) {
        return pathElementsToFile(pathElements).toPath();
    }
}
