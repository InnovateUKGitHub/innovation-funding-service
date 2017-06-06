IFS.competitionManagement.countSelected = (function () {
  'use strict'
  var s
  return {
    settings: {
      selectAllAttribute: 'data-count-selected'
    },
    init: function () {
      s = this.settings

      jQuery('[' + s.selectAllAttribute + ']').each(function () {
        var countEl = jQuery(this)
        var selector = IFS.competitionManagement.countSelected.getSelector(countEl)

        var currentCount = countEl.data('countSelectedInitial')

        if (typeof currentCount !== 'number') {
          currentCount = IFS.competitionManagement.countSelected.countChecked(selector)
        }

        IFS.competitionManagement.countSelected.setCount(countEl, currentCount)

        jQuery('body').on('change', selector, function () {
          jQuery(this).prop('checked') ? currentCount++ : currentCount--

          IFS.competitionManagement.countSelected.setCount(countEl, currentCount)
        })
      })
    },
    getSelector: function (el) {
      return el.attr(s.selectAllAttribute)
    },
    countChecked: function (selector) {
      return jQuery(selector + ':checked').length
    },
    setCount: function (el, count) {
      return el.text(count)
    }
  }
})()
