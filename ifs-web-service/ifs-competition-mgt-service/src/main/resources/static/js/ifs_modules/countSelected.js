IFS.competitionManagement.countSelected = (function () {
  'use strict'
  var s
  return {
    settings: {
      selectAllAttribute: 'data-count-selected',
      maxCountData: 'countSelectedMax',
      currentCountData: 'countSelectedCurrent'
    },
    init: function () {
      s = this.settings

      jQuery('[' + s.selectAllAttribute + ']').each(function () {
        var $countEl = jQuery(this)
        var checkboxSelector = IFS.competitionManagement.countSelected.getSelector($countEl)
        var initialCount = IFS.competitionManagement.countSelected.getCurrentCount($countEl)

        IFS.competitionManagement.countSelected.setCount($countEl, initialCount)

        jQuery('body').on('change', checkboxSelector, function () {
          var currentCount = IFS.competitionManagement.countSelected.getCurrentCount($countEl)

          jQuery(this).prop('checked') ? currentCount++ : currentCount--

          // Minimum count is 0
          currentCount = currentCount >= 0 ? currentCount : 0

          IFS.competitionManagement.countSelected.setCount($countEl, currentCount)
        })
      })
    },
    getSelector: function (el) {
      return el.attr(s.selectAllAttribute)
    },
    getCurrentCount: function ($counterEl) {
      var count = $counterEl.data(this.settings.currentCountData)
      var checkboxSelector = this.getSelector($counterEl)

      if (typeof count !== 'number') {
        count = IFS.competitionManagement.countSelected.countChecked(checkboxSelector)
      }

      return count
    },
    countChecked: function (selector) {
      return jQuery(selector + ':checked').length
    },
    setCount: function (el, count) {
      return el.data(this.settings.currentCountData, count).text(count)
    }
  }
})()
