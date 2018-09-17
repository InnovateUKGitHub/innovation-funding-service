package org.innovateuk.ifs.monitoring;

import org.innovateuk.ifs.monitoring.FileStorageHealthIndicator.FileOperationsWrapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
public class FileStorageHealthIndicatorTest {

    private FileStorageHealthIndicator indicator;

    private FileOperationsWrapper fileOperationsWrapper;

    @Before
    public void setUp() {
        indicator = new FileStorageHealthIndicator();

        fileOperationsWrapper = mock(FileOperationsWrapper.class);
        indicator.setFileOperationsWrapper(fileOperationsWrapper);

        indicator.setFileStoragePath("files");
    }

    @Test
    public void shouldReportHealthy() {
        //given
        when(fileOperationsWrapper.isWritable("files")).thenReturn(true);

        //when
        Health result = indicator.health();

        //then
        assertThat(result.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void shouldReportUnhealthy() {
        //given
        when(fileOperationsWrapper.isWritable("files")).thenReturn(false);

        //when
        Health result = indicator.health();

        //then
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    public void shouldCreateAbsentDirectoryIfConfigured() {
        //given
        indicator.setAllowCreateStoragePath(true);
        when(fileOperationsWrapper.exists("files")).thenReturn(false);

        //when
        indicator.health();

        //then
        verify(fileOperationsWrapper).createDirectory("files");
    }

    @Test
    public void shouldNotCreateAbsentDirectoryIfConfigured() {
        //given
        indicator.setAllowCreateStoragePath(false);
        when(fileOperationsWrapper.exists("files")).thenReturn(false);

        //when
        indicator.health();

        //then
        verify(fileOperationsWrapper, never()).createDirectory("files");
    }


}
