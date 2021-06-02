IFS.core.hashScroll = (function () {
  'use strict'
  return {
    init: function () {
      if (window.location.hash) {
        var item = jQuery(window.location.hash)
        if (item.length) {
          var accordionSection = item.closest('.govuk-accordion__section:not(.govuk-accordion__section--expanded')
          if (accordionSection.length) {
            accordionSection.addClass('govuk-accordion__section--expanded')
            IFS.core.formValidation.scrollToElement(item.first())
          }
        }
      }
    }
  }
})()
