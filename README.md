# NASA Asteroid API

This project provides a RESTful API to retrieve information about asteroids from NASA.

## How to Run the Project

1. **Clone the Repository:**
   ```bash
   git clone <https://github.com/NoerAris/nasa-api.git>

2. **Build the Project:**
   ```bash
   cd <project-directory>
   
   #build jar
   mvn package -P jar
   
   #build war
   mvn package -P war
   
3. **Run application:**
   ```bash
   java -jar target/nasa-api.jar

4. **Access the API:**
   ```bash
   The application will be running at http://localhost:8025/nasa/api
   
   #or can access by swagger
   http://localhost:8025/swagger-ui.html#
   
   Endpoints
   1 . Top 10 Nearest Asteroids
      URL: http://localhost:8025/api/nasa/top-10-nearest-asteroids?start-date=2015-09-07&end-date=2015-09-08
      Method: GET
   2. Asteroid Details
      URL: http://localhost:8025/api/nasa/detail?id=3542519
      Method: GET
   