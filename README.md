# Vehicle Identification System

##  Project Overview

A comprehensive **JavaFX desktop application** for managing vehicle records, customer information, workshop services, insurance policies, and police reports. This system provides an all-in-one solution for vehicle lifecycle management.

##  Team Members

| Name              | Role               |
|-------------------|--------------------|
| Kopano Tsiane     | Lead Developer     |
| Bakuena Mohapi    | Database Designer  |
| Motlatsi Mohami   | System Tester      |
| Rorisang Thakholi | UI/UX Designer     |
| Mohau Qoane       | System Analyst     |
| Karabo Kuena      | System coordinator |


##  Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| JavaFX | 17 | Frontend UI Framework |
| PostgreSQL | 14+ | Backend Database |
| JDBC | - | Database Connectivity |
| Maven | 3.6+ | Build Tool |
| Git & GitHub | - | Version Control |

##  Features

### Core Modules

| Module | Features |
|--------|----------|
| **Vehicle Management** | Add, Update, Delete, Search vehicles |
| **Customer Management** | Customer CRUD with validation |
| **Workshop Services** | Service records and customer queries |
| **Insurance Tracking** | Policies, Claims, Coverage details |
| **Police Records** | Reports, Violations, Fine tracking |

### Additional Features

-  **Dashboard** - Interactive charts and statistics
-  **Browse Records** - Full pagination with 15 records per page
-  **Search & Filter** - Find records quickly
-  **Export to CSV** - Save data for reports
- ️ **Print Support** - Print any view
-  **Keyboard Shortcuts** - Ctrl+D, Ctrl+V, Ctrl+B, etc.
-  **Professional UI** - Clean, modern interface


## ️ Database Schema

### Tables Created

| Table | Description |
|-------|-------------|
| `Customer` | Customer information |
| `Vehicle` | Vehicle registration details |
| `ServiceRecord` | Workshop service history |
| `InsurancePolicy` | Insurance policies |
| `Claim` | Insurance claims |
| `PoliceReport` | Police incident reports |
| `Violation` | Traffic violations |

### Views Created (10+)

- `VehicleDetailsView` - Complete vehicle info
- `InsuranceStatusView` - Active/Expired policies
- `UnpaidViolationsView` - Outstanding fines
- `MonthlyServiceRevenueView` - Revenue analytics

### Procedures Created

- `AddNewVehicle()` - Validated vehicle insertion
- `MarkViolationAsPaid()` - Update violation status

##  Installation & Setup

### Prerequisites

```bash
# Java 17+
java --version

# PostgreSQL 14+
psql --version

# Maven 3.6+
mvn --version







##  Project Structure

VehicleIdentification/
├── src/
│ ├── main/
│ │ ├── java/com/example/vehicleidentification/
│ │ │ ├── MainApp.java
│ │ │ ├── controller/ (9 controllers)
│ │ │ ├── model/ (4 model classes)
│ │ │ └── dao/ (Database connection)
│ │ └── resources/
│ │ ├── fxml/ (9 FXML layouts)
│ │ └── css/ (Stylesheets)
├── pom.xml
├── module-info.java
└── README.md


git clone https://github.com/YOUR_USERNAME/VehicleIdentificationSystem.git
cd VehicleIdentificationSystem


CREATE DATABASE vehicle_identification_system;
\c vehicle_identification_system;

private static final String USER = "postgres";
private static final String PASSWORD = "qwerty123456";


🖥️ Screenshots
Welcome Screen
[Add screenshot here]

Main Dashboard with Charts
[Add screenshot here]

Vehicle Management
[Add screenshot here]

Browse Records with Pagination
[Add screenshot here]

🐛 Troubleshooting
Issue	Solution
Database connection fails	Check PostgreSQL is running and credentials correct
CSS not loading	Verify style.css is in resources/css/
FXML errors	Run mvn clean compile
📞 Contact
Email: your.email@example.com

GitHub: Your GitHub Profile
