describe('test sortErrors', () => {
    beforeAll(() => {
        // import IFS module
        IFS = {"core" : {}}
        jQuery = require('jquery');
        require("./sortErrors")
    })

    test("test getHashFromElement", () => {
        var noHashError = jQuery(`<li>empty error</li>`)[0]
        expect(IFS.core.sortingErrors.getHashFromElement(noHashError)).toBe(null)

        var hashError = jQuery(`<li> <a href="#testHash">Has a hash</a></li>`)[0]
        expect(IFS.core.sortingErrors.getHashFromElement(hashError)).toBe("#testHash")
    })

    test("test compareHash", () => {

        // create an ordering for sorting by (not alphabetical)
        var ordering = ["#one", "#two", "#three"]

        // create a set of errors to compare
        var noHash = jQuery(`<li>empty error</li>`)[0]
        var oneHashA = jQuery(`<li> <a href="#one">A hash</a></li>`)[0]
        var oneHashB = jQuery(`<li> <a href="#one">B hash</a></li>`)[0]
        var oneHashBidentical = jQuery(`<li class="irrelavent"> <a href="#one">B hash</a></li>`)[0]
        var twoHash = jQuery(`<li> <a href="#two">A hash</a></li>`)[0]
        var threeHash = jQuery(`<li> <a href="#three">A A hash</a></li>`)[0]
        var missingHash = jQuery(`<li> <a href="#missing">missing</a></li>`)[0]

        // sanity checks if A < B then B > A, and A=A
        expect(
            IFS.core.sortingErrors.compareHash(noHash, oneHashA, ordering)
        ).toBeLessThan(0)
        expect(
            IFS.core.sortingErrors.compareHash(oneHashA, noHash, ordering)
        ).toBeGreaterThan(0)
        expect(
            IFS.core.sortingErrors.compareHash(oneHashA, oneHashA, ordering)
        ).toBe(0)

        // check we order by hashes, 1 < 2 < 3
        expect(
            IFS.core.sortingErrors.compareHash(oneHashA, twoHash, ordering)
        ).toBeLessThan(0)
        expect(
            IFS.core.sortingErrors.compareHash(twoHash, threeHash, ordering)
        ).toBeLessThan(0)

        // check that 'no hash' goes first, then errors with the hash not in the ordering, then ordered hashes
        expect(
            IFS.core.sortingErrors.compareHash(noHash, missingHash, ordering)
        ).toBeLessThan(0)
        expect(
            IFS.core.sortingErrors.compareHash(missingHash, oneHashA, ordering)
        ).toBeLessThan(0)

        // check we sort alphabetical when hashes match
        expect(
            IFS.core.sortingErrors.compareHash(oneHashA, oneHashB, ordering)
        ).toBeLessThan(0)

        // check other information doesn't change sort order.
        expect(
            IFS.core.sortingErrors.compareHash(oneHashBidentical, oneHashB, ordering)
        ).toBe(0)
    })
})