-- Derby schema for Hotel Management System (JDBC)

CREATE TABLE users (
    userid INT GENERATED ALWAYS AS IDENTITY (START WITH 1458743, INCREMENT BY 1)
        CONSTRAINT pk_users PRIMARY KEY,
    fullname VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    mobile VARCHAR(20) NOT NULL UNIQUE,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(12) NOT NULL DEFAULT 'customer' CONSTRAINT chk_users_role CHECK (role IN ('customer','admin','staff')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    roomno INT CONSTRAINT pk_rooms PRIMARY KEY,
    userid INT REFERENCES users (userid) ON DELETE SET NULL ON UPDATE NO ACTION,
    status VARCHAR(12) DEFAULT 'VACANT' CONSTRAINT chk_rooms_status CHECK (status IN ('VACANT','OCCUPIED','MAINTENANCE')),
    availabilitydate DATE,
    type VARCHAR(20) NOT NULL CONSTRAINT chk_rooms_type CHECK (type IN ('SINGLE','DOUBLE','TWIN','SUITE','DELUXE','FAMILY')),
    price DECIMAL(10,2) NOT NULL CONSTRAINT chk_rooms_price CHECK (price >= 0.00)
);

CREATE TABLE reservations (
    reservationid INT GENERATED ALWAYS AS IDENTITY (START WITH 260100, INCREMENT BY 1)
        CONSTRAINT pk_reservations PRIMARY KEY,
    userid INT NOT NULL REFERENCES users (userid) ON DELETE CASCADE ON UPDATE NO ACTION,
    roomno INT NOT NULL REFERENCES rooms (roomno) ON DELETE NO ACTION ON UPDATE NO ACTION,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status VARCHAR(12) DEFAULT 'BOOKED' CONSTRAINT chk_res_status CHECK (status IN ('BOOKED','CANCELLED')),
    upcoming DATE,
    CONSTRAINT chk_res_dates CHECK (check_out_date > check_in_date)
);

CREATE TABLE transactions (
    transactionid INT GENERATED ALWAYS AS IDENTITY (START WITH 98765000, INCREMENT BY 1)
        CONSTRAINT pk_transactions PRIMARY KEY,
    paydate DATE DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL CONSTRAINT chk_tx_status CHECK (status IN ('SUCCESS','FAILED','PENDING','REFUNDED')),
    mode VARCHAR(20) NOT NULL CONSTRAINT chk_tx_mode CHECK (mode IN ('UPI','CARD','CASH','NETBANKING','WALLET')),
    amount DECIMAL(10,2) NOT NULL CONSTRAINT chk_tx_amount CHECK (amount >= 0.00)
);

CREATE TABLE bills (
    billid INT GENERATED ALWAYS AS IDENTITY (START WITH 321567, INCREMENT BY 1)
        CONSTRAINT pk_bills PRIMARY KEY,
    reservationid INT NOT NULL REFERENCES reservations (reservationid) ON DELETE CASCADE ON UPDATE NO ACTION,
    amount DECIMAL(10,2) NOT NULL CONSTRAINT chk_bill_amount CHECK (amount >= 0.00),
    addcharges DECIMAL(10,2) DEFAULT 8.00 CONSTRAINT chk_bill_add CHECK (addcharges >= 0.00),
    paystatus VARCHAR(20) DEFAULT 'UNPAID' CONSTRAINT chk_bill_status CHECK (paystatus IN ('UNPAID','PAID','PARTIALLY_PAID','REFUNDED','CANCELLED')),
    transactionid INT REFERENCES transactions (transactionid) ON DELETE SET NULL ON UPDATE NO ACTION,
    createdat TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE complaints (
    complaintid INT GENERATED ALWAYS AS IDENTITY (START WITH 100, INCREMENT BY 1)
        CONSTRAINT pk_complaints PRIMARY KEY,
    userid INT NOT NULL REFERENCES users (userid) ON DELETE NO ACTION ON UPDATE NO ACTION,
    contact VARCHAR(18) NOT NULL,
    roomno INT,
    category VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status VARCHAR(25) DEFAULT 'OPEN' CONSTRAINT chk_complaint_status CHECK (status IN ('OPEN','IN_PROGRESS','RESOLVED','CLOSED','CANCELLED')),
    createdat DATE DEFAULT CURRENT_DATE,
    resolvedat DATE
);

-- Useful index for searches
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_reservations_room ON reservations(roomno);

