IFS.core.template = (function () {
  'use strict'
  /*
      used to replace placeholders in a template for example:
      var template = '<li class="success">' +
                        '<div class="file-row">' +
                          '<a href="$href" target="_blank">$text (Opens in a new window)</a>' +
                          '<button class="button-clear remove-file">Remove</button>' +
                        '</div>' +
                      '</li>'

      IFS.core.template.replaceInTemplate(template, {
          text: 'some text',
          href: '/blah'
      })
   */
  return {
    guidGenerator: function () {
      var S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
      }
      return (S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4())
    },
    replaceInTemplate: function (template, options) {
      var result = template
      jQuery.each(options, function (key, value) {
        result = IFS.core.template.replaceAll(result, '$' + key, value)
      })
      return result
    },
    replaceAll: function (target, search, replacement) {
      return target.split(search).join(replacement)
    }
  }
})()
