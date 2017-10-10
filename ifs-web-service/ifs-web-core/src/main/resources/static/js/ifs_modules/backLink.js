//  Simple JS back link
// -----------------------------------------------------------------------------
IFS.core.backLink = (function () {
  'use strict'
  var settings // private alias to settings

  return {
    settings: {
      element: '[data-javascript-back-link="true"]'
    },
    init: function () {
      settings = this.settings

      if (document.referrer) {
        // Replace href with url from referrer
        jQuery(settings.element).attr('href', document.referrer)
      } else {
        // Handle click on back link
        jQuery('body').on('click', settings.element, function (e) {
          IFS.core.backLink.handleClick(e)
        })
      }
    },
    handleClick: function (event) {
      event.preventDefault()
      window.history.back()
    }
  }
})()
