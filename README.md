reg-system
-------------------------

Registration system like microservices application build on Lightbend Lagom framework for my diploma thesis.

# Running aplication and tests

SBT is used to run application. For starting use "sbt runAll, then the gatling test can be issued with "sbt gatling:test" from another console.

# Rest end points

It is possible to send REST requests to application.

## API

### create group

`curl 'http://localhost:9000/api/groups' -X POST -d '{"groupId":"alpha2", "groupName":"Alpha-2", "capacity":"30"}' -H 'Content-Type: application/json'`
+ POST /api/groups
+ BODY `{"groupId":"alpha2", "groupName":"Alpha-2", "capacity":"30"}`

### register user

`curl 'http://localhost:9000/api/registration' -X POST -d '{"registrationId":"whatever", "groupId":"alpha1", "userName":"John"}' -H 'Content-Type: application/json'`
+ POST /api/registration
+ BODY `{"registrationId":"whatever", "groupId":"alpha1", "userName":"John"}`

### get groups
`curl 'http://localhost:9000/api/groups/all'`

+ GET /api/groups/all

### get users

`curl 'http://localhost:9000/api/users/all'`

+ GET /api/users/all