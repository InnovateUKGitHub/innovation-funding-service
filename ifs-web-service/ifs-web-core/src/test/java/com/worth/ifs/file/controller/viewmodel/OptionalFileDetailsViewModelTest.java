package com.worth.ifs.file.controller.viewmodel;

import org.junit.Test;

import static org.junit.Assert.*;

public class OptionalFileDetailsViewModelTest {

    @Test
    public void testWithNoFile() {

        OptionalFileDetailsViewModel editableViewModel = OptionalFileDetailsViewModel.withNoFile(false);
        assertNull(editableViewModel.getFilename());
        assertFalse(editableViewModel.isFileUploaded());
        assertFalse(editableViewModel.isReadonly());

        OptionalFileDetailsViewModel readOnlyViewModel = OptionalFileDetailsViewModel.withNoFile(true);
        assertNull(readOnlyViewModel.getFilename());
        assertFalse(readOnlyViewModel.isFileUploaded());
        assertTrue(readOnlyViewModel.isReadonly());

        assertEquals(OptionalFileDetailsViewModel.withNoFile(false), OptionalFileDetailsViewModel.withNoFile(false));
        assertEquals(OptionalFileDetailsViewModel.withNoFile(true), OptionalFileDetailsViewModel.withNoFile(true));
        assertNotEquals(OptionalFileDetailsViewModel.withNoFile(true), OptionalFileDetailsViewModel.withNoFile(false));
    }

    @Test
    public void testWithExistingFile() {

        OptionalFileDetailsViewModel editableViewModel = OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, false);
        assertEquals("myfile", editableViewModel.getFilename());
        assertTrue(editableViewModel.isFileUploaded());
        assertFalse(editableViewModel.isReadonly());

        OptionalFileDetailsViewModel readOnlyViewModel = OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, true);
        assertEquals("myfile", readOnlyViewModel.getFilename());
        assertTrue(readOnlyViewModel.isFileUploaded());
        assertTrue(readOnlyViewModel.isReadonly());

        assertEquals(OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, false), OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, false));
        assertEquals(OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, true), OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, true));
        assertNotEquals(OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, true), OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, false));
        assertNotEquals(OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, true), OptionalFileDetailsViewModel.withExistingFile("notmyfile", 1000L, true));
        assertNotEquals(OptionalFileDetailsViewModel.withExistingFile("myfile", 1000L, true), OptionalFileDetailsViewModel.withExistingFile("myfile", 9999L, true));
    }
}
