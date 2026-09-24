# Plan napraw - ShoppingList (Android)

**Data utworzenia:** 2026-09-23
**Stack:** Java 17, Gradle, Room + SQLCipher, OkHttp 3 (WebSocket), Retrofit

---

## PRIORYTET 1: Bezpieczenstwo (KRYTYCZNE)

### 1.1. Token w CONNECT message zamiast query param

**Problem:** Token w URL WebSocket (`/ws?token=...`) pojawia sie w logach sieciowych, server logs.
**Lokalizacja:** `repository/ShoppingServiceRepository.java:197, 257`

**Kontekst:** OkHttp `okhttp3.WebSocket` obsluguje custom headers przez `requestBuilder.header(key, value)` — ale obecny kod jawnie usuwa `Authorization` header z komentarzem "server ignores it". Po zmianie backendu mozna przeslac token w naglowku lub w CONNECT message.

**Plan:**

Krok 1: Usunac token z URL WebSocket
- `ShoppingServiceRepository.java:197` — usunac `+ "/ws?token=" + user.getAccessToken()`
- `ShoppingServiceRepository.java:257` — usunac `.setQueryParameter("token", newAccessToken)`
- URL: `WEBSOCKET_BASE_URL + shoppingListDomain + "/ws"`

Krok 2: Przekazac token w CONNECT message
- Projekt juz uzywa wlasnego protokolu na null-bytes z message broker
- CONNECT message jest juz wysylany — wystarczy dodac `Authorization` header do message headers
- `WebSocket.java` — method `sendConnect()` powinna wysylac token w naglowku
- Lub: dodac token jako pole w CONNECT payload JSON

Krok 3: Zaktualizowac TokenAuthenticator
- `TokenAuthenticator.java:79-90` — usunac `.removeHeader("Authorization")`
- Usunac `.setQueryParameter("token", newAccessToken)` — token przesylany w message

Krok 4: Usunac comment z TokenAuthenticator
- Obecny komentarz: "the token is passed only via the 'token' query parameter, the server ignores the Authorization header, so it is removed from the request"
- Zmienic na dokumentacje nowego sposobu

**Testy do napisania:**
- Manual test: polaczenie WS bez token w URL, z token w CONNECT message
- Unit test: `ShoppingServiceRepository.initializeWebSocket()` — URL bez token

---

### 1.2. JWT w User.password polu

**Problem:** `User` entity ma pole `password` ktore przechowywac ma access token — myli dane uwierzytelniajace z tokenem.
**Lokalizacja:** `model/user/User.java` (pole `password`)

**Plan:**

Krok 1: Zmienic nazwe pola
- `User.java` — zmienic `String password` na `String accessToken`
- Aktualizowac wszystkie referencje (getter/setter)
- Dodac migration Room jesoli pole jest w database schema

Krok 2: Usunac niejasne uzycie password pola
- Sprawdzic gdzie `user.password` jest uzywane jako access token

---

### 1.3. Memory leak — NetworkReceiver nieodrejestrowany

**Problem:** `NetworkReceiver` rejestrowany w `InitializerActivity` ale nigdzie nieodrejestrwany — leak pamieci.
**Lokalizacja:** `activity/InitializerActivity.java`

**Plan:**

Krok 1: Zarejestrowac unregister w `onDestroy()` lub `onStop()`
- `InitializerActivity.java` — dodac `unregisterReceiver(networkReceiver)`
- Lub: przeniesc receiver do Application class z proper lifecycle management

---

## PRIORYTET 2: Jakosc kodu

### 2.1. O(n^2) lookup w ShoppingServiceRepository

**Problem:** `findItemById()` iteruje przez WSZYSTKIE kategorie i przedmioty — O(n^2).
**Lokalizacja:** `repository/ShoppingServiceRepository.java`

**Plan:**

Krok 1: Stworzyc HashMap dla fast lookup
- `Map<Long, Category> categoryMap`
- `Map<Long, ShoppingItem> itemMap`
- Aktualizowac przy kazdej zmianie stanu

Krok 2: Zmienic `findItemById()` na O(1) lookup

### 2.2. Filter w onBindViewHolder

**Problem:** Logika filtrowania (sendToBought/deleted) w `onBindViewHolder` — powinna byc w ViewModel/Repository.
**Lokalizacja:** Adapter shopping list

**Plan:**

Krok 1: Przeniesc filtrowanie do Repository/ViewModel
- Dodac `getVisibleItems()` w repository
- Adapter tylko mapuje dane na UI

### 2.3. Manual DI zamiast Hilt/Dagger

**Problem:** Reczne tworzenie zaleznosci (Repository, Service, ViewModel) — trudne do testowania.
**Lokalizacja:** `activity/InitializerActivity.java`, `activity/logindialog/LoginDialogOptionalLogin.java`

**Plan:**

Krok 1 (dlugoterminowy): Dodac Hilt/Dagger
- Zmienic manual DI na `@Inject` + `@HiltAndroidApp`
- Dodac `@AndroidEntryPoint` do Activities/ViewModels

Krok 2 (kratkoterminowy): Stworzyc `AppContainer` class
- Singleton z wszystkimi singleton-beans
- Łatwiej testowac i maintainowac

---

## PRIORYTET 3: Testy

### 3.1. Testy Repository

**Plan:**
- `ShoppingServiceRepositoryTest.java` — initializeWebSocket, refreshAndReconnect, CRUD operations
- `TokenAuthenticatorTest.java` — 401/403 handling, token refresh flow

### 3.2. Testy ViewModel

**Plan:**
- `InitializerViewModelTest.java` — login flow, token validation
- `LoginDialogViewModelTest.java` — validation, error handling

### 3.3. Testy Model

**Plan:**
- `UserTest.java` — token management, serialization

---

## PRIORYTET 4: Wydajnosc

### 4.1. Optimistic UI updates

**Problem:** Stan aktualizowany natychmiast bez error handlingu WS send.
**Lokalizacja:** `repository/ShoppingServiceRepository.java`

**Plan:**

Krok 1: Dodac optimistic update z rollback
- Przed wyslaniem WS: dodac item lokalnie
- Po sukcesie: potwierdzic update
- Po bledzie: wycofac zmiany lokalne

### 4.2. Lazy loading danych

**Problem:** Wszystkie dane ladowane na start.
**Lokalizacja:** `InitializerActivity.java`

**Plan:**

Krok 1: Dodac lazy loading dla rzadko uzywanych danych
- Tags, categories, amount types — load on demand
- Preload only essential data (categories, items)
