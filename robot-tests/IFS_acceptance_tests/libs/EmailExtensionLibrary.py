from logging import warn
from robot.libraries.BuiltIn import BuiltIn
imap = BuiltIn().get_library_instance('ImapLibrary')

def remove_all_emails(**kwargs):
  imap.open_mailbox(**kwargs)
  index = imap.wait_for_email(**kwargs)
  warn("index is %s" % index)
  imap.delete_all_emails()
  imap.close_mailbox()