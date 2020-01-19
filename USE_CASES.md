# GGTour Use Cases

## 1v1 Challenge Scenarios

### Scenario 1:
A player wants to play a 1v1 ladder game for GGTour
with an arbitrary opponent.

The player navigates to the appropriate Discord server
for the ladder format they wish to play. They post a 
message in the rank channel which corresponds to the
rank of player they wish to play (so those players
can see, the bot is agnostic of rank for challenges):

`@GGTour lfg`

A challenge is created on the server and the bot posts
an acknowledgement. Another player sees the request and
posts an acceptance:

`@GGTour accept @Player1`

The challenge is updated on the server to indicate that
it is accepted. The players may then play their game and
report results. If no one responds in 10 minutes, the
challenge expires and is deleted on the server.

### Scenario 2:
A player wants to play a 1v1 ladder game for GGTour with
a specific opponent.

The player navigates to the appropriate Discord server
for the ladder format they wish to play. They post a
message in any channel:

`@GGTour challenge @Player2`

A challenge is created on the server and the bot posts
and acknowledgement. Player two will then post one of the
following:

`@GGTour accept @Player1` or `@GGtour reject @Player1`

In the former case, the challenge is updated to indicate
that it is accepted, and players may play the game. In
the latter case, the challenge is deleted and the first
player is informed their challenge was turned down. If
no response is made for five minutes, the challenge is
deleted on the server.

### Scenario 3:
A player has posted a challenge but wishes to rescind
it.

The player posts in any channel or via PM to the bot:

`@GGTour cancel`

The bot deletes the challenge from the server.

### Scenario 4:
A match is completed and the players wish to report
their results.

The winning player is expected to handle reporting the
result. They may take one of the following two paths:
1) They log into the website, locate the challenge in
the web interface, and then upload the replay. The server
parses the replay and updates the result and each player's
Elo rating.
2) They PM the bot with the replay file; the bot handles
uploading the replay to the server, and the server parses
it and updates the result and each player's Elo rating.

## Team Format Challenge Scenarios
TBD

## Website Usage Scenarios
### Scenario 1:
A player wishes to register for GGTour.

The player clicks on the "Join GGTour" link on the home
page of the website. They are directed to Battle.net in
order to log in to their account via OAuth. They are then
redirected to the GGTour website with their authorization
token, which is sent to the server. The server attempts
to look them up, fails, and creates a new account, then
prompts the user to configure their account.

After account configuration is complete, they are logged
in to the website.