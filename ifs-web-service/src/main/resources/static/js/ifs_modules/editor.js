//wysiwyg editor for textareas
//Dependencies to load first : hallo.min.js
IFS.editor = (function(){
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
                IFS.editor.prepareEditor(this);
            });

            IFS.editor.initEditors();
            IFS.editor.bindEditors();
           // IFS.editor.contentEditableEnterFix();
        },
        prepareEditor : function(textarea){
            var el = jQuery(textarea);
            if(el.attr('readonly')){
                //don't add the editor but do render the html on page load
                el.before('<div class="readonly"></div>');
            }
            else {
                el.before('<div class="editor" spellcheck="true"></div>');
            }
            el.attr('aria-hidden','true');
            IFS.editor.processMarkdownToHtml(el,el.prev());
        },
        initEditors: function(){
          jQuery('.editor').hallo({
            plugins: {
              'halloformat': {},
              'hallolists': {},
              'hallocleanhtml': s.htmlOptions
            },
            toolbar: 'halloToolbarFixed'
          });
        },
        bindEditors : function(){
            jQuery('.editor').bind('hallomodified', function(event, data) {
                var source = jQuery(this).next();
                IFS.editor.processHtmlToMarkdown(data.content,source);
                jQuery(source).trigger('keyup');
            });
        },
        processMarkdownToHtml: function(sourceEl,editorEl) {
            var sourceVal = sourceEl.val();
            if (IFS.editor.htmlToMarkdown(jQuery(editorEl).html()) == sourceVal) {
              return;
            }
            var html = IFS.editor.markdownToHtml(sourceVal);
            jQuery(editorEl).html(html); 
        },
        processHtmlToMarkdown : function(editorEl,sourceEl) {
            var markdown = IFS.editor.htmlToMarkdown(editorEl);
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
            return html;
        }
        // contentEditableEnterFix : function(){
        //    // for having good html we only agree upon <p>test</p> and not p<br/> 
        //    // however <br/> is default behaviour in FF, Chrome with contenteditble sections
        //     var formatBlockSupported = document.queryCommandSupported("formatBlock");

        //     if(formatBlockSupported){
        //         jQuery('.editor').on( "keypress", function(event){
        //              if(event.keyCode == '13') {
        //                 document.execCommand('formatBlock', false, 'p');
        //              }
        //         });
        //     }
        // }
    };
})();
