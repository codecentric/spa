## spa
SPA stands for **S**mall **P**ersistence library for **A**ndroid and its main goal is to ease and speed up Android development in situations when SQLite persistence is needed.

Using small set of simple annotations, user can map objects to the relational database (SQLite) and avoid writing any code needed for creating database structure. Also, SPA library provides set of functionalities for manipulating persisted data.
VoiceNotes is small application that can be used as example how SPA library should be used.

## short usage instructions
- extend *de.codecentric.spa.ctx.DatabaseHelper* and override its *onCreate* and *onUpgrade* methods
- extend *de.codecentric.spa.ctx.PersistenceApplicationContext* and register it in your AndoridManifest.xml (application name)
- on creation of your application context provide it with the list of classes that should be mapped and persisted
  - scan those classes by calling *PersistenceApplicationContext#inspectClass* method
  - generate SQL statements for each scanned class by calling *SQLGenerator#generateSQL*
  - create *EntityHelper* instance for each scanned class
  - instantiate your database helper class
- use *PersistenceApplicationContext#getEntityWrapper* to obtain created *EntityWrapper* instance and exploit its features