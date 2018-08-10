$(document).ready(function () {
  // Turn off jQuery animation
  jQuery.fx.off = true

	// Initialise all GOVUK javascript components
	window.GOVUKFrontend.initAll()
})

$(window).load(function () {
  // Only set focus for the error example pages
  if ($('.js-error-example').length) {
    // If there is an error summary, set focus to the summary
    if ($('.error-summary').length) {
      $('.error-summary').focus()
      $('.error-summary a').click(function (e) {
        e.preventDefault()
        var href = $(this).attr('href')
        $(href).focus()
      })
    } else {
      // Otherwise, set focus to the field with the error
      $('.error input:first').focus()
    }
  }
});
