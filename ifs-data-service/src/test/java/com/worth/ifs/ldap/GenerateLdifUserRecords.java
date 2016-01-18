package com.worth.ifs.ldap;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

/**
 * Temporary
 */
public class GenerateLdifUserRecords {

    private static class LdapUserRecord {

        String firstName;
        String lastName;
        String emailAddress;
        String title;

        public LdapUserRecord(String firstName, String lastName, String emailAddress, String title) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.title = title;
        }
    }

    public static LdapUserRecord createLdapUserRecord(String firstName, String lastName, String emailAddress, String title) {
        return new LdapUserRecord(firstName, lastName, emailAddress, title);
    }

    public static String generateLdapUserRecord(LdapUserRecord... userRecords) {

        List<String> results = simpleMap(asList(userRecords), userRecord -> {

            try {
                String fullName = userRecord.firstName + " " + userRecord.lastName;

                Map<String, String> replacements = new HashMap<>();
                replacements.put("fullName", fullName);
                replacements.put("firstName", userRecord.firstName);
                replacements.put("lastName", userRecord.lastName);
                replacements.put("uid", userRecord.firstName.toLowerCase() + "." + userRecord.lastName.toLowerCase());
                replacements.put("emailAddress", userRecord.emailAddress);
                replacements.put("title", userRecord.title);

                File template = new File(Thread.currentThread().getContextClassLoader().getResource("user_ldif_record_template.txt").toURI());
                List<String> templateLines = Files.readAllLines(template.toPath());
                String templateContents = templateLines.stream().collect(joining("\n"));

                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    templateContents = templateContents.replace("$" + replacement.getKey(), replacement.getValue());
                }

                return templateContents;

            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });

        return simpleJoiner(results, "\n\n\n");

    }

    // DEVELOPMENT, INTEGRATION ENVIRONMENT AND WEBTEST DATA
    public static void main(String[] args) throws IOException, URISyntaxException {

        String contents = generateLdapUserRecord(
                createLdapUserRecord("Steve", "Smith", "steve.smith@empire.com", "Mr"),
                createLdapUserRecord("Jessica", "Doe", "jessica.doe@ludlow.co.uk", "Mrs"),
                createLdapUserRecord("Professor", "Plum", "paul.plum@gmail.com", "Prof"),
                createLdapUserRecord("Comp", "Exec (Competitions)", "competitions@innovateuk.gov.uk", "Mr"),
                createLdapUserRecord("Project", "Finance Analyst (Finance)", "finance@innovateuk.gov.uk", "Mr"),
                createLdapUserRecord("Pete", "Tom", "pete.tom@egg.com", "Mr"),
                createLdapUserRecord("Felix", "Wilson", "felix.wilson@gmail.com", "Mr"));

        System.out.println(contents);
    }
}
