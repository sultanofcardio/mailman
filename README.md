# mailman

A kotlin library that sends email using javamail. That's it, that's all it does - because sometimes that's
all you want to do

## Usage

Add the dependency in your pom.xml

```xml
<repositories>
    .
    .
    .
    <repository>
        <id>sultanofcardio</id>
        <url>https://repo.sultanofcardio.com/artifactory/sultanofcardio</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.sultanofcardio</groupId>
        <artifactId>mailman</artifactId>
    <version>3.0.0</version>
</dependency>
```

Or build.gradle

```groovy
repositories {
    maven { url "https://repo.sultanofcardio.com/artifactory/sultanofcardio" }
}

implementation 'com.sultanofcardio:mailman:3.0.0'
``` 

Or build.gradle.kts

```kotlin
repositories {
    maven { url = URI.create("https://repo.sultanofcardio.com/artifactory/sultanofcardio") }
}

implementation("com.sultanofcardio:mailman:3.0.0")
``` 

Configure a mail server
```kotlin
val server = MailServer(host, port, username, password = password) // Password is optional
```

Send an email
```kotlin
val sent: Boolean = server.sendEmail(Email(from, subject, body, recipient1, recipient2))
```

or

```kotlin
val sent: Boolean = server.sendEmail(from, subject, body, recipient) {
    cc { +ccEmail }

    bcc { +bccEmail }
}
```

Sending email in kotlin should be that easy.

Oh - yeah, I'm sure you could use this in Java too
