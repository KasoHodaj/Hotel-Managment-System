# 🏨 HotelManager System

Μια ολοκληρωμένη Desktop εφαρμογή διαχείρισης ξενοδοχειακής μονάδας, ανεπτυγμένη με **JavaFX** και **PostgreSQL**.

Το σύστημα αντικαθιστά τις παραδοσιακές μεθόδους καταγραφής με μια σύγχρονη, κανονικοποιημένη βάση δεδομένων, προσφέροντας αυτοματισμούς, ασφάλεια συναλλαγών και ευχρηστία.

![Dashboard Screenshot](<img width="1297" height="798" alt="image" src="https://github.com/user-attachments/assets/5bf8515d-cc48-43da-9994-290143733423" />)


---

## 🚀 Βασικές Λειτουργίες

### 📊 Dashboard & Analytics
* **Real-time Στατιστικά:** Προβολή πληρότητας, ενεργών κρατήσεων και συνολικών πελατών.
* **Activity Log:** Ζωντανή καταγραφή ενεργειών (Audit Trail) μέσω Database Triggers.
* **Quick Actions:** Άμεση πρόσβαση στις συχνότερες λειτουργίες.

### 🏨 Διαχείριση Κρατήσεων (Smart Booking)
* **Δυναμική Διασύνδεση:** Επιλογή Πελάτη και Δωματίου από δυναμικές λίστες.
* **Αυτόματος Υπολογισμός:** Αυτόματη κοστολόγηση βάσει ημερών και τιμής δωματίου.
* **Status Lifecycle:** Υποστήριξη καταστάσεων (Active, Confirmed, Completed, Cancelled).

### 👥 Πελατολόγιο & JSON
* Πλήρης διαχείριση στοιχείων πελατών.
* **Ημιδομημένα Δεδομένα:** Υποστήριξη ειδικών προτιμήσεων (Preferences) σε μορφή **JSON** (PostgreSQL feature).

### 🛠️ Εργασίες & Προσωπικό
* **Task Management:** Λίστα To-Do με οπτική κωδικοποίηση για ολοκληρωμένες εργασίες.
* **Διαχείριση Προσωπικού:** Ανάθεση ρόλων και βαρδιών με color-coded roles.

---

## ⚙️ Τεχνολογίες & Αρχιτεκτονική

* **Γλώσσα:** Java (JDK 17+)
* **GUI Framework:** JavaFX (MVC Pattern)
* **Database:** PostgreSQL
* **Object–Relational mapping:** JDBC με DAO Pattern
* **Build Tool:** Maven

### 🧠 Database Logic (Advanced Features)
Η εφαρμογή αξιοποιεί πλήρως τις δυνατότητες του Database Server:
1.  **Triggers:**
    * `log_activity`: Αυτόματη καταγραφή ιστορικού στο `app_logs` μετά από κάθε Insert/Update/Delete.
    * `set_order_number`: Αυτόματη παραγωγή μορφοποιημένου κωδικού παραγγελίας (π.χ. "ORD-000005").
2.  **Stored Procedures:**
    * `delete_client_procedure`: Ασφαλής διαγραφή πελάτη και των εξαρτώμενων κρατήσεών του (Transaction Safe).
3.  **JSON Types:** Αποθήκευση metadata πελατών.

---

## 🛠️ Εγκατάσταση & Εκτέλεση

### 1. Κλωνοποίηση (Clone)
```bash
git clone [https://github.com/ToOnomaSou/HotelManager-System.git](https://github.com/ToOnomaSou/HotelManager-System.git)
cd HotelManager-System
