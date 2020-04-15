# XmlConfigMapper

## What is XmlConfigMapper?

`XmlConfigMapper` uses annotation processing to generate the XML-based config file parser for java model classes (POJO).
This is not a standard XML deserializer. Its aim is reducing code as much as possible when you want to read something from XML as config files.

## Requirements

XmlConfigMapper requires Java 1.8 or later.

## How to use

### Maven

For Maven-based projects, add the following to your POM file in order to use XmlConfigMapper:

```xml
<properties>
    <xmlconfigmapper.version>0.2.0</xmlconfigmapper.version>
</properties>

<dependencies>
    <dependency>
        <groupId>com.github.shootmoon</groupId>
        <artifactId>xmlconfigmapper-core</artifactId>
        <version>${xmlconfigmapper.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.github.shootmoon</groupId>
                        <artifactId>xmlconfigmapper-processor</artifactId>
                        <version>${xmlconfigmapper.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle

For Gradle, you need something along the following lines:

```groovy
plugins {
    id "net.ltgt.apt" version "0.21"
}

dependencies {
    compile 'com.github.shootmoon:xmlconfigmapper-core:0.2.0'

    annotationProcessor 'com.github.shootmoon:xmlconfigmapper-processor:0.2.0'
    testAnnotationProcessor 'com.github.shootmoon:xmlconfigmapper-processor:0.2.0' // if you are using XmlConfigMapper in test code
}
```

## Mark a class as model class

To mark a class as deserializeable by `XmlConfigMapper` you have to annotate your model class with `@XmlConfigBean`.

```java
@XmlConfigBean(name = "book") // name is optional. Per default we use class name in lowercase
public class Book {

    String title; 
}
```

## Property field

Reading the following XML(attribute is not supported):

```xml
<root>
    <book>
        <id>1</id>
    </book>
</root>
```

```java
@XmlConfigBean
public class Book {
    
    String id; 
}
```

Per default the field name will be used as name, but you can customize field within the `@Property(name = "id")` annotation, 

## Type Converter

Property field can read `String`, `Integer`, `Boolean`, `Long`, `Double`, `LocalDateTime`. You can also specify your own type converter that takes the xml text content as input and convert it to the desired type:

```xml
<root>
    <book>
        <id>123</id>
        <publish_date>2015-11-25</publish_date>
    </book>
</root>
```

```java
@XmlConfigBean
public class Book {

    String id; 
    
    @Property(name = "publish_date", converter = MyDateConverter.class)
    Date published; 
}
```

```java
public class MyDateConverter implements TypeConverter<Date> {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd"); // SimpleDateFormat is not thread safe!
    
    @Override
    public Date read(String value) {
    return formatter.parse(value);
    }
}
```

Your custom `TypeConverter` must provide an empty (parameter less) constructor).

## Element field

In XML you can nest child elements in element:

```xml
<root>
    <store>
        <book>
            <id>1</id>
        </book>
        <book>
            <id>2</id>
        </book>
        <book>
            <id>3</id>
        </book>
    </store>
</root>
```

```java
@XmlConfigBean
public class Book {

    String id;
}

@XmlConfigBean
public class Store {

    List<Book> books;
}
```

`XmlConfigMapper` will parse instances of `Book` automatically for you.

## Path

Sometimes you maybe want to put nest child elements under a specific element like this:

```xml
<root>
    <store>
        <books>
            <book>
                <id>1</id>
            </book>
            <book>
                <id>2</id>
            </book>
            <book>
                <id>3</id>
            </book>
        </books>
    </store>
</root>
```

To parse that into java classes we would have to add a `Books` class. This isn't really memory efficient because we have to instantiate `Books` to access `<book>`. With `@Path` we can emulate this XML nodes:

```java
@XmlConfigBean
public class Store {

    @Path("books")
    List<Book> books;
}
```

`XmlConfigMapper` will read `<book>` without the extra cost of allocating such an object. 

## The mapper interface

To generate a mapper for reading `Store` object, a mapper interface needs to be defined:

```java
@XmlConfigMapper
public interface StoreMapper {
    
    MyMapper INSTANCE = XmlConfigMappers.getMapper(MyMapper.class);

    @XmlConfigMapping
    List<Store> getStoreList(String filePath);

    //or just first element
    @XmlConfigMapping
    Store getStore(String filePath);
}