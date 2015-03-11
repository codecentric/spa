### spa
SPA stands for **S**mall **P**ersistence library for **A**ndroid and its main goal is to ease and speed up Android development in situations when SQLite persistence is needed.

Using small set of simple annotations, user can map objects to the relational database (SQLite) and avoid writing any code needed for creating database structure. Also, SPA library provides set of functionalities for manipulating persisted data.
Spa-tester is small application that can be used as example how SPA library should be used.

### short usage instructions
- extend *de.codecentric.spa.ctx.DatabaseHelper* and override its *onCreate* and *onUpgrade* methods
- extend *android.app.Application* and inside it initialize *de.codecentric.spa.ctx.PersistenceContext* to trigger scanning of annotated classes
- on creation of your application context provide it with the list of classes that should be mapped and persisted
  - scan those classes by calling *PersistenceContext#init* method
  - scanning process will generate SQL statements for each scanned class by calling *SQLGenerator#generateSQL*
  - it will also create *EntityHelper* instance for each scanned class
  - then you have to instantiate your database helper class, retrieve generated SQL statements by calling *de.codecentric.spa.ctx.DatabaseHelper#retrieveSqlStatements* method...
  - and set your freshly instantiated and SQL-populated database helper into *de.codecentric.spa.ctx.PersistenceContext*
- use *PersistenceContext#getEntityWrapper* to obtain created *EntityWrapper* instance and exploit its features
- usages notes:
  - Do not use primitive types. They are not supported.
  - no relationships on this branch - just "one class - one table" agreement
