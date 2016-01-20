/*jshint multistr: true */
IFS.invites = (function(){
    "use strict";
    var s;
    return {
      settings : {
          formid : '.contributorsForm',
          inputs :  '.contributorsForm input',
          addRow : '.contributorsForm .add-another-row',
          removeRow : '.contributorsForm .remove-another-row',
          addOrg : '[name="add_partner"]'
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
          jQuery('body').on('click',s.addOrg, function(e){
              IFS.invites.addOrg(e);
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
          var orgContainer = jQuery(e.target).closest('[data-invite-org]');
          var orgId = orgContainer.index();
          var rowId = orgContainer.find('[data-invite-row]').length;
          var html = '<tr class="form-group" data-invite-row>\
                        <td><input type="text" class="form-control width-full" value="" placeholder="name" name="organisations['+orgId+'].invites['+rowId+'].personName" /></td>\
                        <td><input type="email" class="form-control width-full" value="" placeholder="name@company.co.uk" name="organisations['+orgId+'].invites['+rowId+'].email" /></td>\
                        <td class="alignright"><button value="'+orgId+'_'+rowId+'" name="remove_person" type="submit" class="remove-another-row buttonlink">Remove</button></td>\
                      </tr>';
          orgContainer.find('tbody').append(html);
          IFS.invites.saveToCookie();
      },
      addOrg : function(e){
        e.preventDefault();
        var currentOrgs = jQuery('[data-invite-org]');
        var orgId = currentOrgs.length;
        var html = '<li data-invite-org>\
                    <h2 class="heading-medium">Partner Organisation "<input type="text" value="" name="organisations['+orgId+'].organisationName" placeholder="Organisation Name" class="form-control width-large">"</h2>\
                          <input type="hidden" value="" name="organisations['+orgId+'].organisationId" placeholder="name" class="form-control width-full">\
                          <table>\
                              <thead><tr>\
                                  <th>Name</th>\
                                  <th>E-mail</th>\
                                  <th>&nbsp;</th>\
                              </tr></thead>\
                              <tbody>\
                              <tr data-invite-row class="form-group">\
                                  <td><input type="text" value="" name="organisations['+orgId+'].invites[0].personName"  placeholder="name" class="form-control width-full"></td>\
                                  <td><input type="email" value="" name="organisations['+orgId+'].invites[0].email" placeholder="name@company.co.uk" class="form-control width-full"></td>\
                                  <td class="alignright"><button value="'+orgId+'_0" name="remove_person" type="submit" class="remove-another-row buttonlink">Remove</button></td>\
                              </tr>\
                              </tbody>\
                          </table>\
                          <p class="alignright">\
                              <button value="'+orgId+'" name="add_person" type="submit" class="add-another-row buttonlink">Add person</button>\
                          </p>\
                      </li>';
          currentOrgs.last().after(html);
          IFS.invites.saveToCookie();
      },
      recountRows : function(){
          jQuery('[data-invite-org] input').each(function(){
              var input = jQuery(this);

              var orgId = input.closest('[data-invite-org]').index();
              var inviteeId = input.closest('[data-invite-row]').index();

              var oldName = input.attr('name');
              if(typeof(oldName) !== 'undefined'){

                var newName = oldName.split('.');
                if(inviteeId !== -1 && orgId !== -1){
                  if(orgId === 0){ inviteeId--; } //for the readonly owner field in the first organisation
                  newName[0] = 'organisations['+orgId+']';
                  newName[1] = 'invites['+inviteeId+']';
                }
                else if(orgId !== -1) {
                  newName[0] = 'organisations['+orgId+']';
                }
                newName = newName.join('.');
                input.attr('name',newName);
              }
          });
          IFS.invites.saveToCookie();
      },
      removeRow : function(e){
          e.preventDefault();
          var button = jQuery(e.target);
          if(button.closest('[data-invite-org]').find('tbody tr').length == 1){
            button.closest('[data-invite-org]').remove(); 
          }
          else {
            button.closest('tr').remove();
          }
          IFS.invites.recountRows();
      }
    };
})();
