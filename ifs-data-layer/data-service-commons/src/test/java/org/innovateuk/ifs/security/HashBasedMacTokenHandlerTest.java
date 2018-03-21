package org.innovateuk.ifs.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HashBasedMacTokenHandlerTest {

    private HashBasedMacTokenHandler hashBasedMacTokenHandler;

    @Before
    public void setUp() throws Exception {
        hashBasedMacTokenHandler = new HashBasedMacTokenHandler();
    }

    @Test
    public void calculateHash() throws Exception {
        assertEquals("f6d99caceac489fd2d4ba8106d15e64bd7455fd83305f13a7faa32fb3b02fa28",
                hashBasedMacTokenHandler.calculateHash("supersecretkey", "input"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateHash_nullKey() throws Exception {
        hashBasedMacTokenHandler.calculateHash(null, "input");
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateHash_emptyKey() throws Exception {
        hashBasedMacTokenHandler.calculateHash("", "input");
    }

    @Test
    public void calculateHash_nullData() throws Exception {
        assertEquals("c46ebcad47b875a746029ac6c2f8636ffd012d2b3cd524d77f2d813b5b74f589",
                hashBasedMacTokenHandler.calculateHash("supersecretkey", null));
    }

    @Test
    public void calculateHash_emptyData() throws Exception {
        assertEquals("c46ebcad47b875a746029ac6c2f8636ffd012d2b3cd524d77f2d813b5b74f589",
                hashBasedMacTokenHandler.calculateHash("supersecretkey", ""));
    }
}