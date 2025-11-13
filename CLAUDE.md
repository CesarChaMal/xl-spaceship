# CLAUDE.md - AI Assistant Guide for XL-Spaceship

## Project Overview

**XL-Spaceship** is a Spring Boot backend application implementing an extended Battleship game with custom ship shapes and multiple game rule variants. The game features a 16x16 grid, five unique ship types with multiple orientations, and four different rule systems for shot allocation.

**Key Facts:**
- **Language:** Java 8
- **Framework:** Spring Boot 1.5.4.RELEASE
- **Build Tool:** Maven
- **Architecture:** Layered MVC with RESTful APIs
- **Storage:** In-memory (HashMap-based, non-persistent)
- **Lines of Code:** ~727 Java LOC across 19 classes

## Repository Structure

```
xl-spaceship/
├── .git/                           # Git repository
├── .gitignore                      # Java/Spring Boot gitignore
├── README.md                       # Basic run instructions
├── info.txt                        # Java SDK setup (Zulu 8)
├── pom.xml                         # Maven build configuration
├── mvnw, mvnw.cmd                  # Maven wrapper
└── src/
    ├── main/
    │   ├── java/com/workingbit/xlspaceship/
    │   │   ├── XlSpaceshipApplication.java              # Main entry point
    │   │   ├── common/AppConstants.java                 # Constants (board size, URLs)
    │   │   ├── controller/                              # REST controllers
    │   │   │   ├── HomeController.java                  # Root endpoint
    │   │   │   ├── ProtocolController.java              # External game protocol
    │   │   │   └── UserController.java                  # User-facing API
    │   │   ├── domain/                                  # Domain models
    │   │   │   ├── Board.java                           # Game board (complex)
    │   │   │   ├── Cell.java                            # Board cell
    │   │   │   ├── Coords.java                          # X/Y coordinates
    │   │   │   ├── EnumCellType.java                    # Cell states
    │   │   │   ├── EnumRule.java                        # Game rules (strategy)
    │   │   │   ├── EnumShipType.java                    # Ship classifications
    │   │   │   ├── Game.java                            # Game state manager
    │   │   │   ├── User.java                            # Player model
    │   │   │   └── ship/                                # Ship implementations
    │   │   │       ├── Ship.java                        # Abstract base
    │   │   │       ├── AClassShip.java
    │   │   │       ├── AngleShip.java
    │   │   │       ├── BClassShip.java
    │   │   │       ├── SClassShip.java
    │   │   │       └── WingerShip.java
    │   │   └── service/
    │   │       └── GameService.java                     # Core business logic
    │   └── resources/
    │       ├── application.yml                          # Empty (defaults)
    │       ├── rest.properties                          # REST paths
    │       └── ships/                                   # Ship shape definitions
    │           ├── aclass/[0-3].ship
    │           ├── angle/[0-3].ship
    │           ├── bclass/[0-3].ship
    │           ├── one/[0-3].ship
    │           ├── sclass/[0-3].ship
    │           └── winger/[0-3].ship
    └── test/
        ├── java/com/workingbit/xlspaceship/domain/
        │   └── WingerShipTest.java                      # Minimal test (commented)
        └── resources/
            ├── application.yml
            └── rest.properties
```

## Technology Stack

### Core Dependencies
- **Spring Boot 1.5.4.RELEASE** - Web framework
  - `spring-boot-starter-web` - REST APIs
  - `spring-boot-starter-jersey` - JAX-RS (unused)
  - `spring-boot-starter-actuator` - Health/metrics
  - `spring-boot-devtools` - Hot reload
- **Apache Commons Lang3 3.5** - Utilities (RandomUtils, StringUtils)
- **Project Lombok** - Boilerplate reduction (@Data annotations)
- **JUnit 4.12** - Testing framework

### Build Configuration
- **Java Version:** 1.8 (compile and target)
- **Encoding:** UTF-8
- **Packaging:** Executable JAR
- **Spring Boot Maven Plugin:** 2.5.14 (for repackaging)

