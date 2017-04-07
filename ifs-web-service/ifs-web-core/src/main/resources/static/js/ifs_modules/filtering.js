IFS.core.filtering = (function () {
  'use strict'
  return {
    with: function (settings) {
      jQuery(settings.elementsToHide).attr('aria-hidden', true)
      jQuery(settings.elementsToShow).attr('aria-hidden', false)
    }
  }
})()
