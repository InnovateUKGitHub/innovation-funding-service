package com.worth.ifs.controller.viewmodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssessorFeedbackViewModelTest {

    @Test
    public void testWithNoFile() {

        AssessorFeedbackViewModel editableViewModel = AssessorFeedbackViewModel.withNoFile(false);
        assertNull(editableViewModel.getFilename());
        assertFalse(editableViewModel.isFileUploaded());
        assertFalse(editableViewModel.isReadonly());

        AssessorFeedbackViewModel readOnlyViewModel = AssessorFeedbackViewModel.withNoFile(true);
        assertNull(readOnlyViewModel.getFilename());
        assertFalse(readOnlyViewModel.isFileUploaded());
        assertTrue(readOnlyViewModel.isReadonly());

        assertEquals(AssessorFeedbackViewModel.withNoFile(false), AssessorFeedbackViewModel.withNoFile(false));
        assertEquals(AssessorFeedbackViewModel.withNoFile(true), AssessorFeedbackViewModel.withNoFile(true));
        assertNotEquals(AssessorFeedbackViewModel.withNoFile(true), AssessorFeedbackViewModel.withNoFile(false));
    }

    @Test
    public void testWithExistingFile() {

        AssessorFeedbackViewModel editableViewModel = AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, false);
        assertEquals("myfile", editableViewModel.getFilename());
        assertTrue(editableViewModel.isFileUploaded());
        assertFalse(editableViewModel.isReadonly());

        AssessorFeedbackViewModel readOnlyViewModel = AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, true);
        assertEquals("myfile", readOnlyViewModel.getFilename());
        assertTrue(readOnlyViewModel.isFileUploaded());
        assertTrue(readOnlyViewModel.isReadonly());

        assertEquals(AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, false), AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, false));
        assertEquals(AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, true), AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, true));
        assertNotEquals(AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, true), AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, false));
        assertNotEquals(AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, true), AssessorFeedbackViewModel.withExistingFile("notmyfile", 1000L, true));
        assertNotEquals(AssessorFeedbackViewModel.withExistingFile("myfile", 1000L, true), AssessorFeedbackViewModel.withExistingFile("myfile", 9999L, true));
    }
}
