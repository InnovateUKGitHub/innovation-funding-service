package org.innovateuk.ifs.finance.pact;

import au.com.dius.pact.model.Pact;
import au.com.dius.pact.model.PactSource;
import au.com.dius.pact.provider.junit.loader.PactFolderLoader;
import au.com.dius.pact.provider.junit.loader.PactLoader;
import au.com.dius.pact.provider.junit.sysprops.ValueResolver;
import au.com.dius.pact.provider.spring.SpringEnvironmentResolver;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Custom {@link PactLoader} that uses the {@Link SpringEnvironmentResolver} to resolve a Spring environment expression
 * for the pact directory. Delegates to a {@link PactFolderLoader} to load pacts from the directory.
 */
public class SpringPactFolderLoader implements PactLoader {

    private PactLoader pactLoader;

    private ValueResolver valueResolver;

    public SpringPactFolderLoader(Class clazz) {
    }

    @Override
    public List<Pact> load(String providerName) throws IOException {
        return getPactLoader().load(providerName);
    }

    @Override
    public PactSource getPactSource() {
        return getPactLoader().getPactSource();
    }

    @Override
    public void setValueResolver(ValueResolver valueResolver) {
        if (!(valueResolver instanceof SpringEnvironmentResolver)) {
            throw new IllegalArgumentException("Expecting a SpringEnvironmentResolver");
        }
        this.valueResolver = valueResolver;
    }

    private PactLoader getPactLoader() {
        if (pactLoader == null) {
            if (valueResolver == null) {
                throw new IllegalStateException("ValueResolver should have been set after creating this PactLoader");
            }
            String path = valueResolver.resolveValue("pact.dir");
            pactLoader = new PactFolderLoader(new File(path));
        }
        return pactLoader;
    }
}
