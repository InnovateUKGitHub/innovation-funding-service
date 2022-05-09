package org.innovateuk.ifs.api.filestorage.v1.upload.feign;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${feign.name}", url = "${feign.url}", configuration = FeignConfiguration.class)
public interface FileUploadFeign extends FileUpload {

}
