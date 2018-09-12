//  Progressive collapsibles original code by @Heydonworks altered by Worth Systems
// -----------------------------------------------------------------------------
IFS.core.collapsible = (function () {
  'use strict'
  var s // private alias to settings
  var index = 0

  return {
    settings: {
      collapsibleEl: '.collapsible',
      statelessClass: 'collapsible-stateless',
      expandedClass: 'collapsible-expanded'
    },
    init: function () {
      s = this.settings
      // if this has to be more dynamically updated in the future we can add a custom event
      jQuery(s.collapsibleEl).each(function () {
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
        var collapsibleSectionHeader = jQuery(this)

        // use provided id for a11y relationship, or create one if none is explicitly provided
        var explicitId = collapsibleSectionHeader.attr('data-collapsible-id')

        if (explicitId) {
          IFS.core.collapsible.initLoadstate(collapsibleSectionHeader, stateless, expanded,
            function () { return explicitId },
            function (id) { return IFS.core.collapsible.getLoadstateFromCookieByExplicitId(id) })
        } else {
          IFS.core.collapsible.initLoadstate(collapsibleSectionHeader, stateless, expanded,
            function () { return 'collapsible-' + index },
            function (id) { return IFS.core.collapsible.getLoadstateFromCookieByGeneratedId(id) })
        }

        index++
      })
    },
    initLoadstate: function (collapsibleSectionHeader, stateless, expanded, idFn, getLoadstateFromCookieFn) {
      var id = idFn()

      var showExpanded = (!stateless && getLoadstateFromCookieFn(id) != null) ? getLoadstateFromCookieFn(id) : expanded

      // wrap the content and make it focusable
      collapsibleSectionHeader.nextUntil('h2,h3').wrapAll('<div id="' + id + '" aria-hidden="' + !showExpanded + '">')

      // Add the button inside the <h2> so both the heading and button semantics are read
      collapsibleSectionHeader.wrapInner('<button aria-expanded="' + showExpanded + '" aria-controls="' + id + '" type="button">')
    },
    toggleCollapsible: function (el, stateless) {
      var inst = jQuery(el)
      var panel = jQuery('#' + inst.attr('aria-controls'))
      var state = inst.attr('aria-expanded') === 'false'
      // toggle the current
      inst.attr('aria-expanded', state)
      panel.attr('aria-hidden', !state)
      if (!stateless) {
        if (inst.parent().attr('data-collapsible-id')) {
          IFS.core.collapsible.setLoadStateInCookieByExplicitId(panel.attr('id'), state)
        } else {
          IFS.core.collapsible.setLoadStateInCookieByGeneratedId(panel.attr('id'), state)
        }
      }
    },
    getLoadstateFromCookieByExplicitId: function (id) {
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        var json = Cookies.getJSON('collapsibleStates')
        if (typeof (json[id]) !== 'undefined') {
          return json[id]
        } else {
          return null
        }
      }
      return null
    },
    setLoadStateInCookieByExplicitId: function (id, state) {
      var json = {}
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        json = Cookies.getJSON('collapsibleStates')
      }

      json[id] = state

      Cookies.set('collapsibleStates', json, { expires: 0.05 }) // defined in days, 0.05 = little bit more than one hour
    },
    getLoadstateFromCookieByGeneratedId: function (id) {
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        var json = Cookies.getJSON('collapsibleStates')
        var pathname = window.location.pathname
        if (typeof (json[pathname]) !== 'undefined') {
          if (typeof (json[pathname][id]) !== 'undefined') {
            return json[pathname][id]
          } else {
            return null
          }
        }
      }
      return null
    },
    setLoadStateInCookieByGeneratedId: function (id, state) {
      var json = {}
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        json = Cookies.getJSON('collapsibleStates')
      }
      var pathname = window.location.pathname
      if ((typeof (json[pathname]) === 'undefined')) {
        json[pathname] = {}
      }

      json[pathname][id] = state

      Cookies.set('collapsibleStates', json, { expires: 0.05 }) // defined in days, 0.05 = little bit more than one hour
    },
    destroy: function (type) {
      jQuery('body').off('click', s.collapsibleEl + ' > h2 > [aria-controls], ' + s.collapsibleEl + ' > h3 > [aria-controls]')
      jQuery(s.collapsibleEl + ' > h2, ' + s.collapsibleEl + ' > h3').each(function () {
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
