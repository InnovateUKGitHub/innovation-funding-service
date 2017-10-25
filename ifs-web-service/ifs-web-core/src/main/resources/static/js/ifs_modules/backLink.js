//  Simple JS back link
// -----------------------------------------------------------------------------
IFS.core.backLink = (function () {
  'use strict'
  var settings // private alias to settings

  return {
    settings: {
      element: '[data-javascript-back-link]'
    },
    init: function () {
      settings = this.settings
      var element = jQuery(settings.element)
      var linkTitle = element.data('javascript-back-link')
      element.replaceWith('<button class="link-back" data-javascript-back-link>' + linkTitle + '</button>')
      // Handle click on back link
      jQuery('body').on('click', settings.element, function () {
        window.history.back()
      })
    }
  }
})()
