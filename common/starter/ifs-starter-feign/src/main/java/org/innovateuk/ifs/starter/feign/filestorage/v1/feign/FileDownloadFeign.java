package org.innovateuk.ifs.starter.feign.filestorage.v1.feign;

import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "file-storage-service-download", url = "${ifs.feign.file-storage-service}")
public interface FileDownloadFeign extends FileDownload {

}
