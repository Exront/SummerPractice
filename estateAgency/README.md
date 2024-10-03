# Набор персонала
![er-model](src/main/resources/er-model.hh.png)

Проект создаем со сборщиком Maven (в связи с тем, что в Университете не доступен сборщик gradle)

## Чтобы развернуть резервную копию базы данных:
1) Резервная копия базы данных приложена в resources/head_hunter.sql
2) Подключитесь к локальному серверу PostgreSQL через PgAdmin (или терминал)
3) Создайте пустую базу данных rut_head_hunter
4) Восстановите в ней приложенную резервную копию базы данных

## Чтобы подключить JDBC-драйвер к проекту:
1) Через подключение библиотеки к проекту:
* File -> Project Structure -> Libraries
* На плюсе (+) выберите From Maven...
* В поиске найдите (search) драйвер, соответствующий вашей базе данных: org.postgresql:postgresql:42.6.0 [или старшая версия]
* И подтвердите подключение - ОК -> Apply

2) Или через файл-сборки:
* добавьте в файле-сборки pom.xml в список зависимостей:
```
<dependencies>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>
</dependencies>
```
* И подтвердить загрузку библиотеки

## Чтобы осуществить запрос к базе данных необходимо:
1) Открыть соединение с базой данных - [java.sql.Connection](https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html)
2) Определить оператор для SQL-запроса - [java.sql.Statement](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html) или его дочерние классы [java.sql.PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html). При необходимости, вложить данные в SQL-запрос
3) Выполнить SQL-запрос и в зависимости от типа проанализировать результат:
    + Простой запрос - executeQuery - результат запроса [java.sql.ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)
    + Запрос на изменение - executeUpdate - результат запроса представляет собой количество измененных строк (также можно затребовать возвращаемые данные и считать из через [java.sql.ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html))

## Полезные ресурсы
* [The basics of the JDBC API](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)