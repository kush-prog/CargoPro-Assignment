# Load & Booking Management System

A robust backend system built with Spring Boot and PostgreSQL for managing Load & Booking operations efficiently, optimized for performance, security, and scalability.

## 🚀 Features

- **Load Management**: Create, read, update, and delete load operations
- **Booking Management**: Handle booking requests with status transitions
- **Normalized Database Schema**: Foreign key relationships and constraints
- **Input Validation**: Comprehensive validation for all API endpoints
- **Pagination & Filtering**: Efficient data retrieval with query parameters
- **Status Transitions**: Automated status management based on business rules
- **Exception Handling**: Global exception handling with meaningful error messages
- **API Documentation**: Swagger/OpenAPI integration
- **High Test Coverage**: 60%+ test coverage with JUnit and Mockito

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2+
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Jakarta Bean Validation
- **Documentation**: Swagger/OpenAPI 3
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd load-booking-system
```

### 2. Database Setup

1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE loadbooking;
```

2. Update database credentials in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/loadbooking
    username: your_username
    password: your_password
```

### 3. Build and Run

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## 📊 Database Schema

### Load Entity
```sql
CREATE TABLE loads (
    id UUID PRIMARY KEY,
    shipper_id VARCHAR(255) NOT NULL,
    loading_point VARCHAR(255) NOT NULL,
    unloading_point VARCHAR(255) NOT NULL,
    loading_date TIMESTAMP NOT NULL,
    unloading_date TIMESTAMP NOT NULL,
    product_type VARCHAR(255) NOT NULL,
    truck_type VARCHAR(255) NOT NULL,
    no_of_trucks INTEGER NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    comment TEXT,
    date_posted TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'POSTED'
);
```

### Booking Entity
```sql
CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    load_id UUID NOT NULL,
    transporter_id VARCHAR(255) NOT NULL,
    proposed_rate DOUBLE PRECISION NOT NULL,
    comment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL,
    FOREIGN KEY (load_id) REFERENCES loads(id) ON DELETE CASCADE
);
```

## 🔗 API Endpoints

### Load Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/load` | Create a new load |
| GET | `/load` | Get loads with filters and pagination |
| GET | `/load/{loadId}` | Get load by ID |
| PUT | `/load/{loadId}` | Update load details |
| DELETE | `/load/{loadId}` | Delete a load |

**Query Parameters for GET /load:**
- `shipperId` (optional): Filter by shipper ID
- `truckType` (optional): Filter by truck type
- `status` (optional): Filter by status (POSTED, BOOKED, CANCELLED)
- `page` (default: 1): Page number for pagination
- `size` (default: 10): Number of items per page

### Booking Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/booking` | Create a new booking |
| GET | `/booking` | Get bookings with filters |
| GET | `/booking/{bookingId}` | Get booking by ID |
| PUT | `/booking/{bookingId}` | Update booking details |
| DELETE | `/booking/{bookingId}` | Delete a booking |

**Query Parameters for GET /booking:**
- `loadId` (optional): Filter by load ID
- `transporterId` (optional): Filter by transporter ID
- `status` (optional): Filter by status (PENDING, ACCEPTED, REJECTED)

## 📝 Sample API Requests

### Create Load
```json
POST /load
{
  "shipperId": "SHIPPER001",
  "facility": {
    "loadingPoint": "Delhi",
    "unloadingPoint": "Mumbai",
    "loadingDate": "2024-08-01T10:00:00",
    "unloadingDate": "2024-08-03T18:00:00"
  },
  "productType": "Electronics",
  "truckType": "Container",
  "noOfTrucks": 2,
  "weight": 5000.0,
  "comment": "Fragile items, handle with care"
}
```

### Create Booking
```json
POST /booking
{
  "loadId": "123e4567-e89b-12d3-a456-426614174000",
  "transporterId": "TRANSPORTER001",
  "proposedRate": 50000.0,
  "comment": "Interested in this load"
}
```

## 🔄 Business Rules

### Load Status Transitions
- **POSTED**: Default status when a load is created
- **BOOKED**: When a booking is made for the load
- **CANCELLED**: When all bookings are deleted or rejected

### Booking Status Transitions
- **PENDING**: Default status when a booking is created
- **ACCEPTED**: When a booking is approved
- **REJECTED**: When a booking is declined

### Validation Rules
- Cannot create booking for CANCELLED loads
- All bookings start with PENDING status
- When booking is accepted, load status becomes BOOKED
- If all bookings are deleted/rejected, load status reverts to POSTED or CANCELLED

## 🧪 Testing

The project includes comprehensive test coverage:

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report
```

**Test Categories:**
- **Unit Tests**: Service and repository layer testing
- **Integration Tests**: Controller and end-to-end testing
- **Mock Tests**: Using Mockito for dependency mocking

**Coverage Areas:**
- Service layer business logic
- Controller request/response handling
- Repository data access
- Exception handling scenarios
- Validation rules

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/assignment/loadbooking/
│   │   ├── LoadBookingSystemApplication.java
│   │   ├── config/
│   │   │   ├── ModelMapperConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/
│   │   │   ├── LoadController.java
│   │   │   └── BookingController.java
│   │   ├── dto/
│   │   │   ├── LoadDTO.java
│   │   │   ├── FacilityDTO.java
│   │   │   └── BookingDTO.java
│   │   ├── entity/
│   │   │   ├── Load.java
│   │   │   ├── Facility.java
│   │   │   ├── Booking.java
│   │   │   ├── LoadStatus.java
│   │   │   └── BookingStatus.java
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── BusinessException.java
│   │   │   ├── ErrorResponse.java
│   │   │   └── ValidationErrorResponse.java
│   │   ├── repository/
│   │   │   ├── LoadRepository.java
│   │   │   └── BookingRepository.java
│   │   └── service/
│   │       ├── LoadService.java
│   │       └── BookingService.java
│   └── resources/
│       ├── application.yml
│       └── application-test.yml
└── test/
    └── java/com/assignment/loadbooking/
        ├── controller/
        ├── service/
        ├── repository/
        └── integration/
```

## 🔧 Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/loadbooking
    username: ${DB_USERNAME:your_username}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

server:
  port: 8080

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Environment Variables
- `DB_URL`: Database connection URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `SERVER_PORT`: Application port (default: 8080)

## 🛡️ Security Considerations

- Input validation on all endpoints
- SQL injection prevention through JPA
- Error handling without sensitive information exposure
- Database constraints for data integrity

## 📈 Performance Optimizations

- Database indexing on frequently queried fields
- Pagination for large result sets
- Lazy loading for entity relationships
- Connection pooling with HikariCP

## 🏗️ Architecture

The application follows a layered architecture:

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic and rules
3. **Repository Layer**: Data access and database operations
4. **Entity Layer**: JPA entities and database mapping
5. **DTO Layer**: Data transfer objects for API communication

This architecture ensures separation of concerns, maintainability, and testability.
