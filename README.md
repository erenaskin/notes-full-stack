# Notes Fullstack Project

This project is a full-featured note-taking application consisting of a backend API developed with **Java (Spring Boot)**, a mobile application developed with **Flutter**, and a **PostgreSQL** database. The project adopts modern DevOps practices, is containerized with **Docker**, and has automated CI/CD processes with **GitHub Actions**.

---

## ✨ Key Features

- **Create, Read, Update, and Delete (CRUD) Notes:** Users can easily manage their notes.
- **Cross-Platform Mobile App:** Runs on both iOS and Android platforms thanks to Flutter.
- **RESTful API:** Clear and extensible backend services structured with standard HTTP methods.
- **Containerized Backend:** The backend application runs consistently in any environment thanks to Docker.
- **Automated CI/CD:** Every `push` to the `main` branch automatically triggers tests, builds, and the publishing of the Docker image.

---

## 🛠️ Tech Stack

- **Backend:**
  - **Java 17**
  - **Spring Boot 3**
  - **Maven** (Dependency Management)
  - **Spring Data JPA** (Database Access)
- **Frontend:**
  - **Flutter**
  - **Dart**
- **Database:**
  - **PostgreSQL**
- **DevOps:**
  - **Docker**
  - **GitHub Actions**

---

## 📂 Project Structure

```
notes-fullstack/
├── api/                  # Backend (Java/Spring Boot) project
│   ├── src/
│   ├── pom.xml           # Maven dependencies and project settings
│   └── Dockerfile        # File to build the backend application into a Docker image
│
├── notes/                # Frontend (Flutter) project
│   ├── lib/
│   └── pubspec.yaml      # Flutter dependencies and project settings
│
└── .github/
    └── workflows/
        └── ci-cd.yml     # GitHub Actions CI/CD workflow
```

---

## 🚀 Getting Started

Follow the steps below to run the project on your local machine.

### Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **Maven**
- **Flutter SDK**
- **Docker** and **Docker Compose**
- An IDE (IntelliJ IDEA, VS Code, etc.)

### 1. Clone the Project

```bash
git clone <project-url>
cd notes-fullstack
```

### 2. Running the Backend

#### Option A: With Docker (Recommended)

Create a `docker-compose.yml` file in the project's root directory. This file will launch both the backend application and the database with a single command.

**`docker-compose.yml` content:**
```yaml
version: '3.8'
services:
  postgres_db:
    image: postgres:18
    container_name: notes-postgres
    environment:
      POSTGRES_USER: your_postgres_user
      POSTGRES_PASSWORD: your_postgres_password
      POSTGRES_DB: your_db_name
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  notes_api:
    build:
      context: ./api
      dockerfile: Dockerfile
    container_name: notes-api
    depends_on:
      - postgres_db
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres_db:5432/your_db_name
      - SPRING_DATASOURCE_USERNAME=your_datasource_username
      - SPRING_DATASOURCE_PASSWORD=your_datasource_password

volumes:
  postgres_data:
```

Then, run the following command:
```bash
docker-compose up --build
```
This command will build the backend image using the `Dockerfile` in the `api` directory and start it along with the PostgreSQL service.

#### Option B: Locally

1.  **Start the Database:** Run a PostgreSQL server on your local machine and create a database named `notes_db`.
2.  **Application Settings:** Update your database connection details in the `api/src/main/resources/application.properties` file.
3.  **Build and Run the Application:**
    ```bash
    # Navigate to the api directory
    cd api
    # Build the project
    mvn clean package
    # Run the application
    java -jar target/*.jar
    ```

### 3. Running the Frontend

1.  **Navigate to the `notes` directory:**
    ```bash
    cd notes
    ```
2.  **Install dependencies:**
    ```bash
    flutter pub get
    ```
3.  **Run the app on an emulator or a physical device:**
    ```bash
    flutter run
    ```

---

## 🧪 Testing Strategy

This project adopts a layered testing strategy to ensure code quality, reliability, and maintainability. Our approach is based on the "Test Pyramid" model and includes the following three main types of tests.

All tests are organized under the `src/test/java` directory, maintaining the same package structure as the classes they test.

### 1. Unit Tests

- **Purpose:** To verify the functionality of a single class or method in complete isolation from external dependencies (database, other services, etc.).
- **Tools:** `JUnit 5`, `Mockito`, `AssertJ`.
- **Characteristics:**
    - They run very quickly because they do not start the Spring Context.
    - Dependencies are mocked using `Mockito`.
    - The focus is on the logical correctness of a specific piece of code.

### 2. Integration Tests

- **Purpose:** To test whether different components of the application (e.g., Controller → Service → Repository) work correctly together. These tests ensure that the application behaves consistently as a whole.
- **Tools:** `@SpringBootTest`, `TestRestTemplate`/`MockMvc`, `Testcontainers` or `H2 In-Memory Database`.
- **Characteristics:**
    - They provide a more realistic test environment by starting the Spring Application Context.
    - They test integration points such as database operations, API endpoints, and inter-service communication.
    - They run slower than unit tests.

#### Component Tests

As a subtype of integration tests, annotations like `@WebMvcTest` (for the Web layer only) or `@DataJpaTest` (for the JPA layer only) are also used to test only a specific layer (slice). This provides a faster feedback loop than full integration tests.

### 3. End-to-End Tests

- **Purpose:** To verify that the entire system works as expected by simulating a real user scenario from start to finish. These tests validate the application in a state that is closest to the live environment.
- **Tools:** `RestAssured` or `TestRestTemplate` for API tests; tools like `Selenium` or `Cypress` can be used for UI tests.
- **Characteristics:**
    - They are executed on a running instance of the application.
    - They are the slowest and most costly type of test.
    - They ensure that critical user flows (e.g., registration, placing an order) work smoothly.

### Running Tests

You can use the following command to run all tests in the project:

```shell
mvn clean test
```

To compile the project while skipping tests:
```shell
mvn clean install -DskipTests
```

---

## 🔄 CI/CD Process

This project has a CI/CD (Continuous Integration / Continuous Deployment) pipeline integrated with GitHub Actions.

- **Trigger:** Any `push` to the `main` branch.
- **Pipeline:**
  1.  **Test Stage:**
      - `test-backend`: Tests the backend code with Maven.
      - `test-frontend`: Validates the frontend code with Flutter tests.
  2.  **Build & Push Stage:**
      - If the tests pass, the `build-and-push-docker` job is triggered.
      - The backend project is packaged with Maven (a `.jar` file is created).
      - A Docker image is built using the `Dockerfile`.
      - The created image is pushed to Docker Hub using credentials defined in GitHub Secrets.

### Required GitHub Secrets

For the CI/CD pipeline to be able to push the image to Docker Hub, the following secrets must be defined in your project's GitHub repository:
- `DOCKERHUB_USERNAME`: Your Docker Hub username.
- `DOCKERHUB_TOKEN`: An Access Token generated from Docker Hub.

You can add these secrets from the **Repo > Settings > Secrets and variables > Actions** menu.
