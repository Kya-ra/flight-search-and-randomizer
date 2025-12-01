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


## Week 5 (17/11/2025)

This week was focused on improving the user system and integrating it more fully into the frontend. Building on the backend work from the previous week, we added functionality for users to sign up and log in directly from the homepage. Kyara polished the frontend so that users could see their dashboard after logging in, displaying all their details and preferences.

We also implemented functionality for users to edit their information and delete their profile if needed. This gave us valuable experience in connecting frontend forms with backend endpoints, handling state updates, and managing asynchronous requests in React.

Throughout the week, we tested these features extensively. While some integration bugs occurred—such as error 500s due to frontend-backend communication—these were identified and fixed. GitLab pipelines and SAST checks were also run to ensure that our code remained secure and maintainable.

By the end of the week, the user system was largely functional, and the frontend was more cohesive with the backend, setting us up for feature refinement in the coming weeks.

## Week 6 (24/11/2025)

This week, the team focused on improving the user system and adding new frontend features. Conor polished the sign-in and sign-up buttons on the home page, enabling user creation from the frontend. The user dashboard was enhanced to display all user details and provide functionality to edit and delete profiles.

Kyara implemented flight flexibility features on the frontend, allowing the app to aggregate results over a range of dates and display them in a more user-friendly manner. Theresa began work on the random flight suggester, adding the initial frontend and backend components.

Team coordination remained important this week, with discussions on meeting times, feature priorities, and the upcoming code submission deadlines. The focus was on ensuring all features were integrated smoothly and ready for final testing in the following week.
