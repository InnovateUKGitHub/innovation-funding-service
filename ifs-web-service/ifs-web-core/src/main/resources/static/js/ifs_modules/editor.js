// wysiwyg editor for textareas
// Dependencies to load first : hallo.min.js
IFS.core.editor = (function () {
  'use strict'
  var s // private alias to settings
  var converter

  return {
    settings: {
      editorTextarea: 'textarea[data-editor]',
      editorOptions: {
        plugins: {
          'halloformat': {},
          'hallolists': {},
          'hallocleanhtml': {
            format: false,
            allowedTags: ['p', 'em', 'strong', 'ol', 'ul', 'li', 'br', 'b', 'div', 'a']
          }
        },
        toolbar: 'halloToolbarFixed'
      }
    },
    init: function () {
      s = this.settings
      converter = new showdown.Converter()

      jQuery.each(jQuery(s.editorTextarea), function () {
        IFS.core.editor.prepareEditorHTML(this)
      })
      IFS.core.editor.initEditors()

      jQuery('[data-md-to-html]').each(function () {
        var content = jQuery(this).html()
        var html = IFS.core.editor.markdownToHtml(content)
        jQuery(this).html(html)
      })
      jQuery('[data-text-to-html]').each(function () {
        var content = jQuery(this).html()
        var html = IFS.core.editor.textToHtml(content)
        jQuery(this).html(html)
      })
    },
    prepareEditorHTML: function (textarea) {
      var el = jQuery(textarea)
      var editorType = el.attr('data-editor')
      if (editorType !== '') {
        var labelledby = ''
        var label = jQuery('[for="' + el.prop('id') + '"]')
        if (label.length) {
          if (!label.prop('id')) {
            label.prop('id', el.prop('id') + '-label')
          }
          labelledby = 'aria-labelledby="' + label.prop('id') + '"'
        }

        if (el.attr('readonly')) {
          // don't add the editor but do render the html on page load
          el.before('<div class="readonly govuk-body"></div>')
        } else {
          el.before('<div data-editor="' + editorType + '" class="editor govuk-body" spellcheck="true" aria-multiline="true" tabindex="0" ' + labelledby + ' role="textbox"></div>')
        }
        el.attr('aria-hidden', 'true')

        var editorDiv = el.prev()
        switch (editorType) {
          case 'md':
            IFS.core.editor.textareaMarkdownToHtml(el, editorDiv)
            break
          case 'html':
            var html = jQuery.htmlClean(el.val(), s.editorOptions.plugins.hallocleanhtml)
            editorDiv.html(html)
            break
        }
        return editorDiv
      }
    },
    initEditors: function () {
      // adding the link functionality when the editor is in html mode
      var htmlEditorOptions = jQuery.extend(true, {}, s.editorOptions, {
        plugins: {
          'hallolink': {}
        }
      })

      jQuery('[role="textbox"][data-editor="html"]').hallo(htmlEditorOptions)
      jQuery('[role="textbox"][data-editor="md"]').hallo(s.editorOptions)

      jQuery(document).on('hallomodified', '[role="textbox"][data-editor="md"]', function (event, data) {
        var source = jQuery(this).parent().find('textarea')
        IFS.core.editor.editorHtmlToMarkdown(data.content, source)
        jQuery(source).trigger('keyup')
      })

      // Hallo doesn't trigger a modified event for spell check replacements.
      jQuery(document).on('input', '[role="textbox"][data-editor="md"]', function (event) {
        jQuery(this).trigger('change')
      })

      jQuery(document).on('hallomodified', '[role="textbox"][data-editor="html"]', function (event, data) {
        var textarea = jQuery(this).parent().find('textarea')
        var html = jQuery.htmlClean(data.content, s.editorOptions.plugins.hallocleanhtml)
        if (html.replace(/<[^>]+>/ig, '').length === 0) {
          html = ''
        }
        jQuery(textarea).get(0).value = html
        jQuery(textarea).trigger('keyup')
      })
      jQuery(document).on('blur', '[role="textbox"][data-editor]', function () {
        var textarea = jQuery(this).parent().find('textarea')
        jQuery(textarea).trigger('change')
      })
    },
    textareaMarkdownToHtml: function (sourceEl, editorEl) {
      var sourceVal = sourceEl.val()
      var html = IFS.core.editor.markdownToHtml(sourceVal)
      jQuery(editorEl).html(html)
    },
    editorHtmlToMarkdown: function (editorEl, sourceEl) {
      var markdown = IFS.core.editor.htmlToMarkdown(editorEl)
      if (jQuery(sourceEl).get(0).value === markdown) {
        return
      }
      jQuery(sourceEl).get(0).value = markdown
    },
    htmlToMarkdown: function (content) {
      var formattedHtml = IFS.core.editor.formatContent(content)
      var html = jQuery.trim(formattedHtml.replace(/(\r\n|\n|\r)/gm, ''))
      html = jQuery.htmlClean(html, s.editorOptions.plugins.hallocleanhtml)
      return md(html)
    },
    markdownToHtml: function (content) {
      var html
      if (content.length === 0) {
        html = '<p>&nbsp;</p>'
      } else {
        html = converter.makeHtml(content)
        html = jQuery.htmlClean(html, s.editorOptions.plugins.hallocleanhtml)
      }
      return html
    },
    textToHtml: function (content) {
      var html
      if (content.length === 0) {
        html = '<p>&nbsp;</p>'
      } else {
        html = '<p>' + content.replace(/\n/g, '<br />') + '</p>'
      }
      return html
    },
    formatContent: function (content) {
      var unorderedListRegex = /<\/ul>(.*?)<\/div>/g
      var orderedListRegex = /<\/ol>(.*?)<\/div>/g
      var unorderedListRegexTwo = /<\/ul>(.*?)<br>/g
      var orderedListRegexTwo = /<\/ol>(.*?)<br>/g
      var unorderedListRegexThree = /<\/ul>(.*?)<br type="_moz">/g
      var orderedListRegexThree = /<\/ol>(.*?)<br type="_moz">/g
      var unorderedListRegexFour = /<\/ul>(.*?)$/g
      var orderedListRegexFour = /<\/ol>(.*?)$/g
      return content
        .replace(unorderedListRegex, '</ul><div>$1</div></div>')
        .replace(orderedListRegex, '</ol><div>$1</div></div>')
        .replace(unorderedListRegexTwo, '</ul><div>$1</div>')
        .replace(orderedListRegexTwo, '</ol><div>$1</div>')
        .replace(unorderedListRegexThree, '</ul><div>$1</div>')
        .replace(orderedListRegexThree, '</ol><div>$1</div>')
        .replace(unorderedListRegexFour, '</ul><div>$1</div>')
        .replace(orderedListRegexFour, '</ol><div>$1</div>')
    }
  }
})()
