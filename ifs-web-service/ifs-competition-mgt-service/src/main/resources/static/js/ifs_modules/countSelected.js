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
        var $counterEl = jQuery(this)
        var checkboxSelector = IFS.competitionManagement.countSelected.getSelector($counterEl)
        var initialCount = IFS.competitionManagement.countSelected.getCount($counterEl)

        IFS.competitionManagement.countSelected.setCount($counterEl, initialCount)

        jQuery('body').on('change', checkboxSelector, function () {
          var currentCount = IFS.competitionManagement.countSelected.getCount($counterEl)

          jQuery(this).prop('checked') ? currentCount++ : currentCount--

          // Minimum count is 0
          currentCount = currentCount >= 0 ? currentCount : 0

          IFS.competitionManagement.countSelected.setCount($counterEl, currentCount)
        })
      })
    },
    getSelector: function ($counterEl) {
      return $counterEl.attr(s.selectAllAttribute)
    },
    getCount: function ($counterEl) {
      var count = $counterEl.data(this.settings.currentCountData)
      var checkboxSelector = this.getSelector($counterEl)

      if (typeof count !== 'number') {
        count = jQuery(checkboxSelector + ':checked').length
      }

      return count
    },
    setCount: function ($counterEl, count) {
      return $counterEl.data(this.settings.currentCountData, count).text(count)
    }
  }
})()
