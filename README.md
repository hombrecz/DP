reg-system
-------------------------

Registration system like microservices application build on Lightbend Lagom framework for my diploma thesis.

## Rest end points

## API

### create team

`curl 'http://localhost:9000/api/teams' -X POST -d '{"teamId":"alpha2", "teamName":"Alpha-2", "capacity":"30"}' -H 'Content-Type: application/json'`
+ POST /api/teams
+ BODY `{"teamId":"alpha2", "teamName":"Alpha-2", "capacity":"30"}`

### register player

`curl 'http://localhost:9000/api/registration' -X POST -d '{"registrationId":"whatever", "teamId":"alpha1", "name":"John"}' -H 'Content-Type: application/json'`
+ POST /api/registration
+ BODY `{"registrationId":"whatever", "teamId":"alpha1", "name":"John"}`

### get teams
`curl 'http://localhost:9000/api/teams/all'`

+ GET /api/teams/all

### get players

`curl 'http://localhost:9000/api/players/all'`

+ GET /api/players/all