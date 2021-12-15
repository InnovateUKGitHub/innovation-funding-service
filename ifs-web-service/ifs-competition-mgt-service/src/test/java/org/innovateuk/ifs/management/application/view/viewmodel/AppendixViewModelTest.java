package org.innovateuk.ifs.management.application.view.viewmodel;

import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AppendixViewModelTest {

    @Test
    void getHumanReadableFileSize() {
        assertThat(buildAppendixViewModel(1048576L).getHumanReadableFileSize(), equalTo("1"));
        assertThat(buildAppendixViewModel(1000000L).getHumanReadableFileSize(), equalTo("0"));
        assertThat(buildAppendixViewModel(5242880L).getHumanReadableFileSize(), equalTo("5"));
    }

    private AppendixViewModel buildAppendixViewModel(Long sizeBytes) {
        FileEntryResource fileEntryResource = new FileEntryResource();
        fileEntryResource.setFilesizeBytes(sizeBytes);
        AppendixViewModel appendixViewModel = new AppendixViewModel(1L, 1L, "title", fileEntryResource);
        return appendixViewModel;
    }

}