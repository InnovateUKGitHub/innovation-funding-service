package com.worth.ifs.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledFileMoveFiles {



    @Value("${ifs.data.service.file.storage.virus.scanning.scanned.folder}")
    private String fromDirectory;

    @Value("$ifs.data.service.file.storage.base}")
    private String toDirectory;

    @Scheduled(fixedDelayString= "${ifs.data.service.file.storage.virus.scanning.scanned.move.delay.millis}")
    public void moveFiles(){
        // TODO
    }


}
