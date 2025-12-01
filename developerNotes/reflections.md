# Developer / Design Moments

## Week 1 (20/10/2025)

We used this first week to get to know each other and identify what all our strengths and weaknesses were heading into the project. Frontend work was a noticeable void in a lot of our toolkits, but Kyara was willing to take on the challenge of learning React over the course of the project, which allowed the rest of us to focus on our strengths in backend/database work.

We also decided what API we would use in the first week, which I think was a great move. We picked the Google Flights API through SerpAPI as our tool for building our student-budget flight finder. While it was early to lock this in, it gave us the opportunity to spend the next week familiarizing ourselves with the API and its capabilities. This allowed us to head into Week 2 knowing all the tools available to us and to pinpoint what our app should do and make use of.


## Week 2 (27/10/2025)

We used this reading week to flesh out our project plan. We created user stories to give structure to our app and a Mermaid diagram to begin imagining how we would design the architecture. While it was helpful to look toward giants like Ryanair and Aer Lingus for inspiration, we needed to stay mindful that our primary target is students. This means a much stronger focus on flight affordability and highlighting options that reduce cost — such as being flexible with layovers or flight duration.

During this planning stage we also spent time structuring the team. Instead of allowing everyone to randomly pick features to work on, we decided to split into defined roles similar to a real software engineering team. Roles like engineering/product/design manager, QA engineer, database manager, and general manager were proposed. We felt this would give us experience across typical industry responsibilities while preventing work from piling onto one person. We plan to assign final roles next week.




## Week 4 (10/11/2025)

We were running into issues with what ports our backend and frontend were using. Whether is was ports already in use errors or just too much variance in what port a service would end up using, we needed a way to fix the services to a particular port. This would make testing easier on all our systems and make it easier to nake the connection between frontend and backend.

Kyara made the call that since we already have our database running on Docker, why not run our backend and frontend from there as well. She made the necessary changes to our docker-compose file and now when we want to run the project, at least locally, we just start the docker container and all the services start on fixed ports. That was a big quality of life improvemnt.

Secondly, with MVP due this Friday we needed to clean up the project a bit. We consolidated models into one folder, combined some related services to reduce the number of service classes and updated the Readme to have some instructions on running the project on coder. 

With this spring-cleaning done, we can now focus on adding additional functionality as we head into the final weeks of the project.
