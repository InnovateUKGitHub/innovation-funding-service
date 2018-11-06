describe('test form validation', () => {
	beforeAll(() => {
		// import IFS module
		IFS = {"core" : {}}
		require("./formValidation")
	})
	test("check phone regex", () => {
		expect(IFS.core.formValidation.validateTelRegex("0123676767")).toBeTruthy()
		expect(IFS.core.formValidation.validateTelRegex("banana")).toBeFalsy()
	})
	test("hash function to work", () => {
		expect(IFS.core.formValidation.removeHash("#test")).toBe("test")
		expect(IFS.core.formValidation.removeHash("test2")).toBe("test2")
		expect(IFS.core.formValidation.removeHash("te#st")).toBe("te#st")
	})
})