## Architecture and Design Patterns

### Layered Architecture
```
Controllers (REST endpoints)
    ↓
Service Layer (Business logic)
    ↓
Domain Models (Game rules & state)
```

### Design Patterns in Use

1. **MVC Pattern**
   - Controllers handle HTTP
   - Service orchestrates logic
   - Domain contains business rules

2. **Strategy Pattern**
   - `EnumRule` enum implements different shot calculation strategies
   - `getShotCount(Board)` varies by rule type

3. **Template Method Pattern**
   - `Ship` abstract class defines placement algorithm
   - Concrete ships provide type-specific data

4. **Factory Pattern (Implicit)**
   - `Board.randomPlaceShip()` uses reflection to instantiate ships
   - Located at: `src/main/java/com/workingbit/xlspaceship/domain/Board.java:79-97`

5. **DTO Pattern (Loose)**
   - Controllers use `Map<String, Object>` as flexible DTOs
   - **Note:** Not type-safe; consider creating proper DTOs for new features

### Key Architectural Decisions

1. **In-Memory Storage**
   - Games stored in `HashMap<String, Game>` in GameService
   - **Location:** `src/main/java/com/workingbit/xlspaceship/service/GameService.java:16`
   - **Limitation:** Data lost on restart; not scalable
   - **Future:** Needs database for production

2. **Hexadecimal Coordinate System**
   - Board is 16x16, addressed as "YxX" (e.g., "AxB" = row 10, col 11)
   - Parsed at: `GameService.java:66-67`
   - Format: `Integer.parseInt(coord, 16)`

3. **Resource-Based Ship Definitions**
   - Ships defined in text files (e.g., `/ships/winger/winger-0.ship`)
   - Format: First line = dimensions (e.g., "3x5"), then shape with `*` and `.`
   - Allows easy modification without code changes
   - Random variant selection (0-3) adds variety

4. **Dual API Design**
   - **Protocol API** (`/xl-spaceship/protocol`) - For external game servers
   - **User API** (`/xl-spaceship/user`) - For local frontend client
   - Allows different authorization/validation strategies

## Core Components

### 1. Constants (`AppConstants.java`)
**Location:** `src/main/java/com/workingbit/xlspaceship/common/AppConstants.java`

Important constants:
```java
BOARD_SIZE = 16          // 16x16 grid
SHIP_COUNT = 5           // Five ships per player
LOCAL_CLIENT_URL         // CORS allowed origin
```

### 2. Controllers

#### HomeController
- **Path:** `/`
- **Purpose:** Simple welcome endpoint
- Returns: "Home, sweet home…"

#### ProtocolController
- **Base Path:** `/xl-spaceship/protocol`
- **Endpoints:**
  - `POST /game/new` - Create new game (external protocol)
  - `PUT /game/{gameId}` - Receive opponent salvo

#### UserController
- **Base Path:** `/xl-spaceship/user`
- **Endpoints:**
  - `GET /game/{gameId}` - Get game state
  - `PUT /game/{gameId}/fire` - Fire salvo at opponent
  - `POST /game/{gameId}/auto` - Activate autopilot

### 3. Domain Models

#### Game (`Game.java`)
**Purpose:** Game state container
- Holds player and opponent boards
- Tracks current turn
- Manages game rules
- **Key Methods:**
  - `placeShips()` - Randomly places ships on both boards
  - `getPlayerBoardAsList()` - Full visibility
  - `getOpponentBoardAsList()` - Ships hidden

#### Board (`Board.java`) - MOST COMPLEX CLASS
**Purpose:** Core game board logic
- **Size:** 16x16 Cell array
- **Key Responsibilities:**
  - Ship placement with collision detection
  - Salvo processing (hit/miss/kill)
  - Ship count tracking
