package org.innovateuk.ifs;

/**
 * An annotation to mark a test as a Controller test.
 *
 * This is primarily useful for flagging up tests that help produce the Asciidoc API documentation, and we are
 * therefore able to target only these tests to run when producing API documentation.
 */
public @interface ControllerTest {
}
