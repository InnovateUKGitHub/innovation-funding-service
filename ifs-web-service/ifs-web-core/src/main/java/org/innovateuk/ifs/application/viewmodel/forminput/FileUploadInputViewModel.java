package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.form.resource.FormInputType;

public class FileUploadInputViewModel extends AbstractFormInputViewModel {

    private String filename; // r
    private String downloadUrl; //
    private String viewmode; /*
    ${readonly} or
                ${question.isMarkAsCompletedEnabled() and markedAsComplete?.contains(question.id)} or
                ${(currentUser.getId() != questionAssignee?.assigneeUserId and  questionAssignee?.assignee!=null) or
                (questionAssignee?.assignee==null and !userIsLeadApplicant and !subFinanceSection)} ? 'readonly' : 'edit',
                */
    private boolean mayRemove; // ${viewmode == 'edit'}

    @Override
    protected FormInputType formInputType() {
        return FormInputType.FILEUPLOAD;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getViewmode() {
        return viewmode;
    }

    public void setViewmode(String viewmode) {
        this.viewmode = viewmode;
    }

    public boolean isMayRemove() {
        return mayRemove;
    }

    public void setMayRemove(boolean mayRemove) {
        this.mayRemove = mayRemove;
    }
}
