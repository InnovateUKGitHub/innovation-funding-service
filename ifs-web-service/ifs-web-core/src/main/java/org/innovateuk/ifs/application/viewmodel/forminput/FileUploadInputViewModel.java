package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.form.resource.FormInputType;

public class FileUploadInputViewModel extends AbstractFormInputViewModel {

    private String filename;
    private String viewmode;
    private ApplicationResource application;

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

    public String getViewmode() {
        return viewmode;
    }

    public void setViewmode(String viewmode) {
        this.viewmode = viewmode;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    /* View logic methods. */
    public boolean isMayRemove() {
        return viewmode.equals("edit");
    }
}
