IFS.competitionManagement.copyLink = (function () {
  'use strict'
  var s
  return {
    settings: {
      copyLinkAttribute: 'data-copy-link',
      supportsClipboardData: window.clipboardData && window.clipboardData.setData,
      supportsQueryCommandCopy: document.queryCommandSupported && document.queryCommandSupported('copy')
    },
    init: function () {
      s = this.settings
      if (s.supportsClipboardData || s.supportsQueryCommandCopy) {
        var copyLink = '[' + s.copyLinkAttribute + ']'
        jQuery(copyLink).append('<button type="button" class="buttonlink">Copy link</button>')
        jQuery('body').on('click', copyLink + ' button', function (e) {
          var text = jQuery(this).prev().text()
          if (text.length) {
            IFS.competitionManagement.copyLink.copyToClipboard(text)
          }
        })
      }
    },
    copyToClipboard: function (text) {
      s = this.settings

      if (s.supportsClipboardData) {
        // IE specific code path to prevent textarea being shown while dialog is visible.
        return window.clipboardData.setData('Text', text)
      } else if (s.supportsQueryCommandCopy) {
        var textArea = jQuery('<textarea class="visuallyhidden"></textarea>').val(text)
        jQuery('body').append(textArea)
        textArea.select()
        try {
          return document.execCommand('copy')  // Security exception may be thrown by some browsers.
        } catch (err) {
          return false
        } finally {
          textArea.remove()
        }
      }
    }
  }
})()