- **Important Methods:**
  - `randomPlaceShips()` - Places all 5 ship types (line 70)
  - `randomPlaceShip(name, clazz)` - Places single ship via reflection (line 79)
  - `markSalvo(salvos)` - Processes shots, returns results
  - `getBoardAsList(opponent)` - Renders board as strings (hides opponent ships)
  - `getCell(x, y)` - Retrieves cell at coordinates

**Placement Algorithm:**
1. Read ship shape from resource file
2. Random coordinate selection
3. Validate no overlap (includes 1-cell buffer)
4. Retry until successful

#### Ship (`Ship.java` and subclasses)
**Purpose:** Ship representation and placement logic
- **Abstract Base:** Defines common behavior
- **Concrete Types:** AClassShip, AngleShip, BClassShip, SClassShip, WingerShip
- **Key Methods:**
  - `tryPlace(board)` - Validates placement
  - `createShip()` - Reads shape from file
  - `isKilled()` - Checks if all parts hit

#### EnumRule - Game Rules
**Four Variants:**
1. **STANDARD** - Shots = remaining ships
2. **X_SHOT** - Fixed shots (e.g., "5-shot" = 5 shots/turn)
3. **SUPER_CHARGE** - Double shots when killing a ship
4. **DESPERATION** - More shots as you lose ships

**Logic Location:** `EnumRule.java:getShotCount(Board)`

### 4. Service Layer

#### GameService
**Location:** `src/main/java/com/workingbit/xlspaceship/service/GameService.java`

**State:** `Map<String, Game> games` (in-memory)

**Key Methods:**
- `createGame(request)` - Initializes new game, assigns ID "match-{random}"
- `getGame(gameId)` - Returns current state (player board full, opponent hidden)
- `fire(gameId, request, opponent)` - Processes salvo, checks win conditions
- `autopilot(gameId)` - Generates random shots per rules

## Coding Conventions

### Java Style
- **Package:** `com.workingbit.xlspaceship`
- **Indentation:** Spaces (appears to be 4 spaces)
- **Naming:**
  - Classes: PascalCase
  - Methods: camelCase
  - Constants: UPPER_SNAKE_CASE
- **Comments:** Author/date headers on classes
  ```java
  /**
   * Created by Aleksey Popryaduhin on HH:MM DD/MM/YYYY.
   */
  ```

### Lombok Usage
- Use `@Data` on domain models for getters/setters
- Examples: `Board.java`, `Coords.java`, `User.java`
- Reduces boilerplate significantly

### Error Handling (Current State - NEEDS IMPROVEMENT)
- **Current:** `printStackTrace()` in catch blocks
- **Recommendation:** Replace with proper logging (SLF4J)
- **Example:** `Board.java:93`

### Return Types
- Controllers return `Map<String, Object>` (flexible but not type-safe)
- **Recommendation:** Create proper request/response DTOs for new endpoints

## Development Workflow

### Environment Setup

1. **Java Installation:**
   ```bash
   sdk install java 8.0.412.fx-zulu
   sdk use java 8.0.412.fx-zulu
   ```

2. **Build:**
   ```bash
   mvn clean package
   ```

3. **Run:**
   ```bash
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```
   Or with Maven:
   ```bash
   mvn spring-boot:run
   ```

4. **Default Port:** 8080 (Spring Boot default)

### IDE Setup
Project supports:
- IntelliJ IDEA (recommended for Spring Boot)
- Eclipse
- VS Code

All IDE files are gitignored.

### Hot Reload
Spring Boot DevTools is included:
- Changes to Java files trigger automatic restart
- Resources reload without restart

## Testing Guidelines

### Current State
**Minimal testing:**
- Single test class: `WingerShipTest.java`
- Test code is commented out
- JUnit 4 framework configured

### Testing Strategy (Recommended)

1. **Unit Tests**
   - Test each domain class in isolation
   - Mock dependencies
   - Focus on: Ship placement, Board logic, Rule calculations

