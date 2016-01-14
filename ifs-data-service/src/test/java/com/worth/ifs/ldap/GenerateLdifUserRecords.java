package com.worth.ifs.ldap;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

/**
 *
 */
public class GenerateLdifUserRecords {


    public static String generateLdapUserRecord(String firstName, String lastName, String emailAddress, String title) throws URISyntaxException, IOException {

        String fullName = firstName + " " + lastName;

        Map<String, String> replacements = new HashMap<>();
        replacements.put("fullName", fullName);
        replacements.put("firstName", firstName);
        replacements.put("lastName", lastName);
        replacements.put("uid", firstName.toLowerCase() + "." + lastName.toLowerCase());
        replacements.put("emailAddress", emailAddress);
        replacements.put("title", title);

        File template = new File(Thread.currentThread().getContextClassLoader().getResource("user_ldif_record_template.txt").toURI());
        List<String> templateLines = Files.readAllLines(template.toPath());
        String templateContents = templateLines.stream().collect(joining("\n"));

        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            templateContents = templateContents.replace("$" + replacement.getKey(), replacement.getValue());
        }

        return templateContents;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        String recordSeparator = "\n\n# ==================================\n\n";

        System.out.println(generateLdapUserRecord("Steve", "Smith", "steve.smith@empire.com", "Mr") + recordSeparator);
        System.out.println(generateLdapUserRecord("Jessica", "Doe", "jessica.doe@ludlow.co.uk", "Mrs") + recordSeparator);
        System.out.println(generateLdapUserRecord("Professor", "Plum", "paul.plum@gmail.com", "Prof") + recordSeparator);
        System.out.println(generateLdapUserRecord("Comp", "Exec (Competitions)", "competitions@innovateuk.gov.uk", "Mr") + recordSeparator);
        System.out.println(generateLdapUserRecord("Project", "Finance Analyst (Finance)", "finance@innovateuk.gov.uk", "Mr") + recordSeparator);
        System.out.println(generateLdapUserRecord("Pete", "Tom", "pete.tom@egg.com", "Mr") + recordSeparator);
        System.out.println(generateLdapUserRecord("Felix", "Wilson", "felix.wilson@gmail.com", "Mr") + recordSeparator);

    }
}
