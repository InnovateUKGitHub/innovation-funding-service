package org.innovateuk.ifs.starter.feign.filestorage.v1.feign;

import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletion;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "file-storage-service-deletion", url = "${ifs.feign.file-storage-service}")
public interface FileDeletionFeign extends FileDeletion {

}
