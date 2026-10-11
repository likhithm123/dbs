# Campus Event & Resource Management System

A plain Java web application for a Database Systems Lab project. Uses Java Servlets/JSP and JDBC (not legacy ODBC) with Oracle or MySQL. Features cover events, venues, sessions, resources, allocations, registrations, payments, cancellation status, and reports.

## Requirements
- JDK 17+
- Maven
- Apache Tomcat 10.1+
- Oracle Database (recommended for PL/SQL lab) or MySQL

## Configure
Set environment variables `DB_TYPE` (`oracle` or `mysql`), `DB_URL`, `DB_USER`, and `DB_PASSWORD`. Add the matching JDBC driver to the Maven dependencies if your environment does not provide it.

## Build
`mvn clean package`

Deploy `target/campus-events.war` to Tomcat. Open `http://localhost:8080/campus-events/`.

Run `sql/oracle_schema.sql` or `sql/mysql_schema.sql` before starting. Do not commit database passwords.

## Notes
JDBC is the appropriate Java database API. ODBC is a separate native interface and is not recommended for a new Java project. Conflict prevention and resource allocation should be tested against the target database with concurrent requests before production use.