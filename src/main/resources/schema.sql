-- Καθαρισμός παλιών πινάκων (αν υπάρχουν) για καθαρή εγκατάσταση
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS phone_catalogue CASCADE;
DROP TABLE IF EXISTS staff CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS app_logs CASCADE;

--1. Πίνακας Δωματίων
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type VARCHAR(40),
    floor INTEGER,
    occupancy INTEGER,
    price_per_night DECIMAL(10,2) -- (ΣΥΝΟΛΟ ΨΗΦΙΩΝ, ΨΗΦΙΑ ΜΕΤΑ ΤΗΝ ΥΠΟΔΙΑΣΤΟΛΗ)
);

-- 0. Συνάρτηση που καταγράφει την κίνηση
CREATE OR REPLACE FUNCTION log_activity() RETURNS TRIGGER AS $$
DECLARE
    descText TEXT;
BEGIN
    -- Ανάλογα με την ενέργεια, φτιάχνουμε μια περιγραφή
    IF (TG_OP = 'INSERT') THEN
        descText := 'New entry added to ' || TG_TABLE_NAME;
    ELSIF (TG_OP = 'UPDATE') THEN
        descText := 'Updated entry in ' || TG_TABLE_NAME;
    ELSIF (TG_OP = 'DELETE') THEN
        descText := 'Deleted entry from ' || TG_TABLE_NAME;
    END IF;

    -- Καταγραφή στον πίνακα app_logs
    INSERT INTO app_logs (action_type, table_name, description)
    VALUES (TG_OP, TG_TABLE_NAME, descText);

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

--2. Πίνακας Πελατών
CREATE TABLE clients (
    client_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20)
);

-- 3. Πίνακας Κρατήσεων
CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    client_id INTEGER REFERENCES clients(client_id),
    room_id INTEGER REFERENCES rooms(room_id),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'ACTIVE' -- π.χ. ACTIVE, CANCELLED, COMPLETED
);

-- 4. Πίνακας Προσωπικού
CREATE TABLE staff(
    staff_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(50), -- π.χ. 'Reception', 'Manager'
    hiring_date DATE,
    phone_number VARCHAR(20),
    work_days VARCHAR(100),
    salary DECIMAL(10,2)
);

-- 5. Πίνακας Τηλεφωνικού Καταλόγου
CREATE TABLE phone_catalogue (
    phone_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    address VARCHAR(200),
    phone_number VARCHAR(20)
);

-- 6. Πίνακας Προμηθευτών
CREATE TABLE suppliers (
    supplier_id SERIAL PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(200)
);

-- 7. Πίνακας Παραγγελιών (Orders)
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,           -- ΑΡΙΘΜΟΣ (για να ταιριάζει με το int id της Java)
    order_number VARCHAR(50),              -- ΚΕΙΜΕΝΟ (για το "ORD-001")
    supplier_id INTEGER REFERENCES suppliers(supplier_id),
    quantity INTEGER,
    order_type VARCHAR(50),
    placement_date DATE DEFAULT CURRENT_DATE
);

CREATE OR REPLACE FUNCTION generate_order_number()
    RETURNS TRIGGER AS $$
DECLARE
    next_id INTEGER;
BEGIN
    -- Βρίσκουμε ποιο θα είναι το επόμενο ID
    SELECT COALESCE(MAX(order_id), 0) + 1 INTO next_id FROM orders;

    -- Αν είναι INSERT, φτιάχνουμε το νούμερο
    IF TG_OP = 'INSERT' THEN
        -- LPAD(..., 3, '0') μετατρέπει το 1 σε '001', το 15 σε '015'
        NEW.order_number := 'ORD-' || LPAD(next_id::text, 6, '0');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4. Ενεργοποίηση του Trigger (Έναυσμα)
CREATE TRIGGER set_order_number_trigger
    BEFORE INSERT ON orders
    FOR EACH ROW
EXECUTE FUNCTION generate_order_number();


-- 8. Πίνακας To-Do List
CREATE TABLE tasks (
   task_id SERIAL PRIMARY KEY,
   description TEXT,
   due_date DATE,
   type VARCHAR(50), -- π.χ. Cleaning, Maintenance
   is_completed BOOLEAN DEFAULT FALSE
);

