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
- usages notes:
  - Do not use primitive types. They are not supported.
  - Loading is done lazily, user will have to load any relationship objects manually (hopefully not in the future...)
  - Persisting is done eagerly, everything attached to the object being persisted, will be persisted also. No synchronization will be done (no deletion of missing objects or anything of that kind), only persisting of what is present on the object is done.
  - Deletion is done eagerly meaning when deleting object, its children will be deleted too (not only those attached to the object, but all currently in database). So,
	- when deleting an object, row in database table corresponding to a field annotated with @OneToOne is deleted too
	- when deleting an object, all rows in database table corresponding to a field annotated with @OneToMany are deleted too
	- when deleting an object, fields annotated with @ManyToOne are not processed during cascading deletion

## Relationship mapping
- Only unidirectional supported until now...
	- @OneToOne annotation: class containing field annotated with this annotation is the 'parent' of the relationship. Database table corresponding to the 'child' side of the relationship will contain foreign key referencing 'parent' table. *(Should we turn this around?)*
	- @OneToMany annotation: class containg field annotated with this annotation is the 'parent' of the relationship. Database table corresponding to the 'child' side of the relationship will contain foreign key referencing 'parent' table.
	- @ManyToOne annotation: class containing field annotated with this annotation is the 'child' of the relationship. Database table corresponding to it will contain foreign key referencing 'parent' table.