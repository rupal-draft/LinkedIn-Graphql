# <div align="center"> <img src="https://img.shields.io/badge/Spring Boot-v3.x-brightgreen" alt="Spring Boot Version"/> <img src="https://img.shields.io/badge/GraphQL-Schema%20First-blueviolet" alt="GraphQL Approach"/> <img src="https://img.shields.io/badge/Security-Spring%20Security-brightblue" alt="Security"/> <img src="https://img.shields.io/badge/Database-PostgreSQL-blue" alt="Database"/> </div>

<div align="center">
  <h1>LinkedIn Clone Backend API</h1>
  <p>A meticulously crafted and feature-rich backend API built with Spring Boot and GraphQL, designed to replicate and enhance the core functionalities of LinkedIn. Experience a powerful platform for professional networking, dynamic content sharing, exciting job opportunities, seamless real-time communication, and timely updates.</p>
</div>

<br/>

## ✨ Key Features

This robust backend implementation offers an extensive suite of features, providing a solid and scalable foundation for a thriving professional networking platform:

* **🛡️ Secure Authentication:** Implemented with Spring Security, ensuring robust user authentication and authorization to protect user data and privacy.
* **👤 User Management:** Comprehensive functionalities for user profile creation, retrieval, updating, and management, encompassing personal details, professional experience, and educational background.
* **📰 Post Creation & Management:** Empower users to create, share, and manage diverse content, fostering engaging discussions and knowledge exchange within their network.
* **🔗 Connections:** A seamless connection system allowing users to build and manage their professional network, facilitating collaboration and growth opportunities.
* **💼 Job Opportunities:** A dedicated module for posting, browsing, and managing job listings, efficiently connecting talent with relevant career opportunities.
* **💬 Real-time Messaging:** Enable direct and efficient communication between users through chat sessions and message exchange.
* **🔔 Notifications:** A comprehensive notification system keeps users informed about important activities, ensuring they never miss crucial updates and interactions.
* **📬 Connection Requests:** Facilitate the process of building connections with a clear and intuitive connection request system.
* **💾 Saved Jobs:** Allow users to save interesting job postings for later review and application.
* **🚀 Job Applications:** Enable users to apply for job openings directly through the platform, streamlining the recruitment process.
* **🐳 Docker Ready:** The application can be easily containerized using Docker. A Docker image is available on [Docker Hub](https://hub.docker.com/repository/docker/rupaldraft/linkedin-app/general).

## 🛠️ Technologies Used

This project harnesses the power of the following cutting-edge technologies:

* **Backend Framework:** [Spring Boot](https://spring.io/projects/spring-boot) - A leading Java-based framework for building robust and scalable microservices and web applications.
* **API Layer:** [GraphQL](https://graphql.org/) - A powerful query language for your API, enabling clients to request precisely the data they need, enhancing efficiency and flexibility. This project adopts a schema-first approach for a well-defined and intuitive API structure.
* **Security:** [Spring Security](https://spring.io/projects/spring-security) - A highly customizable and robust authentication and access-control framework for Java applications, ensuring the security of your platform.
* **Database:** [PostgreSQL](https://www.postgresql.org/) - A reliable, scalable, and open-source relational database management system, chosen for its performance and data integrity.
* **Build Tool:** [Maven](https://maven.apache.org/) - A powerful build automation tool for managing dependencies and building the Java project efficiently.
* **Documentation:** [Postman](https://www.postman.com/) - Utilized for comprehensive API testing and clear, user-friendly documentation.
* **Containerization:** [Docker](https://www.docker.com/) - Used to containerize the application for easy deployment.


## 🚀 Getting Started

Follow these straightforward steps to set up your local development environment:

1.  **Prerequisites:**
    * Java Development Kit (JDK) 17 or higher installed on your system.
    * [Maven](https://maven.apache.org/download.cgi) properly installed.
    * [PostgreSQL](https://www.postgresql.org/download/) installed and running.
    * (Recommended) [Postman](https://www.postman.com/downloads/) for API exploration and testing.
    * (Optional) [Docker](https://www.docker.com/get-started/) for running the application in a container.

2.  **Clone the Repository:**

    ```bash
    git clone https://github.com/rupal-draft/LinkedIn-Graphql.git
    cd LinkedIn-Graphql
    ```

3.  **Configure Database:**

    * Create a dedicated PostgreSQL database for the application.
    * Update the database connection details in the `src/main/resources/application.properties` or `application.yml` file with your specific database credentials.

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

4.  **Build the Application:**

    ```bash
    mvn clean install
    ```

5.  **Run the Application:**

    * **Using Spring Boot:**

        ```bash
        mvn spring-boot:run
        ```

      The backend server will initialize and run, typically accessible at `http://localhost:8080`.

    * **Using Docker:**

        * Build the Docker image:
            ```bash
            docker build -t your-docker-image-name .
            ```
        * Run the Docker container:
            ```bash
            docker run -p 8080:8080 your-docker-image-name
            ```
6.  **Explore the GraphQL API:**

    Access the intuitive GraphQL Playground, an in-browser IDE for exploring and interacting with your GraphQL API, usually located at `http://localhost:8080/graphql`.

## 🐳 Docker Instructions

* **Docker Hub Repository:** The Docker image for this application is available on Docker Hub: [rupaldraft/linkedin-app](https://hub.docker.com/r/rupaldraft/linkedin-app)
* **Pull the image:**
    ```bash
    docker pull rupaldraft/linkedin-app:tagname
    ```
* **Push a new tag:**
    ```bash
    docker push rupaldraft/linkedin-app:tagname
    ```

## 📄 API Documentation

Comprehensive and user-friendly API documentation is provided via Postman. Import the following collection to effortlessly explore the available GraphQL queries and mutations:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://documenter.getpostman.com/view/30415721/2sB2j68pfM)

**[Link to your Postman Collection](https://documenter.getpostman.com/view/30415721/2sB2j68pfM)**

This detailed documentation covers:

* Secure Authentication and Authorization workflows.
* Comprehensive User management queries and mutations.
* Functionalities for Post creation, retrieval, and management.
* Management of Connections and connection requests.
* Job posting, browsing, and application processes.
* Messaging functionalities and chat sessions.
* The Notification system and its various types.
* Saving and managing favorite job postings.

### GraphQL API Overview

The API is structured around GraphQL, offering a strongly-typed schema for interacting with the backend. Below is a summary of the main queries and mutations:

#### Authentication

* **Mutation:**
    * `signup(input: SignupInput!): SignupResponse!` - Registers a new user.
    * `login(input: LoginInput!): LoginResponse!` - Authenticates an existing user and returns tokens.
    * `changePassword(input: ChangePasswordInput!): Response!` - Allows a user to change their password.
    * `refreshToken: LoginResponse!` - Refreshes the authentication token.

#### Connections

* **Mutation:**
    * `sendConnectionRequest(receiverId: ID!): Response` - Sends a connection request to another user.
    * `acceptConnectionRequest(requestId: ID!): Response` - Accepts a pending connection request.
    * `rejectConnectionRequest(requestId: ID!): Response` - Rejects a pending connection request.
    * `removeConnection(connectionId: ID!): Response` - Removes an existing connection.
* **Query:**
    * `getMyConnections: ConnectionsResponse` - Retrieves the user's connections.
    * `getPendingRequests: ConnectionRequestsResponse` - Retrieves connection requests pending for the user.
    * `getSentRequests: ConnectionRequestsResponse` - Retrieves connection requests sent by the user.

#### Jobs

* **Query:**
    * `getJobById(jobId: ID!): JobResponseDto` - Retrieves a job by its ID.
    * `getMyApplications: JobApplicationsResponse` - Retrieves the user's job applications.
    * `getApplicationsOfJob(jobId: ID!): JobApplicationsResponse` - Retrieves applications for a specific job.
    * `getApplicationsOfJobByStatus(jobId: ID!, status: String!): JobApplicationsResponse` - Retrieves applications for a job filtered by status.
    * `getApplicationById(applicationId: ID!): SingleJobApplicationResponse` - Retrieves a single job application.
    * `getMySavedJobs: JobsResponseDto` - Retrieves the user's saved jobs.
    * `getJobsByStatus(status: String!): JobsResponseDto` - Retrieves jobs by their status.
    * `recommendedJobs: JobsResponseDto` - Retrieves recommended jobs for the user.
    * `getJobsByTitle(title: String!): JobsResponseDto` - Retrieves jobs by title.
    * `getJobsByTitleAndStatus(title: String!, status: String!): JobsResponseDto`
    * `getJobsByTitleAndLocation(title: String!, location: String!): JobsResponseDto`
    * `getJobsByTitleAndLocationAndStatus(title: String!, location: String!, status: String!): JobsResponseDto`
    * `getJobsByLocation(location: String!): JobsResponseDto`
    * `getJobsByLocationAndStatus(location: String!, status: String!): JobsResponseDto`
    * `getJobsByUser(email: String!): JobsResponseDto`
    * `getAllJobs: JobsResponseDto` - Retrieves all jobs.

* **Mutation:**
    * `createJob(jobInput: JobInput!): JobResponseDto` - Creates a new job posting.
    * `updateJob(jobId: ID!, jobUpdate: JobUpdate!): Response` - Updates an existing job posting.
    * `deleteJob(jobId: ID!): Response` - Deletes a job posting.
    * `saveJob(jobId: ID!): Response` - Saves a job for the user.
    * `unsaveJob(jobId: ID!): Response` - Unsaves a job for the user.
    * `applyForJob(jobId: ID!, resumeUrl: String!): Response` - Applies for a job.
    * `withdrawApplication(jobId: ID!): Response` - Withdraws a job application.

#### Message

* **Query:**
    * `getMyChatSessions: ChatSessionsResponse` - Retrieves the user's chat sessions.
    * `getMessagesBySession(sessionId: ID!): MessagesResponse` - Retrieves messages for a specific chat session.
* **Mutation:**
    * `createOrGetChatSession(receiverId: ID!): SingleChatSessionResponse` - Creates a new or retrieves an existing chat session.
    * `sendMessage(input: SendMessageInput!): SingleMessageResponse` - Sends a message in a chat session.
    * `markMessagesAsSeen(sessionId: ID!): Response` - Marks messages in a session as seen.

#### Notifications

* **Query:**
    * `getMyNotifications: NotificationsResponse` - Retrieves the user's notifications.

#### Posts

* **Mutation:**
    * `createPost(postInput: PostInput): CreatedPostResponse` - Creates a new post.
    * `updatePost(id: ID!, content: String!): Response` - Updates an existing post.
    * `deletePost(id: ID!): Response` - Deletes a post.
    * `changeVisibility(id: ID!, visibility: String!): Response` - Changes the visibility of a post.
    * `addComment(postId: ID!, content: String!): Response` - Adds a comment to a post.
    * `deleteComment(postId: ID!, commentId: ID!): Response` - Deletes a comment from a post.
    * `likePost(postId: ID!): Response` - Likes a post.
    * `unlikePost(postId: ID!): Response` - Removes a like from a post.
    * `updateComment(commentId: ID!, content: String!): Response`
* **Query:**
    * `getPostsByUser(userId: ID!): MultiplePostResponse!` - Retrieves posts by a specific user.
    * `getMyPosts: MultiplePostResponse!` - Retrieves the logged-in user's posts.
    * `getPostsByCategory(category: String!): MultiplePostResponse!` - Retrieves posts by category.
    * `getAllPosts: MultiplePostResponse!` - Retrieves all posts.
    * `getPostById(id: ID!): SinglePostResponse` - Retrieves a post by its ID
    * `getAllLikes(postId: ID!): LikeResponse`
    * `getAllComments(postId: ID!): AllCommentResponse`

#### Users

* **Mutation:**
    * `updateProfile(input: UserUpdateInput!): Response` - Updates the user's profile.
    * `addExperience(input: ExperienceInput!): Response` - Adds an experience entry to the user's profile.
    * `addEducation(input: EducationInput!): Response` - Adds an education entry to the user's profile.
    * `deleteExperience(experienceId: ID!): Response` - Deletes an experience entry.
    * `deleteEducation(educationId: ID!): Response`
* **Query:**
    * `filterUsersExperience(position: String, years: Float): ListUserResponseDto` - Retrieves users filtered by experience.
    * `filterUsersEducation(field: String, degree: String): ListUserResponseDto` - Retrieves users filtered by education.
    * `getUserById(id: ID!): DetailUserResponse` - Retrieves a user by their ID.
    * `getMyProfile: DetailUserResponse` - Retrieves the logged-in user's profile.

## 📂 Project Structure

- The `common` folder contains all shared components used across different modules
- Each feature module follows the same consistent structure:
    - `dto/` for data transfer objects
    - `entity/` for domain models
    - `resolver/` for GraphQL resolvers
    - `service/` for business logic
    - `repository/` for data access
- GraphQL schemas are organized by feature in the `resources/graphql` directory

I warmly welcome contributions to enhance this project! Feel free to fork the repository, create a dedicated branch for your feature or bug fix, and submit a well-documented pull request. Please ensure that your code adheres to the project's coding standards and includes relevant unit and integration tests.

## 🙏 Acknowledgements

We extend our sincere gratitude to:

* The dedicated Spring Boot team for developing such an exceptional and versatile framework.
* The innovative GraphQL community for pioneering a powerful and efficient API query language.
* The proficient Spring Security team for providing a robust and adaptable security framework.
* The supportive PostgreSQL community for their reliable and high-performance database system.
* The Docker community for providing a great platform for containerization.

---

<div align="center">
  <sub>Crafted with ❤️ and Spring Boot</sub>
</div>