-- 9. Πίνακας Logs (Για την απαίτηση της εργασίας)
CREATE TABLE app_logs (
    log_id SERIAL PRIMARY KEY,
    action_type VARCHAR(50), -- π.χ. INSERT, UPDATE, DELETE
    table_name VARCHAR(50),
    description TEXT,
    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



---------------------------------------------

-- ΚΑΘΑΡΙΣΜΟΣ ΥΠΑΡΧΟΝΤΩΝ ΔΕΔΟΜΕΝΩΝ
TRUNCATE TABLE app_logs RESTART IDENTITY CASCADE;
TRUNCATE TABLE tasks RESTART IDENTITY CASCADE;
TRUNCATE TABLE orders RESTART IDENTITY CASCADE;
TRUNCATE TABLE reservations RESTART IDENTITY CASCADE;
TRUNCATE TABLE phone_catalogue RESTART IDENTITY CASCADE;
TRUNCATE TABLE staff RESTART IDENTITY CASCADE;
TRUNCATE TABLE suppliers RESTART IDENTITY CASCADE;
TRUNCATE TABLE rooms RESTART IDENTITY CASCADE;
TRUNCATE TABLE clients RESTART IDENTITY CASCADE;

-- 1. ΠΕΛΑΤΕΣ (CLIENTS) - 30 Εγγραφές
INSERT INTO clients (full_name, email, phone_number) VALUES
                                                         ('Ευαγγελία Ζαφειρίου', 'evaggelia.zaferiou@hotmail.com', '+30 6919191484'),
                                                         ('Γιάννης Σαμαράς', 'iannis.samaras@gmail.com', '+30 6963346214'),
                                                         ('Γιάννης Αλεξάνδρου', 'iannis.alexandrou@yahoo.gr', '+30 6952684029'),
                                                         ('Γεωργία Δημητριάδης', 'georgia.dimitriadis@yahoo.gr', '+30 6956255767'),
                                                         ('Παναγιώτης Δημητριάδης', 'panagiotis.dimitriadis@yahoo.gr', '+30 6939959894'),
                                                         ('Σοφία Παπαδόπουλος', 'sofia.papadopoulos@outlook.com', '+30 6999249752'),
                                                         ('Παναγιώτης Οικονόμου', 'panagiotis.oikonomou@outlook.com', '+30 6936850679'),
                                                         ('Σπύρος Γεωργίου', 'spyros.georgiou@yahoo.gr', '+30 6985716487'),
                                                         ('Θανάσης Γεωργίου', 'thanasis.georgiou@gmail.com', '+30 6934534811'),
                                                         ('Σοφία Ζαφειρίου', 'sofia.zaferiou@outlook.com', '+30 6911380036'),
                                                         ('Σοφία Αγγελόπουλος', 'sofia.angelopoulos@hotmail.com', '+30 6943418530'),
                                                         ('Μαρία Παπαδόπουλος', 'maria.papadopoulos@gmail.com', '+30 6928275830'),
                                                         ('Αναστασία Μακρής', 'anastasia.makris@hotmail.com', '+30 6988174206'),
                                                         ('Ευαγγελία Ζαφειρίου', 'evaggelia.zaferiou@yahoo.gr', '+30 6934809990'),
                                                         ('Μαρία Σαμαράς', 'maria.samaras@yahoo.gr', '+30 6932945220'),
                                                         ('Σοφία Βλάχος', 'sofia.vlachos@gmail.com', '+30 6995786070'),
                                                         ('Σπύρος Σαμαράς', 'spyros.samaras@gmail.com', '+30 6914461458'),
                                                         ('Βασίλης Ιωαννίδης', 'vasilis.ioannidis@outlook.com', '+30 6948668414'),
                                                         ('Βασίλης Οικονόμου', 'vasilis.oikonomou@hotmail.com', '+30 6964586929'),
                                                         ('Κώστας Μακρής', 'kostas.makris@hotmail.com', '+30 6998034541'),
                                                         ('Κατερίνα Δημητριάδης', 'katerina.dimitriadis@yahoo.gr', '+30 6982613528'),
                                                         ('Ελένη Μιχαηλίδης', 'eleni.michailidis@yahoo.gr', '+30 6978101034'),
                                                         ('Δημήτρης Δημητριάδης', 'dimitris.dimitriadis@hotmail.com', '+30 6979287969'),
                                                         ('Γιάννης Ζαφειρίου', 'iannis.zaferiou@gmail.com', '+30 6979780265'),
                                                         ('Παναγιώτης Παπαγεωργίου', 'panagiotis.papageorgiou@hotmail.com', '+30 6934383569'),
                                                         ('Δήμητρα Αγγελόπουλος', 'dimitra.angelopoulos@outlook.com', '+30 6918376250'),
                                                         ('Δημήτρης Παππάς', 'dimitris.pappas@gmail.com', '+30 6946781354'),
                                                         ('Γιώργος Παναγιωτόπουλος', 'giorgos.panagiotopoulos@yahoo.gr', '+30 6986658958'),
                                                         ('Σπύρος Παναγιωτόπουλος', 'spyros.panagiotopoulos@yahoo.gr', '+30 6984139682'),
                                                         ('Μιχάλης Σαμαράς', 'michalis.samaras@yahoo.gr', '+30 6973157099');

-- 2. ΔΩΜΑΤΙΑ (ROOMS) - 40 Εγγραφές (10 ανά όροφο)
INSERT INTO rooms (room_number, room_type, floor, occupancy, price_per_night) VALUES
                                                                                  ('101', 'Suite', 1, 4, 190.0),
                                                                                  ('102', 'Single', 1, 1, 80.0),
                                                                                  ('103', 'Suite', 1, 4, 190.0),
                                                                                  ('104', 'Double', 1, 2, 100.0),
                                                                                  ('105', 'Double', 1, 2, 100.0),
                                                                                  ('106', 'Double', 1, 2, 100.0),
                                                                                  ('107', 'Single', 1, 1, 80.0),
                                                                                  ('108', 'Double', 1, 2, 100.0),
                                                                                  ('109', 'Double', 1, 2, 100.0),
                                                                                  ('110', 'Deluxe Suite', 1, 4, 190.0),
                                                                                  ('201', 'Suite', 2, 4, 200.0),
                                                                                  ('202', 'Suite', 2, 4, 200.0),
                                                                                  ('203', 'Deluxe Suite', 2, 4, 200.0),
                                                                                  ('204', 'Deluxe Suite', 2, 4, 200.0),
                                                                                  ('205', 'Single', 2, 1, 90.0),
                                                                                  ('206', 'Suite', 2, 4, 200.0),
                                                                                  ('207', 'Deluxe Suite', 2, 4, 200.0),
                                                                                  ('208', 'Double', 2, 2, 110.0),
                                                                                  ('209', 'Deluxe Suite', 2, 4, 200.0),
                                                                                  ('210', 'Penthouse', 2, 2, 110.0),
                                                                                  ('301', 'Penthouse', 3, 2, 120.0),
                                                                                  ('302', 'Double', 3, 2, 120.0),
                                                                                  ('303', 'Double', 3, 2, 120.0),
                                                                                  ('304', 'Deluxe Suite', 3, 4, 210.0),
                                                                                  ('305', 'Penthouse', 3, 2, 120.0),
                                                                                  ('306', 'Single', 3, 1, 100.0),
                                                                                  ('307', 'Double', 3, 2, 120.0),
                                                                                  ('308', 'Double', 3, 2, 120.0),
                                                                                  ('309', 'Double', 3, 2, 120.0),
                                                                                  ('310', 'Suite', 3, 4, 210.0),
                                                                                  ('401', 'Single', 4, 1, 110.0),
                                                                                  ('402', 'Deluxe Suite', 4, 4, 220.0),
                                                                                  ('403', 'Double', 4, 2, 130.0),
                                                                                  ('404', 'Single', 4, 1, 110.0),
                                                                                  ('405', 'Penthouse', 4, 2, 130.0),
                                                                                  ('406', 'Penthouse', 4, 2, 130.0),
                                                                                  ('407', 'Deluxe Suite', 4, 4, 220.0),
                                                                                  ('408', 'Single', 4, 1, 110.0),
                                                                                  ('409', 'Penthouse', 4, 2, 130.0),
                                                                                  ('410', 'Suite', 4, 4, 220.0);

-- 3. ΠΡΟΣΩΠΙΚΟ (STAFF) - 25 Εγγραφές
INSERT INTO staff (full_name, role, phone_number, work_days, salary, hiring_date) VALUES
                                                                                      ('Αναστασία Στεφανίδης', 'Reception', '+30 6958393540', 'Mon, Sun, Sat, Thu, Tue', 1100, '2020-03-29'),
                                                                                      ('Γεωργία Κωνσταντινίδης', 'Concierge', '+30 6969304119', 'Tue, Fri, Sat, Mon, Sun, Wed', 1600, '2022-03-13'),
                                                                                      ('Θανάσης Γεωργίου', 'Manager', '+30 6958102869', 'Fri, Wed, Tue, Thu, Mon, Sat', 1900, '2023-05-07'),
                                                                                      ('Αναστασία Χατζής', 'Manager', '+30 6911021594', 'Thu, Tue, Sun, Fri, Mon, Wed', 2200, '2022-07-14'),
                                                                                      ('Νίκος Λυμπερόπουλος', 'Manager', '+30 6934901604', 'Tue, Thu, Sat, Fri, Wed', 1500, '2023-06-07'),
                                                                                      ('Ιωάννα Κωνσταντινίδης', 'Concierge', '+30 6990735222', 'Thu, Fri, Wed, Tue', 1300, '2021-09-06'),
                                                                                      ('Γεωργία Δημητριάδης', 'Maintenance', '+30 6922616911', 'Wed, Tue, Thu, Fri, Mon', 900, '2023-09-09'),
                                                                                      ('Κώστας Παπαγεωργίου', 'Cleaning', '+30 6937798207', 'Tue, Sat, Sun, Wed, Fri', 1800, '2020-08-21'),
                                                                                      ('Γιώργος Στεφανίδης', 'Cleaning', '+30 6931780721', 'Thu, Mon, Fri, Sat, Tue, Wed', 800, '2020-01-17'),
                                                                                      ('Μαρία Λυμπερόπουλος', 'Manager', '+30 6944648938', 'Sun, Tue, Fri, Thu', 2000, '2022-03-25'),
                                                                                      ('Σπύρος Βασιλείου', 'Manager', '+30 6983611418', 'Mon, Thu, Wed, Sun, Sat', 1600, '2021-03-27'),
                                                                                      ('Δημήτρης Οικονόμου', 'Reception', '+30 6934154195', 'Mon, Sun, Thu, Sat', 1800, '2024-09-29'),
                                                                                      ('Δήμητρα Ζαφειρίου', 'Chef', '+30 6934674940', 'Fri, Sun, Wed, Thu', 1200, '2022-04-26'),
                                                                                      ('Γιάννης Αντωνίου', 'Chef', '+30 6978711313', 'Sat, Fri, Mon, Tue, Sun, Wed', 1100, '2020-11-19'),
                                                                                      ('Αναστασία Παππάς', 'Chef', '+30 6962280462', 'Wed, Mon, Tue, Thu, Sun, Fri', 1000, '2022-10-21'),
                                                                                      ('Κατερίνα Μακρής', 'Reception', '+30 6945176271', 'Thu, Tue, Wed, Fri', 1800, '2023-03-25'),
                                                                                      ('Γιάννης Βλάχος', 'Chef', '+30 6911175947', 'Wed, Mon, Thu, Sat, Tue', 1800, '2022-10-18'),
                                                                                      ('Γιάννης Χατζής', 'Manager', '+30 6912105326', 'Wed, Tue, Mon, Sun, Sat', 2100, '2021-08-13'),
                                                                                      ('Χριστίνα Νικολάου', 'Chef', '+30 6972124883', 'Fri, Sat, Sun, Mon', 1400, '2020-02-23'),
                                                                                      ('Όλγα Μιχαηλίδης', 'Maintenance', '+30 6999119440', 'Wed, Thu, Tue, Fri', 1000, '2023-08-22'),
                                                                                      ('Μιχάλης Αγγελόπουλος', 'Security', '+30 6949851893', 'Mon, Sun, Wed, Sat', 1600, '2022-09-13'),
                                                                                      ('Γιάννης Αντωνίου', 'Concierge', '+30 6943587332', 'Mon, Tue, Fri, Sat', 1400, '2021-07-08'),
                                                                                      ('Σπύρος Μιχαηλίδης', 'Barista', '+30 6937623679', 'Wed, Thu, Sun, Fri', 1600, '2023-01-20'),
                                                                                      ('Κώστας Κωνσταντινίδης', 'Manager', '+30 6981902083', 'Sat, Mon, Tue, Thu, Fri, Wed', 1500, '2023-11-17'),
                                                                                      ('Γεωργία Ζαφειρίου', 'Concierge', '+30 6931974654', 'Thu, Fri, Sat, Wed, Mon', 1700, '2024-02-09');

-- 4. ΠΡΟΜΗΘΕΥΤΕΣ (SUPPLIERS) - 25 Εγγραφές
INSERT INTO suppliers (supplier_name, phone_number, address) VALUES
                                                                 ('Delta Συντήρηση Ε.Π.Ε.', '2310 110923', 'Παλαιών Πατρών Γερμανού 153, Θεσσαλονίκη'),
                                                                 ('Star Έπιπλα Α.Ε.', '2310 518683', 'Αγίου Δημητρίου 63, Θεσσαλονίκη'),
                                                                 ('Alpha Τρόφιμα Ε.Π.Ε.', '2310 822995', 'Βασιλίσσης Όλγας 170, Θεσσαλονίκη'),
                                                                 ('Gamma Λευκά Είδη Ο.Ε.', '2310 533320', 'Παλαιών Πατρών Γερμανού 1, Θεσσαλονίκη'),
                                                                 ('Epsilon Συντήρηση Α.Ε.', '2310 510951', 'Παύλου Μελά 149, Θεσσαλονίκη'),
                                                                 ('Beta Συντήρηση Ε.Π.Ε.', '2310 980064', 'Αριστοτέλους 73, Θεσσαλονίκη'),
                                                                 ('Alpha Λευκά Είδη Ο.Ε.', '2310 753875', 'Παπαναστασίου 124, Θεσσαλονίκη'),
                                                                 ('Zeta Έπιπλα Ο.Ε.', '2310 171999', 'Παύλου Μελά 18, Θεσσαλονίκη'),
                                                                 ('Beta Αναλώσιμα Ο.Ε.', '2310 228763', 'Αγίου Δημητρίου 93, Θεσσαλονίκη'),
                                                                 ('Star Ποτά Α.Ε.', '2310 721465', 'Βασιλίσσης Όλγας 53, Θεσσαλονίκη'),
                                                                 ('Beta Ηλεκτρικά Ο.Ε.', '2310 965556', 'Μητροπόλεως 152, Θεσσαλονίκη'),
                                                                 ('Alpha Αναλώσιμα Ε.Π.Ε.', '2310 655671', 'Αριστοτέλους 18, Θεσσαλονίκη'),
                                                                 ('Mega Λευκά Είδη Ο.Ε.', '2310 273894', 'Καρόλου Ντηλ 14, Θεσσαλονίκη'),
                                                                 ('Gamma Ποτά Α.Ε.', '2310 298197', 'Λεωφόρος Νίκης 107, Θεσσαλονίκη'),
                                                                 ('Beta Αναλώσιμα Ε.Π.Ε.', '2310 467741', 'Μπότσαρη 65, Θεσσαλονίκη'),
                                                                 ('Epsilon Ηλεκτρικά Ε.Π.Ε.', '2310 232421', 'Παλαιών Πατρών Γερμανού 9, Θεσσαλονίκη'),
                                                                 ('Alpha Ηλεκτρικά Α.Ε.', '2310 967534', 'Βενιζέλου 153, Θεσσαλονίκη'),
                                                                 ('Beta Λευκά Είδη Α.Ε.', '2310 541724', 'Βασιλίσσης Όλγας 10, Θεσσαλονίκη'),
                                                                 ('Epsilon Αναλώσιμα Ο.Ε.', '2310 638544', 'Μπότσαρη 133, Θεσσαλονίκη'),
                                                                 ('Alpha Ποτά Ε.Π.Ε.', '2310 692934', 'Παύλου Μελά 52, Θεσσαλονίκη'),
                                                                 ('Beta Συντήρηση Α.Ε.', '2310 795910', 'Μπότσαρη 135, Θεσσαλονίκη'),
                                                                 ('Delta Αναλώσιμα Ο.Ε.', '2310 687755', 'Ικτίνου 167, Θεσσαλονίκη'),
                                                                 ('Epsilon Αναλώσιμα Α.Ε.', '2310 415864', 'Προξένου Κορομηλά 134, Θεσσαλονίκη'),
                                                                 ('Gamma Ποτά Ο.Ε.', '2310 770616', 'Παλαιών Πατρών Γερμανού 16, Θεσσαλονίκη'),
                                                                 ('Mega Καθαριστικά Ο.Ε.', '2310 595210', 'Ερμού 39, Θεσσαλονίκη');

-- 5. ΤΗΛΕΦΩΝΙΚΟΣ ΚΑΤΑΛΟΓΟΣ - 25 Εγγραφές
INSERT INTO phone_catalogue (name, phone_number, address) VALUES
                                                              ('Cinema Χατζής', '2310 935043', 'Προξένου Κορομηλά 132'),
                                                              ('Αστυνομία Σαμαράς', '2310 832389', 'Παλαιών Πατρών Γερμανού 78'),
                                                              ('Museum Ζαφειρίου', '2310 200210', 'Λαγκαδά 18'),
                                                              ('Τουριστικό Γραφείο Αλεξάνδρου', '2310 526680', 'Μοναστηρίου 190'),
                                                              ('Theater Παναγιωτόπουλος', '2310 993742', 'Αγίου Δημητρίου 5'),
                                                              ('Ταξί Δημητριάδης', '2310 671978', 'Αγίου Δημητρίου 197'),
                                                              ('Museum Γεωργίου', '2310 903789', 'Ικτίνου 63'),
                                                              ('Cinema Μιχαηλίδης', '2310 188118', 'Βασιλίσσης Όλγας 81'),
                                                              ('Εστιατόριο Αγγελόπουλος', '2310 693801', 'Αγίου Δημητρίου 24'),
                                                              ('Ταξί Χατζής', '2310 295019', 'Παπαναστασίου 200'),
                                                              ('Τουριστικό Γραφείο Παπαδόπουλος', '2310 984250', 'Βενιζέλου 163'),
                                                              ('Τουριστικό Γραφείο Μιχαηλίδης', '2310 107565', 'Λεωφόρος Νίκης 118'),
                                                              ('Γιατρός Παπαδόπουλος', '2310 695455', 'Αριστοτέλους 138'),
                                                              ('Ξεναγός Αγγελόπουλος', '2310 938144', 'Παλαιών Πατρών Γερμανού 160'),
                                                              ('Πυροσβεστική Στεφανίδης', '2310 464462', 'Μπότσαρη 72'),
                                                              ('Ταξί Αντωνίου', '2310 592962', 'Καρόλου Ντηλ 18'),
                                                              ('Cinema Χατζής', '2310 147990', 'Δελφών 123'),
                                                              ('Εστιατόριο Παπαγεωργίου', '2310 230631', 'Ικτίνου 131'),
                                                              ('Museum Τσιτσάνης', '2310 713690', 'Μοναστηρίου 161'),
                                                              ('Ενοικιάσεις Κωνσταντινίδης', '2310 221984', 'Καρόλου Ντηλ 88'),
                                                              ('Πυροσβεστική Παππάς', '2310 581131', 'Μοναστηρίου 55'),
                                                              ('Εστιατόριο Νικολάου', '2310 510173', 'Παύλου Μελά 75'),
                                                              ('Εστιατόριο Νικολάου', '2310 970388', 'Λεωφόρος Νίκης 31'),
                                                              ('Αστυνομία Γεωργίου', '2310 223360', 'Παλαιών Πατρών Γερμανού 70'),
                                                              ('Αστυνομία Αλεξάνδρου', '2310 933633', 'Ικτίνου 89');

-- 6. ΚΡΑΤΗΣΕΙΣ (RESERVATIONS) - 40 Εγγραφές
INSERT INTO reservations (client_id, room_id, check_in_date, check_out_date, total_price, status) VALUES
                                                                                                      (12, 22, '2025-06-01', '2025-06-02', 262.0, 'Confirmed'),
                                                                                                      (6, 23, '2025-03-10', '2025-03-11', 129.0, 'Confirmed'),
                                                                                                      (10, 29, '2025-05-23', '2025-05-29', 1182.0, 'Pending'),
                                                                                                      (17, 15, '2025-04-12', '2025-04-14', 216.0, 'Active'),
                                                                                                      (21, 1, '2025-06-23', '2025-06-28', 1415.0, 'Confirmed'),
                                                                                                      (11, 15, '2025-05-30', '2025-06-01', 570.0, 'Cancelled'),
                                                                                                      (16, 38, '2025-12-03', '2025-12-09', 360.0, 'Confirmed'),
                                                                                                      (10, 19, '2025-02-20', '2025-02-22', 310.0, 'Confirmed'),
                                                                                                      (12, 30, '2025-10-16', '2025-10-17', 223.0, 'Pending'),
                                                                                                      (9, 36, '2025-10-28', '2025-11-05', 2392.0, 'Completed'),
                                                                                                      (6, 12, '2025-07-04', '2025-07-11', 1911.0, 'Completed'),
                                                                                                      (13, 22, '2025-06-30', '2025-07-08', 464.0, 'Completed'),
                                                                                                      (4, 12, '2025-08-09', '2025-08-17', 1864.0, 'Active'),
                                                                                                      (9, 6, '2025-05-17', '2025-05-24', 399.0, 'Active'),
                                                                                                      (20, 13, '2025-10-19', '2025-10-21', 200.0, 'Cancelled'),
                                                                                                      (18, 28, '2025-08-26', '2025-09-03', 2288.0, 'Cancelled'),
                                                                                                      (1, 15, '2025-07-03', '2025-07-05', 564.0, 'Completed'),
                                                                                                      (20, 40, '2025-05-11', '2025-05-17', 438.0, 'Pending'),
                                                                                                      (13, 35, '2025-02-27', '2025-02-28', 260.0, 'Active'),
                                                                                                      (7, 37, '2025-02-28', '2025-03-05', 270.0, 'Cancelled'),
                                                                                                      (19, 30, '2025-04-20', '2025-04-26', 606.0, 'Active'),
                                                                                                      (21, 13, '2025-06-01', '2025-06-03', 598.0, 'Active'),
                                                                                                      (16, 24, '2025-05-03', '2025-05-08', 745.0, 'Confirmed'),
                                                                                                      (17, 8, '2025-07-23', '2025-07-27', 412.0, 'Pending'),
                                                                                                      (16, 21, '2025-11-21', '2025-12-01', 560.0, 'Confirmed'),
                                                                                                      (19, 14, '2025-06-07', '2025-06-16', 1341.0, 'Active'),
                                                                                                      (14, 38, '2025-02-06', '2025-02-15', 1152.0, 'Completed'),
                                                                                                      (11, 11, '2025-04-23', '2025-04-28', 270.0, 'Pending'),
                                                                                                      (7, 32, '2025-06-30', '2025-07-02', 226.0, 'Completed'),
                                                                                                      (23, 29, '2025-01-08', '2025-01-18', 2420.0, 'Completed'),
                                                                                                      (23, 3, '2025-08-16', '2025-08-17', 230.0, 'Cancelled'),
                                                                                                      (17, 8, '2025-01-25', '2025-01-27', 358.0, 'Pending'),
                                                                                                      (20, 27, '2025-09-08', '2025-09-10', 234.0, 'Confirmed'),
                                                                                                      (4, 37, '2025-09-26', '2025-09-30', 1036.0, 'Completed'),
                                                                                                      (23, 23, '2025-10-30', '2025-11-09', 2760.0, 'Confirmed'),
                                                                                                      (21, 13, '2025-03-31', '2025-04-05', 735.0, 'Active'),
                                                                                                      (3, 19, '2025-09-04', '2025-09-13', 1665.0, 'Confirmed'),
                                                                                                      (25, 6, '2025-05-21', '2025-05-31', 1590.0, 'Cancelled'),
                                                                                                      (16, 23, '2025-06-23', '2025-07-01', 768.0, 'Cancelled'),
                                                                                                      (19, 25, '2025-05-04', '2025-05-13', 2610.0, 'Pending');

-- 7. ΠΑΡΑΓΓΕΛΙΕΣ (ORDERS) - 30 Εγγραφές
-- Ο αριθμός παραγγελίας (order_number) δημιουργείται αυτόματα από τον Trigger
INSERT INTO orders (supplier_id, quantity, order_type, placement_date) VALUES
                                                                           (17, 187, 'Ηλεκτρικά', '2025-12-31'),
                                                                           (23, 401, 'Λευκά Είδη', '2025-09-10'),
                                                                           (6, 289, 'Λευκά Είδη', '2025-07-22'),
                                                                           (23, 396, 'Ποτά', '2025-04-27'),
                                                                           (18, 212, 'Καθαριστικά', '2025-09-20'),
                                                                           (21, 412, 'Αναλώσιμα', '2025-10-22'),
                                                                           (19, 62, 'Λευκά Είδη', '2025-06-10'),
                                                                           (25, 8, 'Τρόφιμα', '2025-07-24'),
                                                                           (14, 413, 'Λευκά Είδη', '2025-10-28'),
                                                                           (22, 228, 'Ποτά', '2025-05-15'),
                                                                           (7, 24, 'Έπιπλα', '2025-05-12'),
                                                                           (2, 474, 'Ποτά', '2025-11-09'),
                                                                           (9, 303, 'Λευκά Είδη', '2025-01-27'),
                                                                           (3, 316, 'Έπιπλα', '2025-03-12'),
                                                                           (7, 64, 'Αναλώσιμα', '2025-07-23'),
                                                                           (23, 163, 'Συντήρηση', '2025-04-15'),
                                                                           (3, 271, 'Ηλεκτρικά', '2025-05-24'),
                                                                           (8, 369, 'Καθαριστικά', '2025-10-08'),
                                                                           (24, 135, 'Ποτά', '2025-10-07'),
                                                                           (25, 398, 'Τρόφιμα', '2025-06-05'),
                                                                           (21, 185, 'Αναλώσιμα', '2025-06-12'),
                                                                           (2, 13, 'Αναλώσιμα', '2025-03-01'),
                                                                           (24, 74, 'Συντήρηση', '2025-03-29'),
                                                                           (6, 83, 'Έπιπλα', '2025-06-22'),
                                                                           (15, 6, 'Συντήρηση', '2025-08-07'),
                                                                           (18, 292, 'Λευκά Είδη', '2025-08-27'),
                                                                           (4, 22, 'Λευκά Είδη', '2025-02-28'),
                                                                           (16, 433, 'Ποτά', '2025-02-01'),
                                                                           (9, 388, 'Τρόφιμα', '2025-03-29'),
                                                                           (7, 134, 'Συντήρηση', '2025-11-09');

-- 8. TASKS (ΕΡΓΑΣΙΕΣ) - 30 Εγγραφές
INSERT INTO tasks (description, due_date, type, is_completed) VALUES
                                                                  ('Maintenance task: Clean Pool 277', '2025-03-30', 'Maintenance', true),
                                                                  ('Other task: Check Room 107', '2025-09-29', 'Security', true),
                                                                  ('Maintenance task: Check Lobby 402', '2025-05-18', 'Maintenance', true),
                                                                  ('Maintenance task: Review Room 237', '2025-03-12', 'Maintenance', false),
                                                                  ('Cleaning task: Prepare Office 303', '2025-05-29', 'Cleaning', false),
                                                                  ('Guest Service task: Check Pool 327', '2025-09-20', 'Guest Service', true),
                                                                  ('Inventory task: Prepare Kitchen 290', '2025-01-20', 'Inventory', true),
                                                                  ('Security task: Prepare Office 197', '2025-08-15', 'Security', true),
                                                                  ('Administrative task: Review Kitchen 262', '2025-03-10', 'Administrative', true),
                                                                  ('Maintenance task: Prepare Room 189', '2025-08-22', 'Maintenance', true),
                                                                  ('Maintenance task: Clean Lobby 159', '2025-02-28', 'Maintenance', false),
                                                                  ('Security task: Review Pool 132', '2025-02-17', 'Security', false),
                                                                  ('Inventory task: Prepare Kitchen 307', '2025-01-25', 'Inventory', false),
                                                                  ('Security task: Prepare Pool 233', '2025-08-23', 'Security', false),
                                                                  ('Security task: Prepare Room 377', '2025-01-22', 'Security', true),
                                                                  ('Inventory task: Check Kitchen 329', '2025-07-27', 'Inventory', false),
                                                                  ('Administrative task: Clean Office 177', '2025-05-18', 'Administrative', true),
                                                                  ('Inventory task: Check Pool 107', '2025-09-22', 'Inventory', false),
                                                                  ('Maintenance task: Prepare Pool 220', '2025-08-25', 'Maintenance', true),
                                                                  ('Inventory task: Prepare Room 402', '2025-03-10', 'Inventory', true),
                                                                  ('Inventory task: Review Lobby 243', '2025-05-22', 'Inventory', true),
                                                                  ('Inventory task: Fix Office 134', '2025-08-27', 'Inventory', false),
                                                                  ('Administrative task: Check Pool 208', '2025-07-25', 'Administrative', true),
                                                                  ('Inventory task: Fix Kitchen 183', '2025-05-18', 'Inventory', false),
                                                                  ('Guest Service task: Fix Pool 203', '2025-02-17', 'Guest Service', true),
                                                                  ('Guest Service task: Clean Lobby 182', '2025-01-20', 'Guest Service', true),
                                                                  ('Administrative task: Clean Pool 179', '2025-01-13', 'Administrative', false),
                                                                  ('Maintenance task: Review Office 389', '2025-09-17', 'Maintenance', true),
                                                                  ('Security task: Fix Lobby 273', '2025-05-25', 'Security', false),
                                                                  ('Security task: Prepare Room 137', '2025-07-15', 'Security', true);


-- Ενα παραδειγμα για το πελατη με client_id = 1
ALTER TABLE clients ADD COLUMN preferences JSON;

UPDATE clients
SET preferences = '{"allergy": "peanuts", "pillow": "soft", "floor_preference": "high"}'
WHERE client_id = 1;