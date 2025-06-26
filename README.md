# Khizana - M-Commerce Shopping App (User Side)

*Khizana* is a modern m-commerce Android application built with Kotlin and Jetpack Compose. It allows users to browse, search, and shop from a variety of products offered by different vendors. The app is integrated with Shopify's REST Admin API and supports full shopping functionality including cart, checkout, and authentication.

---

## 🛍 Project Description

Khizana presents products from various vendors stored in a ShopifyDB. It enables authenticated users to:

- Browse and filter products.
- Add/remove products to/from the cart.
- View product details, and ratings.
- Place orders with discount codes.
- Choose between payment methods including Cash on Delivery.
- Save addresses.
- Log in or register using email or google or continue as a guest.

---

## 📱 App Features

### Main Screen (3 Tabs)
#### 1. Home
- Search bar for product lookup.
- Display ads and scrollable brand list.
- display all products that belong to a spacific vendor.
- Access to cart and favorites.

#### 2. Categories
- Search, and favorites access.
- Display of main and sub-categories.
- Products filtered by category, price, or subcategory.

#### 3. Profile
- If *logged in*:
  - userName & password.
  - Summary of last 2 orders (with "See all").
  - 4 wish list items (with "See all").
- If *not logged in*:
  - Prompt to register or log in.
  - Restrict access to cart and favorites.

---

## 🧾 Product Details Screen
- Carousel of images.
- Name, sizes, price, and rating.
- Scrollable product description.
- Add to cart and favorite buttons.

---

## 🛒 Shopping Cart
- List of cart items with:
  - Image, name, price, quantity controls.
- Total price calculation.
- Checkout button.
- Delete and quantity change support with stock validation.

---

## ⚙️ Settings
- Currency selection (via live exchange rate API).
- Country selection from list.
- Logout option.

---

## 💳 Payment & Checkout
- Payment methods:
  - Cash on Delivery (limited)
  - Online payment
- Discount code support.
- Confirmation screen before placing order.
- Email confirmation after checkout.

---

## 🔐 Authentication
- Login/Register using:
  - Email & password
  - google
- Guest browsing allowed (limited functionality).
- Email verification on registration.

---

## 🧰 Technologies & Libraries

| Purpose | Tools & Libraries |
|--------|--------------------|
| UI | Jetpack Compose, Navigation Component |
| Architecture | MVVM, Clean Architecture |
| Networking | Retrofit, OkHttp, Shopify REST API |
| Image Handling | Coil |
| Auth | Firebase Authentication |
| Location | Google Places API / HERE SDK |
| State Management | ViewModel, StateFlow |
| Storage | SharedPreferences  |
| Testing | JUnit, Mockito, Robolectric |
| Language | Kotlin |
| Analytics & Tools | Firebase

---

## 📈 API Integration

- Shopify Admin REST API  
