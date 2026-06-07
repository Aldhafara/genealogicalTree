
# genealogicalTree

Serwis do zarządzania drzewem genealogicznym, z obsługą użytkowników, zabezpieczeniami i nowoczesnym UI.

## Spis treści

- [Opis](#opis)
- [Walidacja formularzy](#walidacja-formularzy)
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

## Walidacja formularzy

Aplikacja wykorzystuje walidację po stronie backendu dla formularzy i danych wejściowych.  
Obecnie obejmuje ona formularz rejestracji, ale mechanizm jest przygotowany tak, aby był rozszerzany na kolejne formularze.

### Zasady
- Walidacja działa po stronie serwera.
- Do walidacji wykorzystywany jest Jakarta Bean Validation.
- Część reguł jest realizowana przez własne adnotacje, np. `@HumanName`.
- Komunikaty błędów są pobierane z plików `messages*.properties`.

### Rejestracja użytkownika
Formularz rejestracji oraz odpowiadające mu endpointy API są walidowane po stronie serwera z użyciem Jakarta Bean Validation.

#### Dane użytkownika
- `login`:
   - wymagany,
   - długość od 3 do 255 znaków,
   - dozwolone są litery, cyfry oraz kropki pomiędzy segmentami,
   - przykłady poprawne: `jan`, `jan.kowalski`, `user123`.

- `password`:
   - wymagane,
   - długość od 8 do 255 znaków,
   - dozwolone są litery, cyfry oraz wybrane znaki specjalne.

#### Dane osoby
- `firstName` i `lastName`:
   - walidowane własną adnotacją `@HumanName`,
   - długość od 2 do 255 znaków,
   - dopuszczają wyłącznie format zgodny z regułami dla imion i nazwisk zdefiniowanymi w aplikacji.

### Komunikaty walidacyjne
Komunikaty błędów są utrzymywane w plikach `messages*.properties` i ładowane przez Spring Boot `MessageSource`.  
Domyślny bundle powinien zawierać plik `src/main/resources/messages.properties`, opcjonalnie rozszerzony o wersje językowe, np.:
- `messages.properties`
- `messages_pl.properties`
- `messages_en.properties`

### Uwagi
Walidacja jest wykonywana po stronie backendu, dlatego obowiązuje zarówno dla formularza Thymeleaf, jak i dla wywołań API.

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