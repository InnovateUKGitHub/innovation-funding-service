IFS.core.mirrorElements = (function () {
  'use strict'
  return {
    init: function () {
      jQuery('[data-mirror]').each(function () {
        var element = jQuery(this)
        var source = element.attr('data-mirror')

        if (!element.data('mirror-bound')) {
          IFS.core.mirrorElements.updateElement(element, source)
          IFS.core.mirrorElements.bindMirrorElement(element, source)
        }
        element.data('mirror-bound', true)
      })
    },
    bindMirrorElement: function (element, source) {
      jQuery(document).on('change', source, function () {
        IFS.core.mirrorElements.updateElement(element, this)
      })
    },
    updateElement: function (element, source) {
      var sourceText = IFS.core.mirrorElements.getSourceText(source)
      if (element.is('input')) {
        element.val(sourceText)
      } else {
        element.text(sourceText)
      }
    },
    getSourceText: function (element) {
      var sourceEl = jQuery(element)
      if (sourceEl.length === 1) {
        if (sourceEl.is(':input')) {
          return sourceEl.val() === null ? '' : sourceEl.val()
        } else if (sourceEl.text().length) {
          return sourceEl.text()
        }
      }
      return ''
    }
  }
})()
