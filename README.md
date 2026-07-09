# 🍽 Restaurant Management System

A Java Console-Based Restaurant Management Application developed using **Core Java**, **JDBC**, **MySQL**, and **Clever Cloud** as a centralized cloud database.

---

## 🚀 Features

### Authentication

* Admin Login
* Manager Login
* Chef Login
* Customer Login
* Delivery Boy Login
* OTP Verification using Twilio
* BCrypt Password Encryption

---

### Customer Module

* Registration
* Login
* Browse Menu
* Add to Cart
* Update Cart
* Remove Items
* Checkout
* Payment
* Discount Coupons
* View Order History
* Feedback System

---

### Admin Module

* Manage Customers
* Manage Chefs
* Manage Delivery Boys
* Assign Orders
* Reports
* Analytics

---

### Manager Module

* Inventory Management
* Menu Management
* Order Monitoring
* Delivery Tracking

---

### Chef Module

* Assigned Orders
* Food Preparation
* Status Updates

---

### Delivery Module

* Assigned Deliveries
* Pickup Orders
* Delivery Tracking
* Order Completion

---

## 🏗 Tech Stack

| Technology     | Usage                 |
| -------------- | --------------------- |
| Java 21        | Backend               |
| JDBC           | Database Connectivity |
| MySQL          | Database              |
| Clever Cloud   | Centralized Database  |
| Maven          | Dependency Management |
| BCrypt         | Password Encryption   |
| Twilio         | OTP Verification      |
| JUnit 5        | Testing               |
| GitHub Actions | CI/CD                 |

---

## Database

Hosted on **Clever Cloud**

Contains

* Authentication
* Inventory
* Menu
* Cart
* Checkout
* Orders
* Payments
* Discounts
* Reports
* Notifications
* Delivery Assignments
* Chef Assignments

---

## Build

```bash
mvn clean install
```

Skip tests

```bash
mvn clean install -DskipTests
```

---

## Run

```bash
mvn exec:java "-Dexec.mainClass=org.restaurant.App"
```

or

```bash
java -cp target/classes org.restaurant.App
```

---

## Environment Variables

Create

```text
.env
```

Example

```env
DB_URL=
DB_USER=
DB_PASSWORD=

TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_PHONE_NUMBER=
```

---

## Contributors

| Member               | Role         |
| -------------------- | ------------ |
| Prasanna Venkatesh K | Project Lead |
| Muhindhar            | Developer    |
| Vithyamurugesan      | Developer    |
| itzMalx              | Developer    |
| Aaron83              | Contributor  |

---

## Future Enhancements

* REST APIs
* Spring Boot Migration
* Web UI
* Docker Deployment
* Kubernetes Deployment
* Grafana Monitoring

---

## License

MIT License

---
