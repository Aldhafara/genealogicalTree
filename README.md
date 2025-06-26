
# genealogicalTree

Serwis do zarządzania drzewem genealogicznym, z obsługą użytkowników, zabezpieczeniami i nowoczesnym UI.

## Spis treści

- [Opis](#opis)
- [Technologie](#technologie)
- [Konfiguracja bazy danych](#konfiguracja-bazy-danych)
- [Uruchomienie lokalne](#uruchomienie-lokalne)
- [Dostępne endpointy](#dostępne-endpointy)
- [Swagger / Dokumentacja API](#swagger--dokumentacja-api)
- [Testowanie](#testowanie)

---

## Opis

Aplikacja umożliwia:
- Zarządzanie rodziną i powiązaniami pomiędzy członkami
- Dodawanie i edycję profili osób (imię, nazwisko, daty, relacje)
- Przeglądanie drzewa genealogicznego w przeglądarce (Thymeleaf UI)
- Bezpieczną autoryzację użytkowników (Spring Security)
- Dokumentację API poprzez Swagger UI

Dane każdego użytkownika są odseparowane – użytkownik widzi tylko swoje zasoby.

---

## Technologie

- Java 17
- Spring Boot 3.4
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL (produkcyjnie)
- H2 (do testów)
- Thymeleaf (UI)
- Bucket4J (rate limiting)
- Lombok (mniej boilerplate’u)
- Swagger / OpenAPI (`springdoc-openapi`)
- JUnit, Spock, Groovy (testy)

---

## Konfiguracja bazy danych

Projekt korzysta z PostgreSQL.

### Krok po kroku:

1. W katalogu *'src/main/resources/secrets/'* musisz utworzyć dwa pliki.
   *'dbUsername'* z twoim loginem do PostgreSQL oraz
   *'dbPassword'* z twoim hasłem do  PostgreSQL.
2. Nie musisz tworzyć bazy danych samodzielnie.
   Zostanie ona otworzona automatycznie z nazwą z pliku *'dbName'*.
3. W pliku *'dbUrl'* znajduje się domyślny adres URL serwera bazy danych PostgreSQL z domyślnym portem 5432. Jeżeli Twój lokalny PostgreSQL server nie jest na domyślnym porcie, musisz wyedytować plik *'dbUrl'*.

---

## Uruchomienie lokalne
1. Upewnij się, że masz zainstalowaną JDK 17 i działającą bazę PostgreSQL
2. Skonfiguruj pliki w katalogu *'src/main/resources/secrets/'* (jak wyżej)
3. Uruchom aplikację:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Aplikacja domyślnie będzie dostępna pod adresem:
   ```
   http://localhost:8080
   ```

---

## Dostępne endpointy


| Metoda | Endpoint               | Opis                                                                    | Autoryzacja |
| ------ | ---------------------- |-------------------------------------------------------------------------|-------------|
| POST   | /register              | Rejestracja nowego użytkownika                                          | ❌          |
| POST   | /login                 | Logowanie użytkownika                                                   | ❌          |
| POST   | /file/upload           | Pozwala zaimportować dane z pliku .ged                                  | ✅          |
| GET    | /person/aboutme        | Przekierowuje na stronę ze szczegółami obecnie zalogowanie użytkownika  | ✅          |
| GET    | /family-tree/get-my-id | Pobierz UUID obecnie zalogowanie użytkownika                            | ✅          |
| GET    | /family-tree/get-structure/{id} | Zwraca strukturę drzewa dla użytkownika o zadanym UUID         | ✅          |

_Pełna lista endpointów i parametrów – zobacz  [Swagger](#swagger--dokumentacja-api)_

---

## Swagger / Dokumentacja API
Po uruchomieniu aplikacji dokumentacja dostępna jest pod:
   ```
   http://localhost:8080/swagger-ui.html
   ```
Możesz tam przetestować wszystkie endpointy.

---

## Testowanie
Aby uruchomić testy:
   ```bash
   ./mvnw test
   ```
Testy wykorzystują bazę H2 (in-memory). Dane testowe nie mają wpływu na środowisko produkcyjne.