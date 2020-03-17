# PostgreSQL COPY Helper

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.onozaty/postgresql-copy-helper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.onozaty/postgresql-copy-helper)

PostgreSQL COPY Helper は、PostgreSQLのCOPYコマンドを利用するためのヘルパクラスを提供するJava用ライブラリです。  
オブジェクトの一覧を簡単かつ高速にインポートできます。

PostgreSQLのCOPYコマンドは、INSERTと比べてとても高速に動作します。

* [PostgreSQL: Documentation: COPY](https://www.postgresql.org/docs/current/sql-copy.html)

## セットアップ

PostgreSQL COPY Helper は jCenter および Maven Central で公開されています。  
依存関係に追加するだけで利用可能です。

### maven

```xml
<dependency>
	<groupId>com.github.onozaty</groupId>
	<artifactId>postgresql-copy-helper</artifactId>
	<version>1.1.0</version>
	<type>pom</type>
</dependency>
```

### Gradle

```groovy
implementation 'com.github.onozaty:postgresql-copy-helper:1.1.0'
```

## 利用方法

登録したいデータを表すクラスを定義します。

`@Table`で対象のテーブル名、`@Column`でカラム名を指定します。

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

`CopyHelper.copyFrom`で対象のデータリストを指定するだけで、COPYコマンドが実行されます。

```java
BaseConnection connection = (BaseConnection) DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

List<Item> items = generateItems();
CopyHelper.copyFrom(connection, items, Item.class);
```

## パフォーマンスについて

下記のプロジェクトにて、COPYコマンドのパフォーマンスを測定しています。

* https://github.com/onozaty/java-sandbox/tree/master/postgresql-copy-helper-example

100,000件で、それぞれ下記のような時間となっています。

| パターン | 時間(msec) |
|---------|----------:|
| 1件ずつINSERT | 12,000 |
| Batch INSERT | 1,000 |
| COPYコマンド(本ライブラリを利用) | 100 |
