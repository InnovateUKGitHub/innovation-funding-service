//  Simple ARIA tab interface original code by @Heydonworks altered by Worth Systems
// -----------------------------------------------------------------------------
IFS.core.tabs = (function () {
  'use strict'
  var settings // private alias to settings

  return {
    settings: {
      tabContainer: '.tabs'
    },
    init: function () {
      settings = this.settings
      IFS.core.tabs.initTabHTML()
      // Change focus between tabs with arrow keys
      jQuery('body').on('keydown', '[role="tab"]', function (e) {
        if (e.keyCode === 37 || e.keyCode === 38 || e.keyCode === 39 || e.keyCode === 40) {
          IFS.core.tabs.changeFocus(e, jQuery(this))
        }
      })
      // Handle click on tab to show + focus tabpanel
      jQuery('body').on('click', '[role="tab"]', function (e) {
        IFS.core.tabs.handleClick(e, jQuery(this))
      })
    },
    initTabHTML: function () {
      // The setup
      jQuery(settings.tabContainer + ' > ul').attr({
        'role': 'tablist',
        'aria-hidden': null
      }).show()
      jQuery(settings.tabContainer + ' [role="tablist"] li').attr('role', 'presentation')
      jQuery(settings.tabContainer + ' section div').removeAttr('aria-hidden')

      jQuery('[role="tablist"] a').attr({
        'role': 'tab',
        'tabindex': '-1'
      })

      // Make each aria-controls correspond id of targeted section (re href)
      jQuery('[role="tablist"] a').each(function () {
        var instance = jQuery(this)
        instance.prop('aria-controls', instance.prop('href').substring(1))
      })

      // Make each section focusable and give it the tabpanel role
      jQuery(settings.tabContainer + ' section').attr({
        'role': 'tabpanel'
      })

      // Make first child of each panel focusable programmatically
      jQuery(settings.tabContainer + ' section > *:first-child').attr({
        'tabindex': '0'
      })

      jQuery(settings.tabContainer).each(function () {
        // do we have a tab set in the hash?
        var defaultIndex = 0
        // if (jQuery(this).attr('id')) {
        //   var parameter = IFS.core.tabs.getParameterByName(jQuery(this).attr('id'))
        //   if (parameter !== null && parameter !== '') {
        //     defaultIndex = parseInt(parameter) - 1
        //   }
        // }
        if (location.hash && jQuery(this).children().find('#' + window.location.hash.substring(1))) {
          defaultIndex = jQuery(this).find('#' + window.location.hash.substring(1)).index() - 1
        }

        // If we have a default index then set the right tab otherwise set it to 1
        jQuery(this).find('li').eq(defaultIndex).find('a').attr({
          'aria-selected': 'true',
          'tabindex': '0'
        })
        // Make all but the first section hidden (ARIA state and display CSS)
        jQuery(this).find('[role="tabpanel"]').not(':eq(' + defaultIndex + ')').attr({
          'aria-hidden': 'true'
        })
      })
    },
    changeFocus: function (e, element) {
      // define current, previous and next (possible) tabs
      var original = jQuery(element)
      var originalParent = original.parent()
      var tabsLength = originalParent.siblings().length ++
      var prev = originalParent.index() === 0 ? originalParent.siblings().eq(tabsLength - 1).children('[role="tab"]') : originalParent.prev().children('[role="tab"]')
      var next = originalParent.index() === tabsLength ? originalParent.siblings().eq(0).children('[role="tab"]') : originalParent.next().children('[role="tab"]')
      var target
      // find the direction (prev or next)
      switch (e.keyCode) {
        case 37:
          target = prev
          break
        case 38:
          e.preventDefault()
          target = prev
          break
        case 39:
          target = next
          break
        case 40:
          e.preventDefault()
          target = next
          break
        default:
          target = false
          break
      }

      if (target.length) {
        original.attr({
          'tabindex': '-1',
          'aria-selected': null
        })
        target.attr({
          'tabindex': '0',
          'aria-selected': true
        }).focus()
      }

      // Hide panels
      original.parents(settings.tabContainer).find('[role="tabpanel"]').attr('aria-hidden', 'true')

      // Show panel which corresponds to target
      original.parents(settings.tabContainer).find('#' + jQuery(document.activeElement).attr('href').substring(1)).attr('aria-hidden', null)

      // append id to query string
      // this.handleQueryString(target)
    },
    handleClick: function (e, element) {
      e.preventDefault()

      var instance = jQuery(element)

      // remove focusability [sic] and aria-selected
      instance.parents('[role="tablist"]').find('[role="tab"]').attr({
        'tabindex': '-1',
        'aria-selected': null
      })

      // replace above on clicked tab
      instance.attr({
        'aria-selected': true,
        'tabindex': '0'
      })

      // Hide panels
      instance.parents(settings.tabContainer).find('[role="tabpanel"]').attr('aria-hidden', 'true')

      // show corresponding panel
      instance.parents(settings.tabContainer).find('#' + instance.attr('href').substring(1)).attr('aria-hidden', null)

      // append id to query string
      // this.handleQueryString(instance)
    },
    // handleQueryString: function (instance) {
    //   var index = instance.parent().index()
    //   var uniqueID = instance.parents('div').attr('id')
    //   var oldURL = document.location
    //   var newURL = this.updateQueryStringParameter(oldURL.toString(), uniqueID, index + 1)
    //   var title = document.getElementsByTagName('title')[0].innerHTML
    //   window.history.replaceState({}, title, newURL)
    // },
    // updateQueryStringParameter: function (uri, key, value) {
    //   var re = new RegExp('([?&])' + key + '=.*?(&|#|$)', 'i')
    //   if (value === undefined) {
    //     if (uri.match(re)) {
    //       return uri.replace(re, '$1$2')
    //     } else {
    //       return uri
    //     }
    //   } else {
    //     if (uri.match(re)) {
    //       return uri.replace(re, '$1' + key + '=' + value + '$2')
    //     } else {
    //       var hash = ''
    //       if (uri.indexOf('#') !== -1) {
    //         hash = uri.replace(/.*#/, '#')
    //         uri = uri.replace(/#.*/, '')
    //       }
    //       var separator = uri.indexOf('?') !== -1 ? '&' : '?'
    //       return uri + separator + key + '=' + value + hash
    //     }
    //   }
    // },
    // getParameterByName: function (name, url) {
    //   if (!url) {
    //     url = window.location.href
    //   }
    //   name = name.replace(/[[\]]/g, '$&')
    //   // name = name.replace(/[\[\]]/g, '\\$&')
    //   var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)')
    //   var results = regex.exec(url)
    //   if (!results) return null
    //   if (!results[2]) return ''
    //   return decodeURIComponent(results[2].replace(/\+/g, ' '))
    // },
    destroy: function () {
      // Unbind events
      jQuery('body').off('keydown', '[role="tab"]')
      jQuery('body').off('click', '[role="tab"]')

      // Remove roles and attributes
      jQuery('[role="tablist"] a').removeAttr('role tabindex aria-controls aria-selected').attr({
        'aria-hidden': true
      })
      jQuery(settings.tabContainer + ' [role="tablist"] li').removeAttr('role')
      jQuery(settings.tabContainer + ' > ul').removeAttr('role').hide()
      jQuery(settings.tabContainer + ' section > *:first-child').removeAttr('tabindex')
      jQuery(settings.tabContainer + ' section div').removeAttr('tabindex')
      jQuery(settings.tabContainer + ' section').removeAttr('role aria-hidden')
    }
  }
})()
