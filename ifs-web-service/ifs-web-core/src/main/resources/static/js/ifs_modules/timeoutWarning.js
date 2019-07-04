console.log('Loaded')
IFS.core.timeoutWarning = (function () {
  'use strict'
  var s // private alias to settings
  var timer
  return {
    settings: {
      timeout: 2, // in minutes
      warning: 1 // in minutes
    },
    init: function () {
      this.startTimer()
      jQuery('body').on('mousemove', function () {
        IFS.core.timeoutWarning.startTimer()
      })
      jQuery('body').on('keypress', function () {
        IFS.core.timeoutWarning.startTimer()
      })
    },
    startTimer: function () {
      s = this.settings
      clearTimeout(timer)
      var countdown = ((s.timeout - s.warning) * 60) * 100 // minutes * seconds * milliseconds
      timer = setTimeout(function () {
        console.log('Hello')
        IFS.core.timeoutWarning.fireWarning()
      }, countdown)
    },
    fireWarning: function () {
      var dialog = jQuery('.modal-timeout-warning')
      if (!jQuery('body').find('.modal-overlay').length) {
        jQuery('body').append('<div class="modal-overlay js-close"></div>')
      } else {
        jQuery('.modal-overlay').attr('aria-hidden', 'false')
      }
      setTimeout(function () {
        var height = dialog.outerHeight()
        dialog.css({'margin-top': '-' + (height / 2) + 'px'}).attr('aria-hidden', 'false')
      }, 50)
    }
  }
})()
