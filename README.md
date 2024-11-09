# Configuration Manager

This project is a **Configuration Management System** that allows users to manage configuration files and dynamically apply changes to web pages. The project is split into two parts: a backend (Spring Boot) and a frontend (Vanilla JavaScript). 

## Table of Contents
- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Endpoints](#endpoints)
  - [POST /api/config/add](#post-apiconfigadd)
  - [PUT /api/config/update](#put-apiconfigupdate)
  - [DELETE /api/config/delete](#delete-apiconfigdelete)
  - [GET /api/config/all](#get-apiconfigall)
  - [GET /api/configuration/byFileName/{fileName}](#get-apiconfigurationbyfilename)
  - [GET /api/configuration/specificConfigFile](#get-apiconfigurationspecificconfigfile)
  - [GET /api/configuration/specific](#get-apiconfigurationspecific)
- [Contributing](#contributing)
- [License and Author](#license-and-author)

## Project Overview

The **Configuration Manager** system allows users to:
- Add and manage configuration actions (e.g., remove, replace, insert, alter text) via YAML configuration files.
- Dynamically apply these actions to HTML content based on user-defined conditions.
- Handle priority conflicts when multiple configurations target the same elements.

This repository contains:
- **Backend**: A Spring Boot application that exposes APIs to interact with configuration files.
- **Frontend**: A simple Vanilla JavaScript application that fetches the configurations and applies them to the webpage dynamically.

## Technologies Used

- **Backend**: Spring Boot, YAML, Jackson (JSON processing)
- **Frontend**: Vanilla JavaScript, Fetch API
- **Other**: YAML for configuration files

## Backend Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/berkalparslan/config-app.git
   cd config-app-backend
   ```

2. **Ensure Java 11 or higher is installed**: Check your Java version:
   ```bash
    java -version
    ```
Run the Backend Application: If you are using Maven, you can start the backend using:
   ```bash
    ./mvnw spring-boot:run
```

Backend API: The backend API will be running on http://localhost:8080. You can interact with the following endpoints:

- POST /api/configuration/add - Add a new configuration action to the YAML configuration file.
- PUT /api/configuration/update - Update an existing configuration file to a new action.
- DELETE /api/configuration/delete - Delete a configuration YAML file.
- GET /api/configuration/all - Retrieve all configuration actions.
- GET /api/configuration/byFileName/{fileName} - Retrieve all configuration actions by file name.
- GET /api/configuration/specificConfigFile - Retrieve a specific configuration file.(e.g., spesific.yaml)
- GET /api/configuration/specific - Retrieve a specific configuration file based on provided host and URL

## Frontend Setup

1. **Navigate to the Frontend Directory**:
   ```bash
    cd frontend
    ```
2. **Open the Frontend Application**: Open the index.html file in your browser. The frontend will automatically connect to the backend and apply the configurations dynamically to the page.

Interaction with Backend: The frontend uses the Fetch API to send requests to the backend and dynamically modify the DOM based on the responses. It applies the configurations such as replace, insert, remove, or alter based on the data fetched from the backend.

**No Backend?**:
   - Ensure the backend is running on the same machine and port as configured in the `index.html` (default is `localhost`).
   - If the backend is running on a different server, you may need to adjust the URLs in the front-end JavaScript code to point to the correct backend address.

The frontend part of this project consists of a simple HTML page (`index.html`) that interacts with a backend API. It fetches configuration data and applies changes to the page using JavaScript (`main.js`).

**Frontend Functionality**:
   - **`index.html`**: Contains the structure of the webpage and includes the `main.js` script.
   - **`main.js`**: Handles the API calls to fetch the configuration and apply the changes dynamically.
   
**Main JavaScript Functions**:
   - **`fetchConfiguration()`**: Fetches configuration data from the backend "config.yaml" file and applies it to the page.
   - **`applyConfigurations()`**: Processes the configuration and alters the page content accordingly.
   - **`fetchSpesificConfig(host, url)`**: Fetches configuration based on the host and URL provided by the user according to "spesific.yaml" in backend resources folder.

## Endpoints

### POST /api/config/add

Add a new configuration action to the YAML configuration file.

Request Body:

```json
{
    "type": "replace",
    "selector": "#old-header",
    "newElement": "<header id='new-header'>New Header</header>",
    "priority": 3
}
```

Response:
200 OK: Configuration file created successfully.
500 Internal Server Error: An error occurred during file creation.

### PUT /api/config/update

Description: Update an existing configuration file with a new action.

Request Body:

fileName (String) - The name of the configuration file to be updated.
config (Map<String, Object>) - The new configuration details to be applied.
Example:

Request Body:

```json
{
  "type": "insert",
  "position": "before",
  "target": "body",
  "element": "<footer>New Footer</footer>",
  "priority": 1
}
```

Response:
200 OK: Configuration file updated successfully.
400 Bad Request: The configuration file doesn't exist or is invalid.


### DELETE /api/config/delete

Description: Delete a configuration YAML file.
Request Params:
fileName (String) - The name of the configuration file to be deleted.
Response:
200 OK: Configuration file deleted successfully.
404 Not Found: The requested configuration file was not found.

### GET /api/config/all

Description: Retrieve all configuration actions from the system.

Response:

200 OK: A list of all configuration actions.

```json
[
  {
    "type": "replace",
    "selector": "#old-header",
    "newElement": "<header id='new-header'>New Header 1</header>",
    "priority": 3
  },
  {
    "type": "insert",
    "position": "after",
    "target": "body",
    "element": "<footer>Footer Content</footer>",
    "priority": 2
  }
]
```
### GET /api/configuration/byFileName

/api/configuration/byFileName/{fileName} 

Description: Retrieve a specific configuration file by its name.

Request Params:

fileName (String) - The name of the configuration file to be retrieved.
Response:

200 OK: The configuration file content in JSON format.
404 Not Found: The requested configuration file doesn't exist.

### GET /api/configuration/specificConfigFile
Description: Retrieve a specific configuration file in JSON format.
Response:
200 OK: The specific configuration file in JSON format.
404 Not Found: Configuration not found.

### GET /api/configuration/specific
Description: Retrieve a specific configuration file based on provided host and URL.

Request Params:

host (String) - The host name for which the configuration is needed.
url (String) - The URL for which the configuration is needed.
Response:

200 OK: A list of relevant configuration files based on host and URL.
404 Not Found: Host or URL configuration not found.

Example:

```json
{
  "hostConfigFile": "host1.yaml",
  "urlConfigFile": "url1.yaml"
}

```

## Contributing

We welcome contributions! Feel free to fork this repository, create issues, and submit pull requests. If you encounter any issues or have suggestions, please open an issue, and we will respond as soon as possible.

## License and Author

Ali Berk Alparslan