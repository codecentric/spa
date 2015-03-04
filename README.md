### spa
SPA stands for **S**mall **P**ersistence library for **A**ndroid and its main goal is to ease and speed up Android development in situations when SQLite persistence is needed.

Using small set of simple annotations, user can map objects to the relational database (SQLite) and avoid writing any code needed for creating database structure. Also, SPA library provides set of functionalities for manipulating persisted data.
VoiceNotes is small application that can be used as example how SPA library should be used.

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
  - Loading is done lazily, user will have to load any relationship objects manually (hopefully not in the future...)
  - Persisting is done eagerly, everything attached to the object being persisted, will be persisted also. No synchronization will be done (no deletion of missing objects or anything of that kind), only persisting of what is present on the object is done.
  - Deletion is done eagerly meaning when deleting object, its children will be deleted too (not only those attached to the object, but all currently in database). So,
	- when deleting an object, row in database table corresponding to a field annotated with @OneToOne is deleted too
	- when deleting an object, all rows in database table corresponding to a field annotated with @OneToMany are deleted too
	- when deleting an object, fields annotated with @ManyToOne are not processed during cascading deletion

### Relationship mapping
- Only unidirectional supported until now...
	- @OneToOne annotation: database table corresponding to the class of a field annonatated with this annotation contains foreign key column referencing the other side of the relationship.
	- @OneToMany annotation: database table corresponding to the class of a field annonatated with this annotation contains foreign key column referencing database table corresponding to the declaring class of that field.
	- @ManyToOne annotation: database table corresponding to the declaring class of a field annotated with this annotation contains foreign key column referencing the other side of the relationship.
