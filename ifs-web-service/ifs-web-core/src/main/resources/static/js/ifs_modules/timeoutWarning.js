IFS.core.timeoutWarning = (function () {
  'use strict'
  var s // private alias to settings
  var inactivityTimer
  var inactivityCountdown
  return {
    settings: {
      timeoutLength: 480, // in minutes
      warningLength: 5, // in minutes
      inactivityTimeoutLength: 60, // in minutes
      inactivityWarningLength: 5, // in minutes
      cancelTimeout: '[data-stay-signed-in]'
    },
    init: function () {
      s = this.settings
      inactivityCountdown = (s.inactivityTimeoutLength - s.inactivityWarningLength) * 60000 // minutes * milliseconds
      this.startTimer()
      jQuery('body').on('click', s.cancelTimeout, function () {
        IFS.core.timeoutWarning.startTimer()
        IFS.core.timeoutWarning.processAjax()
      })
    },
    // getCookieExpiry: function () {
    //   console.log(Cookies.get('CSRF-TOKEN'))
    // },
    startTimer: function () {
      clearTimeout(inactivityTimer)
      inactivityTimer = setTimeout(function () {
        IFS.core.timeoutWarning.fireWarning()
      }, inactivityCountdown)
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
        dialog.find('.govuk-button').focus()
      }, 50)
    },
    processAjax: function () {
      jQuery.ajaxProtected({
        url: '/',
        success: function () {
          console.log('Ajax done')
          IFS.core.timeoutWarning.closeModal()
        }})
    },
    closeModal: function () {
      jQuery('[role="dialog"],.modal-overlay').attr('aria-hidden', 'true')
    }
  }
})()
