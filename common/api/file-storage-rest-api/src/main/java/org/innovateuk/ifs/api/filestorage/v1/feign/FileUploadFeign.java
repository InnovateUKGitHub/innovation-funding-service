package org.innovateuk.ifs.api.filestorage.v1.feign;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "file-storage-service-upload", url = "${ifs.feign.file-storage-service}")
public interface FileUploadFeign extends FileUpload {

}
