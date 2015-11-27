/* jshint strict: true, undef: true, unused: true */
/* globals  jQuery : false, md: false, showdown: false, document : false */

//wysiwyg editor for textareas
//Dependencies to load first : hallo.min.js

var ifs_editor = (function(){
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
                ifs_editor.prepareEditor(this);
            });

            ifs_editor.initEditors();
            ifs_editor.bindEditors();
           // ifs_editor.contentEditableEnterFix();
        },
        prepareEditor : function(textarea){
            var el = jQuery(textarea);
            if(el.attr('readonly')){
                //don't add the editor but do render the html on page load
                el.before('<div class="readonly"></div>');
            }
            else {
                el.before('<div class="editor"></div>');
            }
            el.attr('aria-hidden','true');
            ifs_editor.processMarkdownToHtml(el,el.prev());
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
                //make sure that there never is only a break
                var source = jQuery(this).next();
                ifs_editor.processHtmlToMarkdown(data.content,source);
                jQuery(source).trigger('keyup');
            });
        },
        processMarkdownToHtml: function(sourceEl,editorEl) {
            var sourceVal = sourceEl.val();
            if (ifs_editor.htmlToMarkdown(jQuery(editorEl).html()) == sourceVal) {
              return;
            }
            var html = ifs_editor.markdownToHtml(sourceVal);
            jQuery(editorEl).html(html); 
        },
        processHtmlToMarkdown : function(editorEl,sourceEl) {
            var markdown = ifs_editor.htmlToMarkdown(editorEl);
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
        },
        // contentEditableEnterFix : function(){
        //    // for having good html we only agree upon <p>test</p> and not p<br/> 
        //    // however <br/> is default behaviour in FF, Chrome with contenteditble sections
        //     var formatBlockSupported = document.queryCommandSupported("formatBlock");

        //     if(formatBlockSupported){
        //         jQuery('.editor').on( "keypress", function(event){
        //             console.log(event);
        //              if(event.keyCode == '13') {
        //                 document.execCommand('formatBlock', false, 'p');
        //              }
        //         });
        //     }
        // }


    };
})();
