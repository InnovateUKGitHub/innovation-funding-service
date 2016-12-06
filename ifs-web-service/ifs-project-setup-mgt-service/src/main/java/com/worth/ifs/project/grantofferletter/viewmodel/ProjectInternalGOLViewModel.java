package com.worth.ifs.project.grantofferletter.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

/**
 * A view model that backs the Project grant offer letter page
 **/
public class ProjectInternalGOLViewModel implements BasicProjectDetailsViewModel {

    private final Long projectId;
    private final String projectName;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;

    public ProjectInternalGOLViewModel(Long projectId, String projectName, FileDetailsViewModel grantOfferLetterFile,
                                       FileDetailsViewModel additionalContractFile) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }


    public FileDetailsViewModel getGrantOfferLetterFile() {
        return grantOfferLetterFile;
    }

    public void setGrantOfferLetterFile(FileDetailsViewModel grantOfferLetterFile) {
        this.grantOfferLetterFile = grantOfferLetterFile;
    }

    public FileDetailsViewModel getAdditionalContractFile() {
        return additionalContractFile;
    }

    public void setAdditionalContractFile(FileDetailsViewModel additionalContractFile) {
        this.additionalContractFile = additionalContractFile;
    }

}
