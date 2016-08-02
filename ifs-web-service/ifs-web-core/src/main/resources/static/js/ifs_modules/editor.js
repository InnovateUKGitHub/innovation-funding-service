//wysiwyg editor for textareas
//Dependencies to load first : hallo.min.js
IFS.core.editor = (function(){
    "use strict";
    var s; // private alias to settings
    var converter;

    return {
        settings : {
            textareas : '.textarea-wrapped textarea',
            htmlOptions : {
                format: false,
                allowedTags: ['p','em','strong','ol','ul','li','br','b','div']
            }
        },
        init : function(){
            s = this.settings;
            converter = new showdown.Converter();

            var textareas = jQuery(s.textareas);
             jQuery.each(textareas,function(){
                IFS.core.editor.prepareEditor(this);
            });
            jQuery(".js-md-to-html").each(function(){
              var content = jQuery(this).html();
              var html =  IFS.core.editor.markdownToHtml(content);
              jQuery(this).html(html);
            });

            IFS.core.editor.initEditors();
            IFS.core.editor.bindEditors();
        },
        prepareEditor : function(textarea){
            var el = jQuery(textarea);
            var labelledby = '';
            if(jQuery('[for="'+el.attr('id')+'"]').length) {
              labelledby = jQuery('[for="'+el.attr('id')+'"]').attr('id');
            }

            if(el.attr('readonly')){
                //don't add the editor but do render the html on page load
               el.before('<div class="readonly"></div>');
            }
            else {
                el.before('<div data-editor="" class="editor" spellcheck="true" aria-multiline="true" tabindex="0" labelledby="'+labelledby+'" role="textbox"></div>');
            }

            el.attr('aria-hidden','true');
            IFS.core.editor.processMarkdownToHtml(el,el.prev());
        },
        initEditors: function(){
          jQuery('[data-editor]').hallo({
            plugins: {
              'halloformat': {},
              'hallolists': {},
              'hallocleanhtml': s.htmlOptions
            },
            toolbar: 'halloToolbarFixed'
          });
        },
        bindEditors : function(){
            jQuery('[data-editor]').bind('hallomodified', function(event, data) {
                var source = jQuery(this).next();
                IFS.core.editor.processHtmlToMarkdown(data.content,source);
                jQuery(source).trigger('keyup');
            });
        },
        processMarkdownToHtml: function(sourceEl,editorEl) {
            var sourceVal = sourceEl.val();
            if (IFS.core.editor.htmlToMarkdown(jQuery(editorEl).html()) == sourceVal) {
              return;
            }
            var html = IFS.core.editor.markdownToHtml(sourceVal);
            jQuery(editorEl).html(html);
        },
        processHtmlToMarkdown : function(editorEl,sourceEl) {
            var markdown = IFS.core.editor.htmlToMarkdown(editorEl);
            if (jQuery(sourceEl).get(0).value == markdown) {
              return;
            }
            jQuery(sourceEl).get(0).value = markdown;
        },
        htmlToMarkdown : function(content){
            var html = jQuery.trim(content.replace(/(\r\n|\n|\r)/gm,""));
            html = jQuery.htmlClean(html, s.htmlOptions);

            return md(html);
        },
        markdownToHtml : function(content){
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
