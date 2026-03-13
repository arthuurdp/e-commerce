CREATE TABLE states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    region VARCHAR(255) NOT NULL
);

CREATE TABLE cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    state_id BIGINT NOT NULL,
    CONSTRAINT fk_cities_state FOREIGN KEY (state_id) REFERENCES states(id)
);

CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    phone VARCHAR(11),
    birth_date DATE,
    gender VARCHAR(255),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password_change_verified BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(255) NOT NULL,
    cart_id BIGINT NOT NULL,
    CONSTRAINT fk_users_cart FOREIGN KEY (cart_id) REFERENCES cart(id)
);

CREATE TABLE address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    street VARCHAR(255) NOT NULL,
    number INT NOT NULL,
    complement VARCHAR(255) NOT NULL,
    neighborhood VARCHAR(255) NOT NULL,
    postal_code VARCHAR(8) NOT NULL,
    city_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES cities(id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE email_verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    pending_email VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_email_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE password_verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    pending_password VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_password_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(6) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    stock INT NOT NULL,
    created_at DATETIME NOT NULL,
    last_updated_at DATETIME,
    weight DOUBLE NOT NULL,
    width INT NOT NULL,
    height INT NOT NULL,
    length INT NOT NULL
);

CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    is_main BOOLEAN NOT NULL DEFAULT FALSE,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE product_category (
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product_category_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_product_category_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT,
    product_id BIGINT NOT NULL,
    quantity INT,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES cart(id),
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    method VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    transaction_id VARCHAR(255),
    paid_at DATETIME,
    created_at DATETIME NOT NULL
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    total DECIMAL(19, 2) NOT NULL,
    payment_id BIGINT,
    origin_state_id BIGINT NOT NULL,
    created_at DATETIME,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES address(id),
    CONSTRAINT fk_orders_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
    CONSTRAINT fk_orders_origin_state FOREIGN KEY (origin_state_id) REFERENCES states(id)
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    subtotal DECIMAL(19, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE shippings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    me_order_id VARCHAR(255),
    carrier VARCHAR(255),
    tracking_code VARCHAR(255),
    tracking_url VARCHAR(255),
    label_url VARCHAR(255),
    shipping_cost DECIMAL(10, 2),
    posted_at DATETIME,
    delivered_at DATETIME,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_shippings_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
