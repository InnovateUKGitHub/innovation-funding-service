from logging import warn
from robot.libraries.BuiltIn import BuiltIn
imap = BuiltIn().get_library_instance('ImapLibrary')

def remove_all_emails(**kwargs):
  warn("clearing mailbox %s" % kwargs['user'])
  imap.open_mailbox(**kwargs)

  try:
    index = imap.wait_for_email(**kwargs)
    warn("index is %s" % index)
    imap.delete_all_emails()
    warn("mail deleted")

  except:
    warn("Couldn't find any mail to delete")

  imap.close_mailbox()