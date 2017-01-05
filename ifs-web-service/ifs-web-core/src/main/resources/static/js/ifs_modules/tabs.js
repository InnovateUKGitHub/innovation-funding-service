//  Simple ARIA tab interface original code by @Heydonworks altered by Worth Systems
// -----------------------------------------------------------------------------
IFS.core.tabs = (function () {
  'use strict'
  var settings // private alias to settings

  return {
    settings: {
      tabContainer: '.tab-interface'
    },
    init: function () {
      console.log('tabs init')
      settings = this.settings
      IFS.core.tabs.initTabHTML()
      // Change focus between tabs with arrow keys
      $('body').on('keydown', '[role="tab"]', function (e) {
        IFS.core.tabs.changeFocus(e, $(this))
      })
      // Handle click on tab to show + focus tabpanel
      $('body').on('click', '[role="tab"]', function (e) {
        IFS.core.tabs.handleClick(e, $(this))
      })
    },
    initTabHTML: function() {
      // The setup
      $(settings.tabContainer +' > ul').attr('role','tablist').show()
      $(settings.tabContainer +' [role="tablist"] li').attr('role','presentation')
      $(settings.tabContainer +' section div').removeAttr('aria-hidden')
      $('[role="tablist"] a').attr({
        'role' : 'tab',
        'tabindex' : '-1'
      })

      // Make each aria-controls correspond id of targeted section (re href)
      $('[role="tablist"] a').each(function() {
        $(this).attr(
          'aria-controls', $(this).attr('href').substring(1)
        )
      })

      // Make the first tab selected by default and allow it focus
      $('[role="tablist"] li:first-child a').attr({
        'aria-selected' : 'true',
        'tabindex' : '0'
      })

      // Make each section focusable and give it the tabpanel role
      $(settings.tabContainer +' section').attr({
        'role' : 'tabpanel'
      })

      // Make first child of each panel focusable programmatically
      $(settings.tabContainer +' section > *:first-child').attr({
        'tabindex' : '0'
      })

      // Make all but the first section hidden (ARIA state and display CSS)
      $('[role="tabpanel"]:not(:first-of-type)').attr({
        'aria-hidden' : 'true'
      })
    },
    changeFocus: function(e, element) {
      // define current, previous and next (possible) tabs
      var $original = $(element)
      var tabsLength = $(element).parents('li').siblings().length ++
      var $prev = $(element).parents('li').index('li') === 0 ? $(element).parents('li').siblings().eq(tabsLength - 1).children('[role="tab"]') : $(element).parents('li').prev().children('[role="tab"]')
      var $next = $(element).parents('li').index('li') === tabsLength ? $(element).parents('li').siblings().eq(0).children('[role="tab"]') : $(element).parents('li').next().children('[role="tab"]')
      var $target
      // find the direction (prev or next)
      switch (e.keyCode) {
        case 37:
          $target = $prev
          break
        case 38:
          e.preventDefault()
          $target = $prev
          break
        case 39:
          $target = $next
          break
        case 40:
          e.preventDefault()
          $target = $next
          break
        default:
          $target = false
          break
      }

      if ($target.length) {
        $original.attr({
          'tabindex' : '-1',
          'aria-selected' : null
        })
        $target.attr({
          'tabindex' : '0',
          'aria-selected' : true
        }).focus()
      }

      // Hide panels
      $(settings.tabContainer +' [role="tabpanel"]').attr('aria-hidden', 'true')

      // Show panel which corresponds to target
      $('#' + $(document.activeElement).attr('href').substring(1)).attr('aria-hidden', null)
    },
    handleClick: function(e, element) {
      e.preventDefault()

      var instance = $(element)

      // remove focusability [sic] and aria-selected
      $('[role="tab"]').attr({
        'tabindex': '-1',
        'aria-selected' : null
      })

      // replace above on clicked tab
      instance.attr({
        'aria-selected' : true,
        'tabindex' : '0'
      })

      // Hide panels
      $(settings.tabContainer +' [role="tabpanel"]').attr('aria-hidden', 'true')

      // show corresponding panel
      $('#' + instance.attr('href').substring(1)).attr('aria-hidden', null)
    },
    destroy: function () {
      console.log('Tabs destroy')
    }
  }
})()
