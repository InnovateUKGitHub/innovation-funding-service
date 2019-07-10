console.log('Loaded')
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
      sessionWarningLength: 1, // in minutes 5
      inactivityTimeoutLength: 60, // in minutes
      inactivityWarningLength: 5, // in minutes
      cancelTimeout: '[data-stay-signed-in]'
    },
    init: function () {
      s = this.settings
      console.log(Cookies.get('CSRF-TOKEN'))
      if (Cookies.get('CSRF-TOKEN')) {
        // we are logged in so start the inactivity timer
        console.log('Logged in')
        inactivityCountdown = (s.inactivityTimeoutLength - s.inactivityWarningLength) * 60000 // minutes * milliseconds
        IFS.core.timeoutWarning.startInactivityTimer()
        jQuery('body').on('click', s.cancelTimeout, function () {
          IFS.core.timeoutWarning.startInactivityTimer()
          IFS.core.timeoutWarning.processAjax()
        })
        if (Cookies.get('SESSION-TIMEOUT')) {
          // we have a session timeout cookie, start the timer
          console.log('Session cookie')
          console.log(Cookies.get('SESSION-TIMEOUT'))
          IFS.core.timeoutWarning.startSessionTimer()
        } else {
          console.log('No session cookie')
          // its the first time on a logged in page so set session timeout cookie
          var date = new Date()
          date.setHours(date.getHours() + s.sessionTimeoutLength)
          var domain = IFS.core.timeoutWarning.getDomain()
          console.log(date)
          console.log(domain)
          Cookies.set('SESSION-TIMEOUT', date, { path: '/', domain: domain })
          // and start the session timer
          IFS.core.timeoutWarning.startSessionTimer()
        }
      } else {
        console.log('Logged out')
        // logged out so delete the session timeout cookie
        console.log(Cookies.get('SESSION-TIMEOUT'))
        Cookies.remove('SESSION-TIMEOUT', { path: '/' })
      }
    },
    startSessionTimer: function () {
      console.log('Start countdown')
      // start session countdown
      var sessionTimeout = new Date(Cookies.get('SESSION-TIMEOUT'))
      var now = new Date()
      var difference = sessionTimeout.getTime() - now.getTime()
      var differenceInMinutes = Math.round(difference / 60000)
      console.log(differenceInMinutes)
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
      jQuery.ajaxProtected({
        url: '/',
        success: function () {
          console.log('Ajax done')
          IFS.core.timeoutWarning.closeModal()
        }})
    },
    closeModal: function () {
      jQuery('[role="dialog"],.modal-overlay').attr('aria-hidden', 'true')
    },
    getDomain: function () {
      var host = window.location.host
      var subDomains = host.split('.')
      if (subDomains.length > 2) {
        var domain = subDomains.slice(-2)
        return domain[0] + '.' + domain[1]
      } else {
        return host
      }
      // ifs-uat.apps.org-env-0.org.innovateuk.ukri.org
      // auth-ifs-uat.apps.org-env-0.org.innovateuk.ukri.org
    }
  }
})()
