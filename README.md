# OAuthTest

A graphical user interface for testing and learning OAuth.

> This project is currently in early development and not yet ready for usage

## How to build

Clone this project from GitHub:

```
git clone https://github.com/curityio/oauthtest.git
```

Enter the project root directory and run the Gradle `fatJar` task:

```
cd oauthtest
./gradlew fatJar
```

> Note: The build is managed by the Gradle wrapper, so you don't need to download Gradle itself, the correct version of Gradle
  will be automatically downloaded when first run.

This will create a runnable jar in the `oauthtest-core/build/libs` directory.

## Running the Application

With Gradle:

```
./gradlew run
```

Using the runnable jar:

1. First, build a runnable jar:

```
./gradlew fatJar
```

2. Now, run the jar with java:

```
java -jar oauthtest-core/build/libs/oauthtest-run.jar
```
