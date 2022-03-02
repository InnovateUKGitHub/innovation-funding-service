package org.innovateuk.ifs.starters.stubdev.security;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.innovateuk.ifs.commons.security.UidSupplier;
import org.innovateuk.ifs.starters.stubdev.cfg.StubDevConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import sun.security.action.GetPropertyAction;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.security.AccessController.doPrivileged;

/**
 * Light touch for overriding the default security implementation.
 *
 * This overrides the default UidSupplier in stub mode and returns the default or user specified uuid.
 *
 * The uuid is shared via a temp file among all services (jvms) and is not thread safe.
 */
public class StubUidSupplier implements UidSupplier {

    @Autowired
    private StubDevConfigurationProperties stubDevConfigurationProperties;

    private File uuidTempFile;

    @PostConstruct
    public void init() throws IOException {
        Path tmpdir = Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")));
        uuidTempFile = tmpdir.resolve("stub-auth-user.tmp").toFile();
        if (uuidTempFile.createNewFile()) {
            setUuid(stubDevConfigurationProperties.getDefaultUuid());
        }
    }

    @Override
    public String getUid(HttpServletRequest request) {
        try {
            return Files.asCharSource(uuidTempFile, Charsets.UTF_8).read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uuid file", e);
        }
    }

    protected void setUuid(String userUuid) {
        try {
            Files.asCharSink(uuidTempFile, Charsets.UTF_8).write(userUuid);
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist uuid file", e);
        }
    }

}
