describe('test wordCount', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./wordCount")
    })

    test("test updateWordCount", () => {
        // a template for quickly creating different size textareas
        var textAreaWrapperTemplate = `<div class="word-count">
            <textarea data-max_words="<MAX>" data-maxwordslength="<MAX>" data-maxwordslength-errormessage="TEST1234">
                <TEXT>
            </textarea>
            <span class="count-label">
                <span class="count-down positive"></span>
            </span>
        </div>`

        // max = 10, 2 words, error message = "Words remaining: 8"
        var textAreaWrapper = jQuery(textAreaWrapperTemplate.replace("<MAX>", "10").replace("<TEXT>", "test test"));
        var textarea = textAreaWrapper.find('textarea')                
        IFS.core.wordCount.updateWordCount(textarea[0])
        expect(textAreaWrapper.find('.count-down').text()).toBe("Words remaining: 8")
        expect(textAreaWrapper.find('.count-down').hasClass("negative")).toBe(false)

        // max = 7, 3 words, error message = "Words remaining: 4"
        var textAreaWrapper = jQuery(textAreaWrapperTemplate.replace("<MAX>", "7").replace("<TEXT>", "a-b test test"));
        var textarea = textAreaWrapper.find('textarea')
        IFS.core.wordCount.updateWordCount(textarea[0])
        expect(textAreaWrapper.find('.count-down').text()).toBe("Words remaining: 4")
        expect(textAreaWrapper.find('.count-down').hasClass("negative")).toBe(false)

        // max = 5, 8 words, error message = "Words remaining: -3" class becomes negative
        var textAreaWrapper = jQuery(textAreaWrapperTemplate.replace("<MAX>", "5").replace("<TEXT>", "a b c d e f g h"));
        var textarea = textAreaWrapper.find('textarea')
        IFS.core.wordCount.updateWordCount(textarea[0])
        expect(textAreaWrapper.find('.count-down').text()).toBe("Words remaining: -3")
        expect(textAreaWrapper.find('.count-down').hasClass("negative")).toBe(true)

    })

})