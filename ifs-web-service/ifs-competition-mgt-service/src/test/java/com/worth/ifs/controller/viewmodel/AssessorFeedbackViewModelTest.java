package com.worth.ifs.controller.viewmodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssessorFeedbackViewModelTest {

    @Test
    public void testWithNoFile() {

        AssessorFeedbackViewModel editableViewModel = AssessorFeedbackViewModel.withNoFile(false);
        assertNull(editableViewModel.getFilename());
        assertTrue(editableViewModel.isNoFileUploaded());
        assertFalse(editableViewModel.isReadonly());

        AssessorFeedbackViewModel readOnlyViewModel = AssessorFeedbackViewModel.withNoFile(true);
        assertNull(readOnlyViewModel.getFilename());
        assertTrue(readOnlyViewModel.isNoFileUploaded());
        assertTrue(readOnlyViewModel.isReadonly());

        assertEquals(AssessorFeedbackViewModel.withNoFile(false), AssessorFeedbackViewModel.withNoFile(false));
        assertEquals(AssessorFeedbackViewModel.withNoFile(true), AssessorFeedbackViewModel.withNoFile(true));
        assertNotEquals(AssessorFeedbackViewModel.withNoFile(true), AssessorFeedbackViewModel.withNoFile(false));
    }

    @Test
    public void testWithExistingFile() {

        AssessorFeedbackViewModel editableViewModel = AssessorFeedbackViewModel.withExistingFile("myfile", false);
        assertEquals("myfile", editableViewModel.getFilename());
        assertFalse(editableViewModel.isNoFileUploaded());
        assertFalse(editableViewModel.isReadonly());

        AssessorFeedbackViewModel readOnlyViewModel = AssessorFeedbackViewModel.withExistingFile("myfile", true);
        assertEquals("myfile", readOnlyViewModel.getFilename());
        assertFalse(readOnlyViewModel.isNoFileUploaded());
        assertTrue(readOnlyViewModel.isReadonly());

        assertEquals(AssessorFeedbackViewModel.withExistingFile("myfile", false), AssessorFeedbackViewModel.withExistingFile("myfile", false));
        assertEquals(AssessorFeedbackViewModel.withExistingFile("myfile", true), AssessorFeedbackViewModel.withExistingFile("myfile", true));
        assertNotEquals(AssessorFeedbackViewModel.withExistingFile("myfile", true), AssessorFeedbackViewModel.withExistingFile("myfile", false));
        assertNotEquals(AssessorFeedbackViewModel.withExistingFile("myfile", true), AssessorFeedbackViewModel.withExistingFile("notmyfile", true));
    }
}