2. **Integration Tests**
   - Test controller endpoints
   - Use `@SpringBootTest` and `MockMvc`
   - Verify request/response formats

3. **Key Areas to Test:**
   - Ship placement collision detection
   - Salvo processing logic
   - Win condition detection
   - Game rule calculations
   - Coordinate parsing (hex conversion)

### Running Tests
```bash
mvn test
```

## Common Development Tasks

### Adding a New Ship Type

1. **Create ship class:**
   ```java
   // src/main/java/com/workingbit/xlspaceship/domain/ship/NewShip.java
   public class NewShip extends Ship {
       public NewShip(Integer x, Integer y, Object[] lines, Class<?> aClass) {
           super(EnumShipType.NEW_TYPE, x, y, lines, aClass);
       }
   }
   ```

2. **Add to EnumShipType:**
   ```java
   // Add to EnumShipType.java
   NEW_TYPE
   ```

3. **Create ship definition files:**
   ```
   src/main/resources/ships/newtype/new-0.ship
   src/main/resources/ships/newtype/new-1.ship
   src/main/resources/ships/newtype/new-2.ship
   src/main/resources/ships/newtype/new-3.ship
   ```

4. **Update Board.randomPlaceShips():**
   ```java
   randomPlaceShip(String.format("/ships/newtype/new-%s.ship",
       RandomUtils.nextInt(0, 4)), NewShip.class);
   ```

5. **Update AppConstants.SHIP_COUNT** if needed

### Adding a New Game Rule

1. **Add to EnumRule:**
   ```java
   public enum EnumRule {
       STANDARD, X_SHOT, SUPER_CHARGE, DESPERATION, NEW_RULE;

       public int getShotCount(Board board) {
           switch (this) {
               // ... existing cases
               case NEW_RULE:
                   return calculateNewRuleShots(board);
               default:
                   return 1;
           }
       }

       private int calculateNewRuleShots(Board board) {
           // Implement your logic
       }
   }
   ```

2. **Update documentation** explaining the new rule

### Adding a New Endpoint

1. **Create method in appropriate controller:**
   ```java
   @GetMapping("/game/{gameId}/status")
   public Map<String, Object> getGameStatus(@PathVariable String gameId) {
       return gameService.getGameStatus(gameId);
   }
   ```

2. **Add service method:**
   ```java
   public Map<String, Object> getGameStatus(String gameId) {
       // Implementation
   }
   ```

3. **Consider creating proper DTO classes** instead of Map<String, Object>

### Debugging Tips

1. **View board state:**
   - `Board.toString()` is not overridden
   - Use `getBoardAsList(false)` to see full board
   - Cell has custom `toString()` method

2. **Check game state:**
   - GameService stores games in `games` map
   - Use debugger to inspect `games.get(gameId)`

3. **Coordinate conversion:**
   - Remember hex format: "YxX"
   - Use `Integer.parseInt(coord, 16)` for parsing
   - Example: "Fx5" = row 15, col 5

## Git Workflow

### Branch Naming
- Feature branches: `claude/claude-md-{identifier}`
- **Current branch:** `claude/claude-md-mhy19ykcblb288ek-016NQtGXzdDCvNyxyXYHLtn4`

### Commit Guidelines

1. **Message Format:**
   ```
   Short summary (50 chars or less)

   Detailed explanation if needed.
   - Bullet points for multiple changes
   ```

2. **Commit Frequency:**
   - Commit logical units of work
   - Don't commit broken code
   - Each commit should build successfully

3. **What to Commit:**
   - Source code changes
   - Test additions/modifications
   - Configuration updates
   - Documentation updates

4. **What NOT to Commit:**
   - Build artifacts (target/)
   - IDE files (.idea/, *.iml)
   - Environment configs (*.env)
   - See `.gitignore` for full list

