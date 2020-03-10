# postgresql-copy-helper

Provides a helper class for using the PostgreSQL COPY command.  
Import a list of objects easily and quickly.

PostgreSQL COPY command is much faster than INSERT.

* [PostgreSQL: Documentation: COPY](https://www.postgresql.org/docs/current/sql-copy.html)

## Setup

postgresql-copy-helper is published on jCenter.  
It is available simply by adding it to a dependency.

### maven

```xml
<dependency>
	<groupId>com.github.onozaty</groupId>
	<artifactId>postgresql-copy-helper</artifactId>
	<version>0.0.1</version>
	<type>pom</type>
</dependency>
```

### Gradle

```groovy
implementation 'com.github.onozaty:postgresql-copy-helper:0.0.1'
```

## Usage

Define a class that represents the structure of the data you want to register.

Annotate the target table name with `@Table` and the column name with `@Column`.

```java
package postgresql.copy.helper.example;

import com.github.onozaty.postgresql.copy.bean.Column;
import com.github.onozaty.postgresql.copy.bean.Table;

@Table("items")
public class Item {

    @Column("id")
    private final int id;

    @Column("name")
    private final String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

Just specify the target objects with `CopyHelper.copyFrom` and the COPY command will be executed.

```java
BaseConnection connection = (BaseConnection) DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

List<Item> items = generateItems();
CopyHelper.copyFrom(connection, items, Item.class);
```

## About performance

The following project is measuring the performance of the COPY command.

* https://github.com/onozaty/java-sandbox/tree/master/postgresql-copy-helper-example

After importing 100,000 data, the following results were obtained.

| Pattern | Elapsed time (msec) |
|---------|----------:|
| INSERT one by one | 12,000 |
| Batch INSERT | 1,000 |
| COPY command (Use this library) | 100 |
