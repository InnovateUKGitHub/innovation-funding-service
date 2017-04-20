package org.innovateuk.ifs.project.spendprofile.resource;

import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
