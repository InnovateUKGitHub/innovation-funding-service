IFS.core.copyLink = (function () {
  'use strict'
  var s
  return {
    settings: {
      copyLinkEl: '.copy-link'
    },
    init: function () {
      s = this.settings
      if ((window.clipboardData && window.clipboardData.setData) || (document.queryCommandSupported && document.queryCommandSupported('copy'))) {
        jQuery(s.copyLinkEl).attr({
          'aria-hidden': null
        }).show()
        jQuery('body').on('click', s.copyLinkEl, function (e) {
          e.preventDefault()
          var text = jQuery(this).prev().text()
          IFS.core.copyLink.copyToClipboard(text)
        })
      }
    },
    copyToClipboard: function (text) {
      s = this.settings

      if (window.clipboardData && window.clipboardData.setData) {
        // IE specific code path to prevent textarea being shown while dialog is visible.
        return window.clipboardData.setData('Text', text)
      } else if (document.queryCommandSupported && document.queryCommandSupported('copy')) {
        var textArea = jQuery('<textarea></textarea>').addClass('copy-link-textarea').val(text)
        jQuery('body').append(textArea)
        textArea.select()
        try {
          return document.execCommand('copy')  // Security exception may be thrown by some browsers.
        } catch (err) {
          console.warn('Copy to clipboard failed.', err)
          return false
        } finally {
          jQuery('.copy-link-textarea').remove()
        }
      }
    }
  }
})()
