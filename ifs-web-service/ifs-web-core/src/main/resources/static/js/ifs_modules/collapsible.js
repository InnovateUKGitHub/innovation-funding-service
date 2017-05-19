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
      statelessClass: 'collapsible-stateless'
    },
    init: function (type) {
      s = this.settings
      s.collapsible = type === 'tabs' ? s.collapsibleTabs : s.collapsibleEl
      // if this has to be more dynamically updated in the future we can add a custom event
      jQuery(s.collapsible).each(function () {
        var stateless = jQuery(this).hasClass(s.statelessClass)
        IFS.core.collapsible.initCollapsibleHTML(this, stateless)
        jQuery(this).on('click', 'h2 > [aria-controls], h3 > [aria-controls]', function () {
          IFS.core.collapsible.toggleCollapsible(this, stateless)
        })
      })
    },
    initCollapsibleHTML: function (el, stateless) {
      var inst = jQuery(el).find('h2,h3')
      var id = 'collapsible-' + index   // create unique id for a11y relationship
       // don't save state if we've asked it not to
      var loadstate = !stateless && IFS.core.collapsible.getLoadstateFromCookie(id)

        // wrap the content and make it focusable
      inst.nextUntil('h2,h3').wrapAll('<div id="' + id + '" aria-hidden="' + !loadstate + '">')

        // Add the button inside the <h2> so both the heading and button semantics are read
      inst.wrapInner('<button aria-expanded="' + loadstate + '" aria-controls="' + id + '" type="button">')
      index++
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
    setLoadStateInCookie: function (index, state) {
      var json = {}
      if (typeof (Cookies.getJSON('collapsibleStates')) !== 'undefined') {
        json = Cookies.getJSON('collapsibleStates')
      }
      var pathname = window.location.pathname
      if ((typeof (json[pathname]) === 'undefined')) {
        json[pathname] = {}
      }
      if (state === true) {
        json[pathname][index] = state
      } else if (typeof (json[pathname][index]) !== 'undefined') {
        // removing of false and empty objects from the json object as we store this in a cookie,
        // only == true will be opened on pageload so those are the only ones we have to store
        delete json[pathname][index]

        // options other than looping over for getting the object count break in ie8
        var count = 0
        jQuery.each(json[pathname], function () {
          count++
        })
        if (count === 0) {
          delete (json[pathname])
        }
      }
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
