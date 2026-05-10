# 🚗 ParkZen Backend — Smart Parking System

Production-ready Spring Boot 3 backend for the ParkZen Smart Parking System.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL (Aiven) |
| Payment | Razorpay (Test Mode) |
| Build | Maven |
| Deployment | Docker + Render |

---

## 📁 Project Structure

```
parkzen-backend/
├── src/main/java/com/parkzen/
│   ├── config/          # SecurityConfig, AppConfig, RazorpayConfig
│   ├── controller/      # AuthController, UserController, OwnerController, AdminController
│   ├── dto/
│   │   ├── request/     # All request DTOs
│   │   └── response/    # All response DTOs
│   ├── entity/          # JPA entities (User, Owner, Admin, Booking, Payment, ...)
│   ├── enums/           # Role, BookingStatus, PaymentStatus, SlotStatus, ...
│   ├── exception/       # GlobalExceptionHandler + custom exceptions
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
│   ├── service/         # Service interfaces
│   ├── service/impl/    # Service implementations
│   └── util/            # EntityMapper, QRCodeUtil, SecurityUtil
├── src/main/resources/
│   ├── application.properties
│   └── data.sql
├── Dockerfile
├── render.yaml
├── mysql-schema.sql
├── .env.example
└── pom.xml
```

---

## ⚙️ Environment Variables

Copy `.env.example` to `.env` and fill in:

```env
DB_URL=jdbc:mysql://your-host:3306/parkzen_db?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your-super-secret-jwt-key-minimum-32-characters
RAZORPAY_KEY=rzp_test_xxxxxxxxxxxxxxxx
RAZORPAY_SECRET=your_razorpay_test_secret
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

---

## 🗄️ Database Setup (Aiven MySQL)

1. Create a MySQL service on [Aiven](https://aiven.io)
2. Download the SSL CA certificate
3. Run the schema:
   ```bash
   mysql -h your-host -P 3306 -u username -p parkzen_db < mysql-schema.sql
   ```
4. Set your `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` env vars

---

## 🚀 Run Locally

### Prerequisites
- Java 21
- Maven 3.9+
- MySQL running

### Steps

```bash
# 1. Clone the repo
git clone https://github.com/your-org/parkzen-backend.git
cd parkzen-backend

# 2. Set environment variables (Linux/Mac)
export DB_URL=jdbc:mysql://localhost:3306/parkzen_db
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=parkzen-secret-key-minimum-32-chars-long
export RAZORPAY_KEY=rzp_test_xxxx
export RAZORPAY_SECRET=xxxx

# 3. Run
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## 🐳 Run with Docker

```bash
docker build -t parkzen-backend .

docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host:3306/parkzen_db \
  -e DB_USERNAME=user \
  -e DB_PASSWORD=pass \
  -e JWT_SECRET=your-secret \
  -e RAZORPAY_KEY=rzp_test_xx \
  -e RAZORPAY_SECRET=xx \
  parkzen-backend
```

---

## ☁️ Deploy to Render

1. Push to GitHub
2. Go to [render.com](https://render.com) → New Web Service
3. Connect your repo
4. Set environment variables from `.env.example`
5. Render auto-detects `render.yaml` and builds with Docker

Production URL: `https://parkzen-backend.onrender.com`

---

## 🔑 Default Admin Credentials

```
Email:    admin@parkzen.com
Password: admin@123
```

⚠️ Change in production!

---

## 📡 API Reference

### Base URLs
- Local: `http://localhost:8080/api`
- Production: `https://parkzen-backend.onrender.com/api`

### Auth Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/user/register` | Register user |
| POST | `/auth/user/login` | Login user |
| POST | `/auth/owner/register` | Register owner |
| POST | `/auth/owner/login` | Login owner |
| POST | `/auth/admin/login` | Admin login |

### User Endpoints (Bearer Token required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/user/parkings/nearby?lat=&lng=&radius=` | Find nearby parkings |
| GET | `/user/parking/{id}` | Get parking details |
| GET | `/user/slots/{parkingId}` | Get available slots |
| POST | `/user/book-slot` | Book a slot |
| POST | `/user/payment/create-order` | Create Razorpay order |
| POST | `/user/payment/verify` | Verify payment |
| GET | `/user/ticket/{bookingId}` | Get booking ticket (QR) |
| PUT | `/user/booking/extend/{id}` | Extend booking |
| GET | `/user/bookings/history` | Booking history |
| POST | `/user/review/add` | Add review |
| POST | `/user/complaint/add` | Submit complaint |
| POST | `/user/contact-admin` | Message admin |

### Owner Endpoints (Bearer Token required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/owner/dashboard` | Dashboard analytics |
| POST | `/owner/slot/add` | Add parking slot |
| PUT | `/owner/slot/update/{id}` | Update slot |
| DELETE | `/owner/slot/delete/{id}` | Delete slot |
| PUT | `/owner/slot/status/{id}?status=` | Update slot status |
| GET | `/owner/bookings` | View bookings |
| GET | `/owner/payments` | View payments |
| GET | `/owner/analytics` | Revenue analytics |
| GET | `/owner/reviews` | View reviews |

### Admin Endpoints (Bearer Token required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/dashboard` | System dashboard |
| GET | `/admin/users` | All users |
| GET | `/admin/owners` | All owners |
| GET | `/admin/bookings` | All bookings |
| GET | `/admin/payments` | All payments |
| PUT | `/admin/owner/approve/{id}` | Approve owner |
| PUT | `/admin/owner/reject/{id}` | Reject owner |
| GET | `/admin/complaints` | All complaints |
| POST | `/admin/send-message-owner` | Message owner |
| POST | `/admin/fire-alert` | Broadcast fire alert |
| POST | `/admin/parking-full-alert` | Broadcast full alert |

---

## 💳 Razorpay Payment Flow

1. User books a slot → `POST /user/book-slot` → returns `bookingId`
2. Create order → `POST /user/payment/create-order?bookingId={id}` → returns `razorpayOrderId`
3. Frontend opens Razorpay checkout with `razorpayOrderId`
4. Verify → `POST /user/payment/verify` with `razorpayOrderId`, `razorpayPaymentId`, `razorpaySignature`
5. Booking status changes to `CONFIRMED`

---

## 💰 Pricing Formula

```
Total Amount = (pricePerHour × durationHours)
             + (chargingPricePerHour × durationHours)  ← if charging slot selected
```

---

## 🧪 Testing with Postman

1. Import the API collection
2. Set `baseUrl` variable to `http://localhost:8080/api`
3. Register/login to get JWT token
4. Set `Authorization: Bearer {token}` header for protected routes

---

## 📋 Response Format

All responses follow this wrapper:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {}
}
```

---

## 🔒 Roles & Access

| Role | Access |
|---|---|
| `ROLE_USER` | `/api/user/**` |
| `ROLE_OWNER` | `/api/owner/**` (only after admin approval) |
| `ROLE_ADMIN` | `/api/admin/**` |

