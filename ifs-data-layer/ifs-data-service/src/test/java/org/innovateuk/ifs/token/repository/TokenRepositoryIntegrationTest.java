package org.innovateuk.ifs.token.repository;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.innovateuk.ifs.token.resource.TokenType.RESET_PASSWORD;
import static org.innovateuk.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static java.time.ZonedDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<TokenRepository> {

    private static final CharSequence HASH_SALT = "klj12nm6nsdgfnlk12ctw476kl";

    @Before
    public void setUp() throws Exception {

    }

    @Autowired
    @Override
    protected void setRepository(final TokenRepository repository) {
        this.repository = repository;
    }

    @Rollback
    @Test
    public void testFindByHash() throws Exception {
        final String hash = hash(1L, "firstname.lastname@innovateuk.org");

        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash, now(), JsonNodeFactory.instance.objectNode());

        final Token expected = repository.save(token);

        final Token found = repository.findByHash(hash).get();
        assertEquals(expected, found);
    }

    @Rollback
    @Test
    public void testFindByHashAndTypeAndClassName() throws Exception {
        final String hash1 = hash(1L, "firstname.lastname@innovateuk.org");
        final String hash2 = hash(1L, "firstname.lastname@innovateuk.org");

        final Token token1 = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash1, now(), JsonNodeFactory.instance.objectNode());
        final Token token2 = new Token(RESET_PASSWORD, User.class.getName(), 1L, hash2, now(), JsonNodeFactory.instance.objectNode());

        final Token expected = repository.save(token1);
        repository.save(token2);

        final Optional<Token> found = repository.findByHashAndTypeAndClassName(hash1, VERIFY_EMAIL_ADDRESS, User.class.getName());
        assertTrue(found.isPresent());
        assertEquals(expected, found.get());
    }

    @Rollback
    @Test
    public void testFindByTypeAndClassNameAndClassPk() throws Exception {
        final String hash1 = hash(1L, "user.one@innovateuk.org");
        final String hash2 = hash(2L, "user.two@innovateuk.org");

        final Token token1 = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash1, now(), JsonNodeFactory.instance.objectNode());
        final Token token2 = new Token(RESET_PASSWORD, User.class.getName(), 2L, hash2, now(), JsonNodeFactory.instance.objectNode());

        final Token expected = repository.save(token1);
        repository.save(token2);

        Optional<Token> found = repository.findByTypeAndClassNameAndClassPk(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L);
        assertTrue(found.isPresent());
        assertEquals(token1, found.get());
    }

    private String hash(final Long userId, final String email) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(HASH_SALT);
        int random = (int) Math.ceil(Math.random() * 1000); // random number from 1 to 1000
        String hash = String.format("%s==%s==%s", userId, email, random);
        return encoder.encode(hash);
    }

}
