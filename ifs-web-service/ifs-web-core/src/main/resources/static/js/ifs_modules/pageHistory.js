IFS.core.pageHistory = (function () {
  'use strict'
  return {
    init: function () {
      var pageHistory = JSON.parse(Cookies.get('pageHistory'))
      var pageTitleOverride = jQuery('.page-history-title-override')
      var name = jQuery('h1:first').clone().children().remove().end().text().trim()
      if (pageTitleOverride.length) {
        name = pageTitleOverride.val()
      }
      if (pageHistory[0].uri.indexOf(window.location.pathname) !== -1) {
        pageHistory[0].name = name
        Cookies.remove('pageHistory')
        Cookies.set('pageHistory', JSON.stringify(pageHistory))
      }
    }
  }
})()
