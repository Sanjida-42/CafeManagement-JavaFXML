# Café Shop Management System

A simple desktop app built with JavaFX to help small cafés manage their daily work. It includes easy sign-up/login, product tracking, ordering with carts, coupons for discounts, and a dashboard to see important stats like sales and stock levels.


## Features

- **User Sign-Up and Login**: Create an account or log in securely to use the app.
- **Dashboard**: Shows quick stats like number of products, total orders, revenue, coupons used, total savings from discounts, and low-stock items.
- **Inventory Management**: Add, update, or delete products with details like name, type, stock, price, status (available/not available), and image.
- **Menu and Ordering**: Browse products as cards, add to cart, apply coupons for discounts (e.g., 10% off), and place orders with automatic stock updates.
- **Coupon System**: Get a new coupon code after each order (e.g., "CAFE10-XYZ123" for 10% off next time). Apply codes during checkout to save money.
- **Customer Management**: View customer details like name, email, order count, and total spent.
- **Easy Interface**: Clean design with alerts for success or errors, making it simple for café staff to use.

## Technologies Used

- **Java Development Kit (JDK)**: Version 24 (for running the app).
- **JavaFX SDK**: For building the user interface.
- **FXML**: For designing screens (used with Scene Builder).
- **MySQL Database**: For storing data like users, products, orders, and coupons.
- **IDE**: NetBeans (for coding) and Scene Builder (for UI design).

## Setup

1. **Install JDK 24**: Download from [Oracle's website](https://www.oracle.com/java/technologies/downloads/).
2. **Install JavaFX SDK**: Download from [Gluon](https://gluonhq.com/products/javafx/) and add it to your project.
3. **Set Up MySQL**: Install MySQL (free from [MySQL site](https://dev.mysql.com/downloads/)). Create a database named "cafe-shop" (the app will make tables automatically).
4. **Open in NetBeans**: Import the project folder, add JavaFX libraries, and run the main class (CafeShopMain.java).

Run the app—it connects to the local database and starts!

## Usage

1. **Start the App**: Run it from NetBeans. You'll see the login screen.
2. **Sign Up or Log In**: New users sign up with name, email, password. Existing users log in.
3. **Navigate Sections**:
   - **Dashboard**: View stats like revenue and low stock.
   - **Inventory**: Add/edit products.
   - **Menu**: Browse items, add to cart, apply coupon, pay.
   - **Customers**: See customer info.
4. **Place an Order**: In Menu, add items, enter coupon if you have one, click Pay—stock updates, and a new coupon is generated.
5. **Logout**: From the sidebar to end session.

The app shows alerts for actions like "Order successful!" or errors. It's designed to be easy for café owners and staff. 


--- 


