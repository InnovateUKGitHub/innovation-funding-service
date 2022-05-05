package org.innovateuk.ifs.util;

import java.util.stream.Collectors;

/**
 * This is just a temporary (cheeky) way to force people into installing java 11
 *
 * https://devops.innovateuk.org/documentation/display/IFS/Java+on+Mac
 */
public class JavaVersion {

    private JavaVersion() {}

    public static void javaEleven() {
        String multilineString = "Please upgrade \n \n to \n Java 11 ;)";
        multilineString.lines()
                .filter(line -> !line.isBlank())
                .map(String::strip).collect(Collectors.toList());

    }

}
