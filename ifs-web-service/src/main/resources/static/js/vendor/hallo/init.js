jQuery(document).ready(function() {

  var element = jQuery('.textarea-wrapped textarea');
  




  var initEditors = function(){
      jQuery('.editor').hallo({
        plugins: {
          'halloformat': {},
          'hallolists': {},
          'halloreundo': {}
        },
        toolbar: 'halloToolbarInstant'
      });
  };

  var markdownize = function(content) {
    // var html = content.split("\n").map($.trim).filter(function(line) { 
    //   return line != "";
    // }).join("\n");
    // console.log(content,html);

    return md(content);
  };

  var converter = new Showdown.converter();
  var htmlize = function(content) {
    return converter.makeHtml(content);
  };

  // Method that converts the HTML contents to Markdown
  var showSource = function(content,el) {
    console.log(el);
    var markdown = markdownize(content);
    if (jQuery(el).get(0).value == markdown) {
      return;
    }
    jQuery(el).get(0).value = markdown;
  };


  var updateHtml = function(content,el) {
        // console.log(el);

    if (markdownize(jQuery(el).html()) == content) {
      return;
    }
    var html = htmlize(content);
    jQuery(el).html(html); 
  };


  jQuery.each(element, function(index,value){
    var el = jQuery(this);
      if(el.attr('readonly')){
            //don't add the editor but do render the html
            el.before('<div class="readonly"></div>')
      }
      else {
            el.before('<div class="editor"></div>');
      }
      el.attr('aria-hidden','true');

      updateHtml(jQuery(this).html(),jQuery(this).prev());

  });

  jQuery('.editor').bind('hallomodified', function(event, data) {
    var source = jQuery(this).next();
    showSource(data.content,source);
    jQuery(source).trigger('keyup');
  });

  initEditors();


});
