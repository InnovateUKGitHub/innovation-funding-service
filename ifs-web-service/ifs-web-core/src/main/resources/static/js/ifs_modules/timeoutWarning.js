IFS.core.timeoutWarning = (function () {
  'use strict'
  var s // private alias to settings
  var sessionTimer
  var sessionCountdown
  var inactivityTimer
  var inactivityCountdown
  return {
    settings: {
      sessionTimeoutLength: 8, // in hours
      sessionWarningLength: 5, // in minutes
      inactivityTimeoutLength: 60, // in minutes
      inactivityWarningLength: 5, // in minutes
      cancelTimeout: '[data-stay-signed-in]'
    },
    init: function () {
      s = this.settings
      var domain = IFS.core.timeoutWarning.getDomain()
      if (Cookies.get('CSRF-TOKEN') && domain !== 'ifs.local-dev') {
        // we are logged in so start the inactivity timer
        inactivityCountdown = (s.inactivityTimeoutLength - s.inactivityWarningLength) * 60000 // minutes * milliseconds
        IFS.core.timeoutWarning.startInactivityTimer()
        jQuery('body').on('click', s.cancelTimeout, function () {
          IFS.core.timeoutWarning.startInactivityTimer()
          IFS.core.timeoutWarning.processAjax()
        })
        if (Cookies.get('SESSION-TIMEOUT')) {
          // we have a session timeout cookie, start the timer
          IFS.core.timeoutWarning.startSessionTimer()
        } else {
          // its the first time on a logged in page so set session timeout cookie
          var date = new Date()
          date.setHours(date.getHours() + s.sessionTimeoutLength)
          Cookies.set('SESSION-TIMEOUT', date, { path: '/', domain: domain, expires: 0.5 })
          // and start the session timer
          IFS.core.timeoutWarning.startSessionTimer()
        }
        jQuery(document).ajaxComplete(function () {
          IFS.core.timeoutWarning.startInactivityTimer()
        })
      } else {
        // logged out so delete the session timeout cookie
        Cookies.remove('SESSION-TIMEOUT', { path: '/', domain: domain })
      }
    },
    startSessionTimer: function () {
      // start session countdown
      var sessionTimeout = new Date(Cookies.get('SESSION-TIMEOUT'))
      var now = new Date()
      var difference = sessionTimeout.getTime() - now.getTime()
      var differenceInMinutes = Math.round(difference / 60000)
      sessionCountdown = (differenceInMinutes - s.sessionWarningLength) * 60000 // minutes * milliseconds
      clearTimeout(sessionTimer)
      sessionTimer = setTimeout(function () {
        IFS.core.timeoutWarning.fireWarning('.modal-session-timeout-warning')
      }, sessionCountdown)
    },
    startInactivityTimer: function () {
      clearTimeout(inactivityTimer)
      inactivityTimer = setTimeout(function () {
        IFS.core.timeoutWarning.fireWarning('.modal-inactivity-timeout-warning')
      }, inactivityCountdown)
    },
    fireWarning: function (modalDialog) {
      var dialog = jQuery(modalDialog)
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
      if (Cookies.get('CSRF-TOKEN')) {
        // if we are logged in send and ajax request to keep us logged in
        jQuery.ajaxProtected({
          url: '/',
          success: function () {
            IFS.core.timeoutWarning.closeModal()
          }})
      } else {
        // if we are not logged in refresh the page to send us to the login page
        window.location.reload()
      }
    },
    closeModal: function () {
      jQuery('[role="dialog"],.modal-overlay').attr('aria-hidden', 'true')
    },
    getDomain: function () {
      var host = window.location.hostname
      var subDomains = host.split('.')
      var domain = subDomains.slice(-2)
      if (subDomains.length > 2) {
        return domain[0] + '.' + domain[1]
      } else {
        return host // this for local development
      }
    }
  }
})()
