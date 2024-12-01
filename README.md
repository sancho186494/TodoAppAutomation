# **Todo App Autotesting Project**

This repository contains the automated tests for the **Todo App** REST API. The tests are written in **Java 17** using **TestNG** as the testing framework and **Gradle** as the build tool.

---

## **Features**
- Automated test cases for:
    - Retrieving todos (`GET /todos`)
    - Creating todos (`POST /todos`)
    - Updating todos (`PUT /todos/{id}`)
    - Deleting todos (`DELETE /todos/{id}`)
    - WebSocket tests (`/ws`)
    - Handling error scenarios
- Clear and maintainable test structure
- Easily extendable for new features or endpoints

---

## **Technology Stack**
- Java 17: Programming language
- TestNG: Testing framework
- Gradle: Build automation tool
- Retrofit2: API interaction library
- OkHttp3: WebSocket connection library

## **Prerequisites**
Before you can run the tests, ensure you have the following installed on your system:
- **Java 17+**
- A running instance of the **Todo App API** (default: `http://localhost:8080`)

---

## **Setup Instructions**
1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/todo_app_autotests.git
   cd todo_app_autotests
   ```

2. **Build the project using Gradle:**
   ```bash
   ./gradlew build
   ```

---

## **Running Tests**
1. **Run all tests:**
   ```bash
   ./gradlew test
   ```

2. **Run specific TestNG suites:**
   ```bash
   ./gradlew test --tests PostTodosTest
   ```

3. **After running tests, the test report will be available at:**
   ```bash
   build/reports/tests/test/index.html
   ```