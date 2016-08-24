package com.worth.ifs.project.otherdocuments.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.util.BooleanFunctions;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.sun.jmx.snmp.EnumRowStatus.active;

/**
 * Form backing the Other Documents page
 */
public class ProjectOtherDocumentsForm extends BaseBindingResultTarget {

    private boolean rejected;
    private boolean approved;
    private List<String> rejectionReasons;

    public boolean isApproved(){return this.approved;}

    public void setApproved(boolean approved){this.approved = approved;}

    public boolean isRejected(){return this.rejected;}

    public void setRejected(boolean rejected){this.rejected = rejected;}

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<String> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }


}
