[Weather-Serice](http://localhost:8082/weather/data?q=London,us)

This is a demo application to get weather statistics for given city

#### To build this project use
    mvn install

#### Run this project with maven
    mvn spring-boot:run

#### Run Spring Boot app with java -jar command
    java -jar target/weather-service-0.0.1-SNAPSHOT.jar

#### Application end points
    http://localhost:8082/data?q=London,us
    Here "q" represent the city for which weather statistics is needed.
    
#### Swagger End points
	http://localhost:8082/v2/api-docs
	http://localhost:8082/swagger-ui.html

#### Solution Explanation
    Here WeatherService class represent Spring Service class that encapsulate business logic.
    As of now WeatherService depends upon extenal service OpenWeatherMapService Facade class to get the weather forcasting details.
    
  - Statistics calulation algorithm
    1. Filter all the weather details which does not belong to next 3 days of data.
    2. Transform the complex response from external service in to simple pojo which contains the metrix required to calculate final output only
    3. Group the transformed pojo by date.
    4. For each date in previous group, calculate the total of morning temperature, evening temperature, pressure and number of morning and evening enteries.
    5. Based on the metrics calculated in last step for each date, calculate the following metrics
        
        Avg morning temp of day = (total morning temp of day)/(No of morning entries in a day)
        
        Avg evening temp of day = (total evening temp of day)/(No of evening entries in a day)
        
        Avg pressure of day = (total pressure of day)/(No of morning entries in a day + No of evening entries in a day)