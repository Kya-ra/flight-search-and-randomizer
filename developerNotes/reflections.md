# Developer / Design Moments

### 21.10.2025 (Meeting 1)
**What we achieved:
**Challenges:
**Next steps:



## Week 4 (10/11/2025)

We were running into issues with what ports our backend and frontend were using. Whether is was ports already in use errors or just too much variance in what port a service would end up using, we needed a way to fix the services to a particular port. This would make testing easier on all our systems and make it easier to nake the connection between frontend and backend.

Kyara made the call that since we already have our database running on Docker, why not run our backend and frontend from there as well. She made the necessary changes to our docker-compose file and now when we want to run the project, at least locally, we just start the docker container and all the services start on fixed ports. That was a big quality of life improvemnt.

Secondly, with MVP due this Friday we needed to clean up the project a bit. We consolidated models into one folder, combined some related services to reduce the number of service classes and updated the Readme to have some instructions on running the project on coder. 

With this spring-cleaning done, we can now focus on adding additional functionality as we head into the final weeks of the project.
