# Lab 1 Git Race -- Project Report

## Description of Changes

### 1. Time-based Greeting
- The greeting message changes dynamically based on the time of day.  
**Greetings:**
  - **06:00 – 11:59** → *Good Morning*  
  - **12:00 – 17:59** → *Good Afternoon*  
  - **18:00 – 05:59** → *Good Night*  

---

### 2. Data Persistence
- Data (greeting messages, user information, etc.) is persisted in an **embedded, file-stored H2 database**.

---

### 3. User Authentication
- Users can sign up, log in, and log out.  
- Implemented with **Spring Security**, based on **server-side sessions**.  
- Sessions are extended with the default **remember-me** configuration.  

---

### 4. Greeting History
- Authenticated users can view the history of greetings they have sent.  
- Each record includes:
  - **Timestamp**  
  - **Endpoint type used**  

---

### 5. Statistics
- **Anonymous users**: Can obtain *global statistics*.  
- **Authenticated users**: Can obtain statistics limited to their own greetings.  

**Provided statistics include:**
- Total greetings stored (global or by current user)  
- Endpoints ranked by number of names (global or by current user)  
- User roles ranked by number of names (global only)  
- Times of day ranked by number of names (global or by current user)  
- Top 3 dates with most greetings (global or by current user)  
- Top 3 hours with most greetings (global or by current user)  
- Total names used in greetings (global or by current user)  
- Top 3 names by frequency (global or by current user)  

---

### 6. Additional API Endpoints
New REST API endpoints were introduced to support the latest features:

- **Authentication info:** `/api/whoami`  
- **Greeting history:** `/api/history`  
- **Statistics:** `/api/statistics`, `/api/myStatistics`  

---

### 7. Rate Limiting
- All API endpoints are limited to **50 requests per minute**.  
- **Authenticated users:** The limit applies across all clients logged in with the same account.  
- **Anonymous users:** The client IP is used to enforce the limit.  

---

### 8. OpenAPI Documentation
- All API endpoints are documented using **Swagger/OpenAPI**.  
- Documentation is available at:  
  **`/swagger-ui/index.html`**  

---

### 9. GitHub Actions
- On every commit to the main branch, **GitHub Actions** runs automated tests.  
- Ensures all features continue to work correctly after changes.  

---

### 10. HTTP Status Codes
- API responses follow standard **HTTP status codes**.  
- Example: `/api/hello` now responds with **201 (Created)** since a new *Greeting* resource is created and stored.  

---

## Technology Stack 

## Technical Decisions

---

#### **Spring Security** 
Initially, authentication was designed using **JWT tokens**. However, since the project uses **Spring Boot + Thymeleaf**, which are server-side heavy (all views are served by server controllers), it was decided to use the already implemented authentication technology provided by **Spring Security** instead of maintaining a custom JWT solution.

---

#### BCrypt
Spring Security requires a password encoder for hashing and verifying passwords. Since **BCrypt** had been successfully used in past Java projects, it was chosen again for this application to ensure secure and familiar password handling.

---

#### Bucket4j 
After reviewing documentation on rate limiting, it was decided to use **Bucket4j** instead of implementing a custom bucket system from scratch. This reduced development effort while ensuring a reliable and proven solution.

---

#### Swagger / OpenAPI
Since **Swagger/OpenAPI** had already been used successfully in past projects, the same technology was adopted again to document the API. This ensured consistency, familiarity, and reduced onboarding time for developers.

---

#### Mockito
Mockito was chosen for mocking dependencies in tests to keep the test suite fast, isolated, and maintainable. Its wide adoption and strong integration with **JUnit 5** made it the natural choice for this project.

---

## Learning Outcomes

---

### Kotlin & Spring Boot Architectural Layers
- Applied for the first time **Kotlin** (a new technology) in a project.  
- Learned how Spring Boot structures applications through delegation and layering:
  - **Entities** → Representing domain data (Spring Data JPA)
  - **Repositories** → Data persistence layer   
  - **Services** → Business logic encapsulation  
  - **Controllers** → Handling HTTP requests and responses  

---

### Server-Side Web Implementation
- Understood how to build **server-rendered applications** using **Spring Boot + Thymeleaf**.  
- Learned how to integrate backend logic directly with frontend templates, instead of greater frontend
and backend separation.  

---

### Spring Security
- Gained hands-on experience with authentication and authorization in Spring applications.  
- Learned how to configure server-side session-based login and logout. 

---

### API Rate Limiting
- Learned the importance of preventing abuse of public APIs, and the different ways of implementation.  
- Implemented a basic rate limiting with **Bucket4j** to control request flow.  

---

### Testing Fundamentals
- Acquired knowledge of different types of tests and their purposes:  
  - **Unit Tests** → Testing individual classes and methods  
  - **JPA Tests** → Validating persistence layer with in-memory DB  
  - **MockMvc Tests** → Verifying controller behavior without running a full server  
  - **Integration Tests** → Ensuring multiple layers work together correctly  

---

## AI Disclosure
### AI Tools Used
- **ChatGPT (OpenAI)**

---

### AI-Assisted Work
- **Documentation & Explanations**:  
  AI was used to generate project documentation, clarify concepts (for example, authentication approaches, API rate limiting different implementations), and explain best practices.  
- **Frontend (HTML/Thymeleaf Fragments)**:  
  Assistance was received in writing HTML and Thymeleaf templates, as frontend development is not my strongest area.  
- **Testing**:  
  Since I have not studied testing in depth, AI was used to help design and implement tests beyond basic unit tests (for example, MockMvc tests, integration tests).  
- **Debugging**:
  Finally, for complex errors originated by the technologies used, for example Spring Security, AI was consulted for ways to fix them.

**Estimated Contribution:** ~50% AI-assisted  
**Modifications Made:** All AI-generated content was reviewed and adapted first, before integrating it into the project according to the project's needs.  

---

### Original Work
- **Business Logic & Entities**:  
  Implemented core application logic and domain entities independently.  
- **Technologies Already Familiar With**:  
  Set up and worked with JPA and database persistence based on prior experience.  
- **Learning Process**:  
  Gained practical knowledge of Spring Security, Kotlin + Spring Boot application layering, and API rate limiting by actively integrating and adapting AI suggestions into the project.  
