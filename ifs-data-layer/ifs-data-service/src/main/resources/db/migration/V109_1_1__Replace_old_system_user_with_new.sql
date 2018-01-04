/**
  This is contract phase of IFS-2419.  A new system registration user was inserted for IFS-2419 with new UUID.
  Old one wasn't removed to allow for ZDD.  This user can now be removed on next deploy.  This script achieves this
  by removing old user and updating email address of new user.
 */

