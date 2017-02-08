from logging import info
from robot.libraries.BuiltIn import BuiltIn
imap = BuiltIn().get_library_instance('ImapLibrary')

def remove_all_emails(**kwargs):

  imap.open_mailbox(**kwargs)

  try:
    imap.wait_for_email(**kwargs)
    imap.delete_all_emails()

  except:
    info("Couldn't find any mail to delete")

  imap.close_mailbox()