### Pushing Changes
```bash
# Always use -u flag for first push
git push -u origin <branch-name>

# Retry with exponential backoff if network issues
# (2s, 4s, 8s, 16s delays)
```

## Important Gotchas and Known Issues

### 1. Coordinate System Confusion
- **Issue:** Coordinates are "YxX" format, not "XxY"
- **Example:** "AxB" means row 10 (A in hex), column 11 (B in hex)
- **Location:** `GameService.java:66-67`

### 2. In-Memory Storage Limitations
- **Issue:** All game state lost on restart
- **Impact:** Not production-ready
- **Future:** Needs Redis or database

### 3. No Input Validation
- **Issue:** No validation on incoming requests
- **Risk:** Invalid coordinates cause exceptions
- **Recommendation:** Add validation before processing

### 4. CORS Configuration
- **Issue:** Hardcoded to `http://localhost:4200`
- **Location:** `XlSpaceshipApplication.java:19`
- **Production:** Use environment-based configuration

### 5. Exception Handling
- **Issue:** `printStackTrace()` instead of logging
- **Locations:** Throughout codebase
- **Fix:** Replace with SLF4J logging

### 6. Type Safety
- **Issue:** `Map<String, Object>` everywhere
- **Impact:** No compile-time type checking
- **Recommendation:** Create DTOs for requests/responses

### 7. Reflection Usage
- **Issue:** Ships instantiated via reflection
- **Location:** `Board.java:87-88`
- **Impact:** Runtime errors if constructor changes
- **Caution:** Maintain constructor signature

### 8. Ship Count Management
- **Issue:** Manual increment in `randomPlaceShip()` (Board.java:96)
- **Risk:** Easy to forget when adding ships
- **Better:** Auto-calculate based on placed ships

### 9. No Authentication/Authorization
- **Issue:** Anyone can access any game
- **Security:** Major concern for production
- **Future:** Add Spring Security

### 10. Test Coverage
- **Issue:** Effectively 0% test coverage
- **Impact:** Regression risk
- **Priority:** HIGH - Add tests before major changes

## Configuration Files

### application.yml
**Location:** `src/main/resources/application.yml`
- Currently empty (uses Spring Boot defaults)
- Default port: 8080
- **To change port:** Add `server.port: 9000`

### rest.properties
**Location:** `src/main/resources/rest.properties`
```properties
USER_RESOURCE=/xl-spaceship/user
PROTOCOL_RESOURCE=/xl-spaceship/protocol
```
**Note:** These appear unused in current code (controllers use hardcoded paths)

### pom.xml Key Points
- Java 8 required
- UTF-8 encoding
- Spring Boot 1.5.4 (older version)
- AWS SDK declared but unused

## API Reference

### Protocol API (`/xl-spaceship/protocol`)

#### POST /game/new
Create new game (external protocol)
- **Request:** `{user_id, full_name, rules}`
- **Response:** `{user_id, full_name, game_id, starting, rules}`

#### PUT /game/{gameId}
Receive opponent's salvo
- **Request:** `{salvo: ["YxX", ...]}`
- **Response:** `{salvo: {coord: "hit|miss|kill"}, game: {player_turn|won}}`

### User API (`/xl-spaceship/user`)

#### GET /game/{gameId}
Get current game state
- **Response:** `{self: {user_id, board}, opponent: {user_id, board}, game: {player_turn}}`

#### PUT /game/{gameId}/fire
Fire salvo at opponent
- **Request:** `{salvo: ["YxX", ...]}`
- **Response:** `{salvo: {coord: "hit|miss|kill"}, game: {player_turn|won}}`

#### POST /game/{gameId}/auto
Activate autopilot
- **Response:** Same as fire endpoint

### Response Formats

**Board Format:** Array of 16 strings, each 16 characters
- `.` = Unknown
- `*` = Ship (only visible on own board)
- `-` = Miss
- `x` = Hit

