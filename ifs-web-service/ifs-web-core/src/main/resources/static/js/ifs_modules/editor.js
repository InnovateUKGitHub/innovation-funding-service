//wysiwyg editor for textareas
//Dependencies to load first : hallo.min.js
IFS.core.editor = (function() {
  "use strict";
  var s; // private alias to settings
  var converter;

  return {
    settings : {
      editorTextarea : 'textarea[data-editor]',
      htmlOptions : {
        format: false,
        allowedTags: ['p', 'em', 'strong', 'ol', 'ul', 'li', 'br', 'b', 'div']
      }
    },
    init : function() {
      s = this.settings;
      converter = new showdown.Converter();

      jQuery.each(jQuery(s.editorTextarea), function() {
        IFS.core.editor.prepareEditorHTML(this);
      });
      IFS.core.editor.initEditors();

      jQuery("[data-md-to-html]").each(function() {
        var content = jQuery(this).html();
        var html =  IFS.core.editor.markdownToHtml(content);
        jQuery(this).html(html);
      });
    },
    prepareEditorHTML : function(textarea) {
      var el = jQuery(textarea);
      var editorType = el.attr('data-editor');
      if(editorType !== ''){
        var labelledby = '';
        if(jQuery('[for="'+el.attr('id')+'"]').length) {
          labelledby = jQuery('[for="'+el.attr('id')+'"]').attr('id');
        }

        if(el.attr('readonly')){
          //don't add the editor but do render the html on page load
          el.before('<div class="readonly"></div>');
        }
        else {
          el.before('<div data-editor="'+editorType+'" class="editor" spellcheck="true" aria-multiline="true" tabindex="0" labelledby="'+labelledby+'" role="textbox"></div>');
        }

        el.attr('aria-hidden', 'true');
        switch(editorType){
          case 'md':
            IFS.core.editor.textareaMarkdownToHtml(el, el.prev());
            break;
          case 'html':
            var html = jQuery.htmlClean(el.val(), s.htmlOptions);
            el.prev().html(html);
            break;
        }
      }
    },
    initEditors: function() {

      jQuery('[role="textbox"][data-editor]').hallo({
        plugins: {
          'halloformat': {},
          'hallolists': {},
          'hallocleanhtml': s.htmlOptions
        },
        toolbar: 'halloToolbarFixed'
      });

      jQuery('[role="textbox"][data-editor="md"]').on('hallomodified', function(event, data) {
        var source = jQuery(this).next();
        IFS.core.editor.editorHtmlToMarkdown(data.content, source);
        jQuery(source).trigger('keyup');
      });

      jQuery('[role="textbox"][data-editor="html"]').on('hallomodified', function(event, data) {
        var textarea = jQuery(this).next();
        var html =  jQuery.htmlClean(data.content, s.htmlOptions);
        if(html.replace(/<[^>]+>/ig, "").length === 0){
          html = '';
        }
        jQuery(textarea).get(0).value = html;
        jQuery(textarea).trigger('keyup');
      });
      jQuery('[role="textbox"][data-editor]').on('blur', function() {
        var textarea = jQuery(this).next();
        jQuery(textarea).trigger('change');
      });

    },
    textareaMarkdownToHtml: function(sourceEl, editorEl) {
      var sourceVal = sourceEl.val();
      if (IFS.core.editor.htmlToMarkdown(jQuery(editorEl).html()) == sourceVal) {
        return;
      }
      var html = IFS.core.editor.markdownToHtml(sourceVal);
      jQuery(editorEl).html(html);
    },
    editorHtmlToMarkdown : function(editorEl, sourceEl) {
      var markdown = IFS.core.editor.htmlToMarkdown(editorEl);
      if (jQuery(sourceEl).get(0).value == markdown) {
        return;
      }
      jQuery(sourceEl).get(0).value = markdown;
    },
    htmlToMarkdown : function(content) {
      var html = jQuery.trim(content.replace(/(\r\n|\n|\r)/gm, ""));
      html = jQuery.htmlClean(html, s.htmlOptions);

      return md(html);
    },
    markdownToHtml : function(content) {
      var html;
      if(content.length === 0){
        html = "<p>&nbsp;</p>";
      }
      else {
        html = converter.makeHtml(content);
      }
      html = jQuery.htmlClean(html, s.htmlOptions);
      return html;
    }
  };
})();
