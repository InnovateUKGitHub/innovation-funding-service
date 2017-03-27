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
        var count = IFS.competitionManagement.countSelected.countChecked(selector)
        IFS.competitionManagement.countSelected.setCount(countEl, count)

        jQuery('body').on('change', selector, function () {
          var count = IFS.competitionManagement.countSelected.countChecked(selector)
          IFS.competitionManagement.countSelected.setCount(countEl, count)
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
