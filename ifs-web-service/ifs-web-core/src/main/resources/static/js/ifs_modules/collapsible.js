//  Progressive collapsibles original code by @Heydonworks altered by Worth Systems
// -----------------------------------------------------------------------------
IFS.core.collapsible = (function () {
  'use strict'
  var s // private alias to settings
  var index = 0

  return {
    settings: {
      collapsibleEl: '.collapsible',
      collapsibleTabs: '.tabs section',
      statelessClass: 'collapsible-stateless',
      expandedClass: 'collapsible-expanded'
    },
    init: function (type) {
      s = this.settings
      s.collapsible = type === 'tabs' ? s.collapsibleTabs : s.collapsibleEl
      // if this has to be more dynamically updated in the future we can add a custom event
      jQuery(s.collapsible).each(function () {
        var $el = jQuery(this)
        var stateless = $el.hasClass(s.statelessClass)
        var expanded = $el.hasClass(s.expandedClass)

        IFS.core.collapsible.initCollapsibleHTML(this, stateless, expanded)

        jQuery(this).on('click', 'h2 > [aria-controls], h3 > [aria-controls]', function () {
          IFS.core.collapsible.toggleCollapsible(this, stateless)
        })
      })
    },
    initCollapsibleHTML: function (el, stateless, expanded) {
      jQuery(el).children('h2,h3').each(function () {
        var inst = jQuery(this)
        var id = 'collapsible-' + index   // create unique id for a11y relationship
        // don't save state if we've asked it not to
        var showExpanded = (!stateless && IFS.core.collapsible.hasLoadstateFromCookie(id)) ? IFS.core.collapsible.getLoadstateFromCookie(id) : expanded

        // wrap the content and make it focusable
        inst.nextUntil('h2,h3').wrapAll('<div id="' + id + '" aria-hidden="' + !showExpanded + '">')

        // Add the button inside the <h2> so both the heading and button semantics are read
        inst.wrapInner('<button aria-expanded="' + showExpanded + '" aria-controls="' + id + '" type="button">')
        index++
      })
    },
    toggleCollapsible: function (el, stateless) {
      var inst = jQuery(el)
      var panel = jQuery('#' + inst.attr('aria-controls'))
      var state = inst.attr('aria-expanded') === 'false'
      // toggle the current
      inst.attr('aria-expanded', state)
      panel.attr('aria-hidden', !state)
      if (!stateless) {
        IFS.core.collapsible.setLoadStateInCookie(panel.attr('id'), state)
      }
    },
    getLoadstateFromCookie: function (index) {
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        var json = Cookies.getJSON('collapsibleStates')
        var pathname = window.location.pathname
        if (typeof (json[pathname]) !== 'undefined') {
          if (typeof (json[pathname][index]) !== 'undefined') {
            return json[pathname][index]
          }
        }
      }
      return false
    },
    hasLoadstateFromCookie: function (index) {
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        var json = Cookies.getJSON('collapsibleStates')
        var pathname = window.location.pathname
        return (typeof (json[pathname]) !== 'undefined') && (typeof (json[pathname][index]) !== 'undefined')
      }
      return false
    },
    setLoadStateInCookie: function (index, state) {
      var json = {}
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        json = Cookies.getJSON('collapsibleStates')
      }
      var pathname = window.location.pathname
      if ((typeof (json[pathname]) === 'undefined')) {
        json[pathname] = {}
      }

      json[pathname][index] = state

      Cookies.set('collapsibleStates', json, { expires: 0.05 }) // defined in days, 0.05 = little bit more than one hour
    },
    destroy: function (type) {
      s.collapsible = type === 'tabs' ? s.collapsibleTabs : s.collapsibleEl
      jQuery('body').off('click', s.collapsible + ' > h2 > [aria-controls], ' + s.collapsible + ' > h3 > [aria-controls]')
      jQuery(s.collapsible + ' > h2, ' + s.collapsible + ' > h3').each(function () {
        IFS.core.collapsible.destroyHTHL(this)
      })
    },
    destroyHTHL: function (el) {
      var inst = jQuery(el)
      inst.next().children(':first').unwrap()
      inst.find('button').contents().unwrap()
      // inst.removeAttr('role aria-expanded aria-controls')
    }
  }
})()
