Disc Rod

TODO:
Add color change commands (ability to change background/text colors) - DONE
    -Also allow users to change their name's color - STILL TO DO
Add a system for keeping track of logged in users (maybe with a list) - DONE
    -Also ensure that all users have unique names with this system - DONE
Add a better system for keeping track of who is an admin and who is not
    -Allow the first logged in user to set parameters like who is an admin and
    certain channel title permissions
Fix/redo text input system for the client - DONE
    -Use an actual GUI element if possible, this would greatly simplify
    unicode inputs - DONE
Add different channels and ability to change channels
Add a scrolling text functionality, eliminating the need to clear the chat
    after 25 lines of text
Add a functionality to save chat logs to a text file with a command
Add the ability to see a client's IP based on their username and vice versa - KIND OF DONE
    -This would be done with the list of users aforementioned, since the list
    would also include an IP to be matched with the user's name - NOT YET
Add a functionality to ban an IP of a client from joining as well as a
    name
    -Could be done permanently or for a set length of time only
    -Also allow a ban message to be shown to the user
Add a window title functionality - DONE
    -Ideally the window title would show information like the connected server's
    IP, the channel name, and/or the current user's name - DONE
Allow users to leave a server and join a different one without closing the
    client
Add a rank system in which certain colors/priveleges may be given to one or more
    users by name or IP (unlikely to implement this)

KNOWN BUGS:
    At times, if the background color is changed at the same time the chat log
    is cleared, the color change will not go through.