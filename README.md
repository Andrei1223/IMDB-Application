# IMDB Clone: Movie & Actor Database Management System

## Overview
The IMDB Clone is a comprehensive Object-Oriented Java application that simulates the core functionalities of the Internet Movie Database. It provides a robust platform for managing and interacting with a vast collection of movies, TV series, and actors.

Built with scalability and clean architecture in mind, the application features a Role-Based Access Control (RBAC) system, an interactive issue-tracking (request) system, and dynamic user engagement features like rating and experience points. Users can interact with the platform through a Java Swing GUI or a fast CLI.

## Key Features

### Role-Based Access
* **Regular Users:** Can browse the catalog, manage their favorites list, create issue requests, and rate productions.
* **Contributors:** Can add new movies, series, and actors to the system, update their existing contributions, and resolve user requests related to their data.
* **Administrators:** Have global access to add, update, or remove any user, production, or actor from the system, and manage general system requests.

### Content & Engagement
* **Comprehensive Catalog:** Supports detailed entries for Movies (with release years and durations) and Series (structured by seasons and episodes).
* **Actor Profiles:** View actor biographies and their filmography.
* **Rating System:** Users can leave ratings (1-10) and comments on productions.
* **Experience System (XP):** Users level up and gain experience by contributing to the platform (adding content, resolving requests, or writing reviews).

### System Operations
* **Ticketing/Request System:** Users can report missing information or request account deletions, routing the ticket directly to the responsible admin or contributor.
* **Real-time Notifications:** Users are alerted when their requests are resolved or when their added productions receive new reviews.
* **Search & Filtering:** Robust searching capabilities and dynamic filtering for productions (e.g., by genre or number of ratings).

## Tech Stack & Architecture

* **Language:** Java 17 
* **Build Tool:** Gradle 
* **UI Framework:** Java Swing 
* **Data Parsing:** JSON.Simple & Jackson
* **Design Patterns Implemented:** * **Singleton:** Manages the central database instance.
    * **Factory:** Dynamically creates the appropriate user objects upon login.
    * **Builder:** Constructs complex user profiles step-by-step.
    * **Observer:** Powers the internal notification event system.
    * **Strategy:** Calculates dynamic experience point allocation based on user actions.

## Getting Started

### Prerequisites
* Java Development Kit (JDK) 17 or higher.
* Git (for cloning the repository).

### Installation & Running
This project uses the Gradle Wrapper, meaning you do not need to install Gradle globally on your machine.

1. **Clone the repository:**
   ```
   git clone [https://github.com/Andrei1223/IMDB-Application.git]
   cd imdb-clone
   ```

2. **Run the application:**
You can run the application directly using the Gradle wrapper, which will automatically download necessary dependencies and compile the code.

 - Linux/macOS:
```
./gradlew run
```
 - Windows:

```
gradlew.bat run
```

###  Data Structure
The application loads its initial state from standard JSON files located in the src/main/resources/input/ directory:


 - accounts.json: User credentials and roles.

- actors.json: Actor biographies and filmographies.

- production.json: Movie and series details.

- requests.json: Initial state of the issue tracking system.
