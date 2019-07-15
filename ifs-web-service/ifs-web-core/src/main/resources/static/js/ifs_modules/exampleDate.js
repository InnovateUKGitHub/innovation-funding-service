IFS.core.exampleDate = (function () {
  'use strict'
  var s
  return {
    settings: {
      exampleDateEl: '[data-example-date]'
    },
    init: function () {
      s = this.settings
      var date = new Date()
      date.setMonth(date.getMonth() + 3)
      var day = date.getDate()
      var month = date.getMonth()
      month += 1 // JavaScript months are 0-11
      var year = date.getFullYear()
      jQuery(s.exampleDateEl).text(day + ' ' + month + ' ' + year)
    }
  }
})()
