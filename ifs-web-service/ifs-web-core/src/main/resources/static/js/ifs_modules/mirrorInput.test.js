describe('test mirrorInput', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./mirrorInput")
    })

    test("test getSourceText", () => {
        var textElement = jQuery('<div>test text</div>');
        expect(IFS.core.mirrorElements.getSourceText(textElement[0])).toBe("test text");

        // test element with value
        var valueElement = jQuery('<input type="text" value="test value"/>');
        expect(IFS.core.mirrorElements.getSourceText(valueElement[0])).toBe("test value");

        // empty element
        expect(IFS.core.mirrorElements.getSourceText('')).toBe("");
    })
})