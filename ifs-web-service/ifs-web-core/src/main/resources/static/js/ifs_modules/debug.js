IFS.core.debug = (function () {
  'use strict'
  return {
    init: function () {
      if ((window.location.hostname === 'ifs.local-dev' && window.location.port !== '4000') && window.location.pathname.indexOf('/prototypes') === -1) {
        jQuery(document).on('dblclick', '#footer', function () {
          IFS.core.debug.toggleDebug()
        })

        jQuery(document).on('keyup', '[data-selector-highlighter] code', function () {
          var query = jQuery(this).text()
          IFS.core.debug.resetStyles()
          IFS.core.debug.highlightSelected(query)
        })

        if (typeof (Cookies.get('debugSelectors')) !== 'undefined') {
          IFS.core.debug.enableDebug()
        }
        // red a11y borders
        jQuery('head').append('<style>a[href="#"],a[href=""],a[href*="prototypes"]{ outline:5px solid red !important; }</style>')
      }
    },
    toggleDebug: function () {
      if (jQuery('[data-selector-highlighter]').length) {
        IFS.core.debug.disableDebug()
      } else {
        IFS.core.debug.enableDebug()
      }
    },
    enableDebug: function () {
      Cookies.set('debugSelectors', 'true', { expires: 0.05 }) // defined in days, 0.05 = little bit more than one hour
      jQuery('body').append('<div data-selector-highlighter=""><code contenteditable></code><div></div">')
      jQuery('[data-selector-highlighter] code').focus()
    },
    disableDebug: function () {
      Cookies.remove('debugSelectors')
      jQuery('[data-selector-highlighter]').remove()
      IFS.core.debug.resetStyles()
    },
    highlightSelected: function (query) {
      var result = jQuery('[data-selector-highlighter] div')
      query = jQuery.trim(query)
      if (query.length) {
        try {
          var el = jQuery(query)
          if (el.length) {
            jQuery(el).css('outline', '5px solid red')
            var resultText = el.length > 1 ? el.length + ' matches' : '1 match'
            result.text(resultText)
          } else {
            // valid selector no match
            result.text('no match')
          }
        } catch (err) {
          // invalid selector
          result.text('no match')
        }
      } else {
        // empty string
        result.text('no match')
        jQuery('code').text(' ')
      }
    },
    resetStyles: function () {
      jQuery('[style*="outline"]').removeAttr('style')
    }
  }
})()
