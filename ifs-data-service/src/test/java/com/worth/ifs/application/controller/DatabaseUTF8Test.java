package com.worth.ifs.application.controller;

import org.junit.Test;

import java.io.File;

import static java.lang.Thread.currentThread;
import static java.nio.file.Files.readAllLines;
import static org.junit.Assert.fail;

public class DatabaseUTF8Test {

    private static final String SCHEMA_SCRIPT_DIRECTORY_NAME = "migration";

    @Test
    public void test() throws Exception {
        File db = new File(currentThread().getContextClassLoader().getResource("db").toURI());
        for (File dataDirectory : db.listFiles(f -> f.isDirectory())) {
            for (File sqlFile : dataDirectory.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".sql"))) {
                for (String line : readAllLines(sqlFile.toPath())) {
                    if (line.toLowerCase().contains("latin1")) {
                        fail("Use only utf8 for database sripts. Script: " + sqlFile.getName());
                    }
                }
            }
        }
    }

}
