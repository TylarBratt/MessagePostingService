# MessagePostingService
A message posting service using SpringBoot and OAuth2.0

UMS - User Managment Service
 
 Login services-
 
    Initial "/" mapping sets a login page
    
    "login/oauth2/code/github" recieves authorization token and queries for username. It then saves the token and username to the authorization table in the user managment db.
    
    Additionally, a messaage is sent to the TMS with the token and username to be saved to the tweet managment db.
  
  Roles- 
  
    Get, Post, Put, Delete mappings to retrieve, create, update, and delete roles respectively.
  
  User-
  
    "/logout" - (Get) returns user last logout (Post) sets users last logout.
    
    "/user" - (Get) returns full list of users (Post) Create a user (Put) Update a user (Delete) Delete a user.
    
    "/user/role" - (Get) retrieves users role (Post) Assign a role to a user (Delete) Delete a users role.
    
    "/user/producer" - (Get) returns list of producers with a subscriberID (Duplicate)
    

TMS - Tweet Managment Service

  "/recieve" - recieves the auth message from UMS and saves to db.
  
  "/message" - (Get) retrieves a message using the messageID (Post) Create a message (Put) update a message (Delete) Delete a message.
  
  "/message/producer" - (Get) initiates the retrieval of producer ID's using a subscribers ID.
  
  "/auth" - (Get) checks if the session is authorized.
