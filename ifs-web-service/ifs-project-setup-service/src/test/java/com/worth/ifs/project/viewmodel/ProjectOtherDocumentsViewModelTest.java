package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectOtherDocumentsViewModelTest {

    @Test
    public void testIsShowingDocumentsBeingReviewed() {
        boolean otherDocumentsSubmitted = true;
        //Null means that an approval decision hasn't been made yet.
        Boolean approved = null;
        ProjectOtherDocumentsViewModel model = new ProjectOtherDocumentsViewModel(null, null, null, null, null, null,
                false, otherDocumentsSubmitted, approved, false, null);

        assertTrue(model.isShowDocumentsBeingReviewedMessage());
    }

    @Test
    public void testIsShowingDocumentsNotBeingReviewed() {
        boolean otherDocumentsSubmitted = true;
        Boolean approved = false;
        ProjectOtherDocumentsViewModel model = new ProjectOtherDocumentsViewModel(null, null, null, null, null, null,
                false, otherDocumentsSubmitted, approved, false, null);

        assertFalse(model.isShowDocumentsBeingReviewedMessage());
    }

    @Test
    public void testIsShowApprovedMessage() {
        Boolean approved = true;
        ProjectOtherDocumentsViewModel model = new ProjectOtherDocumentsViewModel(null, null, null, null, null, null,
                false, false, approved, false, null);

        assertTrue(model.isShowApprovedMessage());
    }

    @Test
    public void testIsShowNotApprovedMessage() {
        //Null means that an approval decision hasn't been made yet.
        Boolean approved = null;
        ProjectOtherDocumentsViewModel model = new ProjectOtherDocumentsViewModel(null, null, null, null, null, null,
                false, false, approved, false, null);


        assertFalse(model.isShowApprovedMessage());
    }

}
