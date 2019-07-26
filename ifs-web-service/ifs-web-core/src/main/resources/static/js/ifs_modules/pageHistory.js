IFS.core.pageHistory = (function () {
  'use strict'
  return {
    init: function () {
      var pageHistory = JSON.parse(Cookies.get('pageHistory'))
      var name = jQuery('h1:first').clone().children().remove().end().text().trim()
      pageHistory[0].name = name
      Cookies.remove('pageHistory')
      Cookies.set('pageHistory', JSON.stringify(pageHistory))
    }
  }
})()
