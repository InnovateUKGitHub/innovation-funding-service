package org.innovateuk.ifs.tmp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
@Slf4j
public class OOMKiller {

    @PostConstruct
    public void killer() {
        try {
            generateOOM();
        } catch (Exception e) {
           log.error("kill fail", e);
        }
    }

    public void generateOOM() throws Exception {
        ArrayList v = new ArrayList();
        while (true)
        {
            byte b[] = new byte[10485760];
            v.add(b);
            Runtime rt = Runtime.getRuntime();
            log.warn( "free memory: " + rt.freeMemory() );
            log.warn( "total memory: " + rt.totalMemory());
            log.warn( "max memory: " + rt.maxMemory());
            Thread.sleep(1000);
        }
    }
}
