/*jshint multistr: true */
IFS.invites = (function(){
    "use strict";
    var s;
    return {
      settings : {
          formid : '.contributorsForm',
          inputs :  '.contributorsForm input',
          addRow : '.contributorsForm .add-another-row',
          removeRow : '.contributorsForm .remove-another-row'
      },
      init : function(){
          s = this.settings;
          jQuery('body').on('change',s.inputs, function(){
              IFS.invites.saveToCookie();
          });
          jQuery('body').on('click',s.addRow, function(e){
              IFS.invites.addRow(e);
          });
          jQuery('body').on('click',s.removeRow, function(e){
              IFS.invites.removeRow(e);
          });
      },
      saveToCookie : function(){
          //cookie is updated simply by having an ajax call go to the server
          var data = (jQuery(s.formid).serialize());
          jQuery.ajax({
            url: window.location.href,
            method: "POST",
            dataType : 'json', //for requests headers so that the back-end can give a version without 302 redirect
            data: data
          });
      },
      addRow : function(e){
          e.preventDefault();
          var orgContainer = jQuery(e.target).closest('li');
          var orgId = orgContainer.attr('data-invite-org');
          var rowId = orgContainer.find('[data-invite-row]').length;
          var html = '<tr class="form-group" data-invite-row>\
                        <td><input type="text" class="form-control width-full" value="" placeholder="name" name="organisations['+orgId+'].invites['+rowId+'].personName" /></td>\
                        <td><input type="email" class="form-control width-full" value="" placeholder="name@company.co.uk" name="organisations['+orgId+'].invites['+rowId+'].email" /></td>\
                        <td class="alignright"><button value="'+orgId+'_'+rowId+'" name="remove_person" type="submit" class="remove-another-row buttonlink">Remove</button></td>\
                      </tr>';
          orgContainer.find('tbody').append(html);
          IFS.invites.saveToCookie();
      },
      recountRows : function(){
          jQuery('[data-invite-row]').each(function(index,value){
              var orgId = jQuery(value).closest('li').attr('data-invite-org');

              jQuery(value).find('input').each(function(inviteIndex, inviteValue){
                  var input = jQuery(inviteValue);
                  var oldName = input.attr('name');

                  var newName = oldName.split('.');
                  newName[0] = 'organisations['+orgId+']';
                  newName[1] = 'invites['+inviteIndex+']';
                  newName = newName.join('.');

                  input.attr('name',newName);
              });
          });
      },
      removeRow : function(e){
          e.preventDefault();
          var button = jQuery(e.target);
          button.closest('tr').remove();
          IFS.invites.recountRows();
          IFS.invites.saveToCookie();
      }
    };
})();
