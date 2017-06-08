IFS.core.copyLink = (function () {
  'use strict'
  var s
  return {
    settings: {
      copyLinkEl: '.copy-link'
    },
    init: function () {
      s = this.settings
      jQuery('body').on('click', s.copyLinkEl, function (e) {
        IFS.core.wordCount.copyLinkToClipboard()
      })
    },
    copyLinkToClipboard: function (textarea) {
      s = this.settings
      var url = jQuery(s.copyLinkEl).pre().text()

    }
  }
})()