**Salvo Results:**
- `"hit"` - Shot hit a ship
- `"miss"` - Shot missed
- `"kill"` - Shot destroyed the last remaining part of a ship

## Future Improvements & Technical Debt

### High Priority
1. **Add comprehensive tests** (unit and integration)
2. **Replace Map<String, Object> with DTOs** (type safety)
3. **Add input validation** (prevent invalid requests)
4. **Implement proper error handling** (replace printStackTrace)
5. **Add logging** (SLF4J/Logback)

### Medium Priority
6. **Database integration** (PostgreSQL/MySQL for persistence)
7. **Add Spring Security** (authentication/authorization)
8. **API documentation** (Swagger/OpenAPI)
9. **Externalize configuration** (environment variables)
10. **Health checks** (proper Actuator configuration)

### Low Priority
11. **Upgrade Spring Boot** (1.5.4 → 3.x requires major refactoring)
12. **WebSocket support** (real-time updates)
13. **Metrics and monitoring** (Prometheus/Grafana)
14. **Containerization** (Docker)
15. **CI/CD pipeline** (GitHub Actions)

## Working with This Codebase as an AI Assistant

### Before Making Changes
1. **Read relevant files** using Read tool
2. **Understand the context** - Don't change in isolation
3. **Check dependencies** - Ship changes affect Board, Game, etc.
4. **Verify constants** - Check AppConstants for board size, ship count

### Making Changes
1. **Follow existing patterns** - Use Lombok, follow naming conventions
2. **Maintain backwards compatibility** - Existing API contracts
3. **Update related files** - If you change Ship, update Board placement
4. **Consider impact** - Changes to domain models affect service layer

### Testing Changes
1. **Build after changes:** `mvn clean package`
2. **Run the application** to verify
3. **Test API endpoints** manually or with curl/Postman
4. **Add tests** for new functionality

### Documentation
1. **Update this file** if architecture changes
2. **Add JavaDoc** for new public methods
3. **Update README.md** if run instructions change

### Common Pitfalls
- Don't break Ship constructor signature (reflection dependency)
- Don't change AppConstants without checking all usages
- Don't forget to increment shipCount when adding ships
- Remember coordinate format is hex and "YxX"

## Key Files Reference Quick Guide

| Component | File Location | Lines |
|-----------|---------------|-------|
| Main Entry | `XlSpaceshipApplication.java` | 27 |
| Constants | `common/AppConstants.java` | ~30 |
| Game Logic | `service/GameService.java` | 105 |
| Board Logic | `domain/Board.java` | ~200 |
| Ship Base | `domain/ship/Ship.java` | ~150 |
| Game Rules | `domain/EnumRule.java` | ~50 |
| User Controller | `controller/UserController.java` | ~50 |
| Protocol Controller | `controller/ProtocolController.java` | ~40 |

## Support and Resources

- **Build Issues:** Check Java version (must be 1.8)
- **Runtime Issues:** Check port 8080 availability
- **CORS Issues:** Update `XlSpaceshipApplication.java:19`
- **Ship Placement Issues:** Check ship definition files format
- **Maven Issues:** Use `./mvnw` instead of system Maven

---

**Last Updated:** 2025-11-13
**Codebase Version:** 0.0.1-SNAPSHOT
**Analysis Generated by:** Claude AI Assistant

---

## Quick Start Checklist for New AI Assistants

- [ ] Read this entire CLAUDE.md file
- [ ] Check current branch: `claude/claude-md-mhy19ykcblb288ek-016NQtGXzdDCvNyxyXYHLtn4`
- [ ] Review pom.xml for dependencies
- [ ] Read AppConstants.java for key configuration
- [ ] Understand Board.java (most complex class)
- [ ] Review GameService.java for business logic flow
- [ ] Check Ship.java and subclasses for placement logic
- [ ] Understand coordinate system (hex "YxX" format)
- [ ] Note in-memory storage limitation
- [ ] Remember: Tests are minimal (add tests for changes!)
