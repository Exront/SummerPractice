import java.sql.*;

public class JDBCRunner {

    private static final String PROTOCOL = "jdbc:postgresql://";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL_LOCALE_NAME = "localhost/";

    private static final String DATABASE_NAME = "estateAgency";

    public static final String DATABASE_URL = PROTOCOL + URL_LOCALE_NAME + DATABASE_NAME;
    public static final String USER_NAME = "postgres";
    public static final String DATABASE_PASS = "postgres";

    public static void main(String[] args) {

        checkDriver();
        checkDB();
        System.out.println("Подключение к базе данных | " + DATABASE_URL + "\n");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS)) {
            executeQueries(connection);


        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")){
                System.out.println("Произошло дублирование данных");
            } else throw new RuntimeException(e);
        }
    }

    public static void checkDriver () {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Нет JDBC-драйвера! Подключите JDBC-драйвер к проекту согласно инструкции.");
            throw new RuntimeException(e);
        }
    }

    public static void checkDB () {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, USER_NAME, DATABASE_PASS);
        } catch (SQLException e) {
            System.out.println("Нет базы данных! Проверьте имя базы, путь к базе или разверните локально резервную копию согласно инструкции");
            throw new RuntimeException(e);
        }
    }

    public static void executeQueries(Connection connection) throws SQLException{

        System.out.println("Арендаторы, старше 25 лет");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM clients WHERE age > 25")) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " +
                        rs.getString("name") +
                        ", Surname: " + rs.getString("Surname") + ", Age: " +
                        rs.getInt("age") +
                        ", Phone Number: " + rs.getString("phone_number") + ", Apartment ID: " +
                        rs.getInt("apartment_id"));
            }
        }

        System.out.println("\nКвартиры, цена аренды которых выше 60 тыс.");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM apartments WHERE price > 60000")) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Floor: " +
                        rs.getInt("floor") +
                        ", Rooms: " + rs.getInt("rooms") + ", Price: " +
                        rs.getInt("price") +
                        ", City ID: " + rs.getString("city_id"));
            }
        }

        System.out.println("\nКвартиры, сопоставленные с городами");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT apartments.id, cities.name FROM apartments " +
                "JOIN cities ON apartments.city_id = cities.id")){
                while (rs.next()) {
                    System.out.println("Apartment ID: " + rs.getInt("id") + ", City: " + rs.getString("name"));
                }
        }

        System.out.println("\nКлиенты, арендованные ими квартиры и города, в которых находятся эти квартиры");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT clients.name || ' ' || clients.surname AS \"Client Full Name\"," +
                    "apartments.id AS \"Apartment ID\", cities.name AS \"City\" FROM clients\n" +
                    "JOIN apartments ON clients.apartment_id = apartments.id\n" +
                    "JOIN cities ON apartments.city_id = cities.id ORDER BY \"Apartment ID\"")){
            while (rs.next()) {
                System.out.println("Client Full Name: " + rs.getString("Client Full Name") + ", Apartment ID: "
                + rs.getInt("Apartment ID") + ", City: " + rs.getString("City"));
            }
        }

        System.out.println("\nПодсчёт квартир в каждом городе");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT apartments.city_id, cities.name AS \"City\", " +
                    "COUNT(apartments.id) AS apartment_count\n" +
                    "FROM apartments  JOIN cities ON apartments.city_id = cities.id \n" +
                    "GROUP BY apartments.city_id, cities.name ORDER BY apartments.city_id")){
            while (rs.next()) {
                System.out.println("City ID: " + rs.getInt("city_id") + ", City: "
                        + rs.getString("City") + ", Apartment Count: " + rs.getString("apartment_count"));
            }
        }

        System.out.println("\nДобавление арендатора в таблицу");
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO clients (name, surname, age, phone_number, apartment_id) "
                + "VALUES (?, ?, ?, ?, ?)")){
            statement.setString(1, "Тимофей");
            statement.setString(2, "Воронин");
            statement.setInt(3, 23);
            statement.setString(4, "+7 (960) 415-40-32");
            statement.setInt(5, 11);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Арендатор добавлен");
            }
        }

        System.out.println("\nОбновление данных арендатора");
        try(PreparedStatement statement = connection.prepareStatement("UPDATE clients SET age = 32 WHERE name = 'Тимофей'")){
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Данные арендатора обновлены");
            }
        }

        System.out.println("\nУдаление арендатора");
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM clients WHERE name = 'Тимофей' " +
                "AND surname = 'Воронин' AND apartment_id = 11")) {
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Арендатор удален");
            }
        }

        System.out.println("\nПодсчет общих цен аренд квартир в каждом городе");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT city_id, SUM(price) AS apartments_prices " +
                    "FROM apartments GROUP BY city_id ORDER BY city_id")){
            while (rs.next()) {
                System.out.println("City ID: " + rs.getInt("city_id") + ", Total price: "
                        + rs.getString("apartments_prices"));
            }
        }

        System.out.println("\nПодсчет средних цен аренд квартир в каждом городе");
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT city_id, AVG(price) AS apartments_prices " +
                    "FROM apartments GROUP BY city_id ORDER BY city_id")){
            while (rs.next()) {
                System.out.println("City ID: " + rs.getInt("city_id") + ", Average price: "
                        + rs.getString("apartments_prices"));
            }
        }
    }
}
