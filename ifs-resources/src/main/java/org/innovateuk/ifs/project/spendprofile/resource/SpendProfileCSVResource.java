package org.innovateuk.ifs.project.spendprofile.resource;

/*
 * Resource object to wrap the spend profile CSV data and information for the export.
 */
public class SpendProfileCSVResource {

    private String csvData;

    private String fileName;

    public String getCsvData() {
        return csvData;
    }

    public void setCsvData(String csvData) {
        this.csvData = csvData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
