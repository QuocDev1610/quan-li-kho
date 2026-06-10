# Inventory JavaFX Desktop Client

JavaFX desktop client for the Spring Boot REST API.

## Run during development

```powershell
cd desktop-client
mvn javafx:run
```

## Build an exe

With a JDK that includes `jpackage`:

```powershell
cd desktop-client
mvn package
jpackage --type exe --name InventoryClient --input target --main-jar inventory-desktop-client-1.0.0.jar --main-class inventory.desktop.MainApp
```

JWT is stored only in memory for the current login session. Logout clears the token from memory.
