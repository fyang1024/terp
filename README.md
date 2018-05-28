# TERP 

## What is TERP?

TERP stands for The Exchange Rate Path.

It tries to solve the following problem: 

Given prices of currencies on various exchanges

Assuming same currency can be transferred between different exchanges at no cost

What is the best exchange rate between any two currencies, 
and what trades and transfers need to be made to achieve that rate?


# How to Build

## Prepare dependencies

* JDK 1.8 (JDK 1.9+ probably work, but not tested yet)
* Gradle, see [Install Gradle](https://gradle.org/install/)

## Getting the code

* Use Git from the Terminal, see the [Setting up Git](https://help.github.com/articles/set-up-git/) and [Fork a Repo](https://help.github.com/articles/fork-a-repo/) articles.
In the shell command, type:
```bash
git clone https://fyang1024@bitbucket.org/fyang1024/terp.git
```
## Building from source code

* Build in the Terminal

```bash
cd terp
./gradlew build
```

* Build an executable JAR

```bash
./gradlew clean shadowJar
```
* Build in [IntelliJ IDEA](https://www.jetbrains.com/idea/) (community version is enough):

  1. Start IntelliJ. Select `File` -> `Open`, then locate to the terp folder which you have git cloned to your local drive. Then click `Open` button on the right bottom.
  2. Check on `Use auto-import` on the `Import Project from Gradle` dialog. Select JDK 1.8 in the `Gradle JVM` option. Then click `OK`.
  3. IntelliJ will open the project and start gradle syncing, which will take several minutes, depending on your network connection and your IntelliJ configuration
  4. After the syncing finished, select `Gradle` -> `Tasks` -> `build`, and then double click `build` option.

# How to Run

* Use the executable JAR

```bash
cd build/libs 
java -jar tenx-terp.jar
```

* In IntelliJ IDEA
  1. After the building finishes, locate `Console` in the project structure view panel, which is on the path `terp/src/main/java/tech/tenx/terp/Console`.
  2. Select `Console`, right click on it, and select `Run 'Console.main()'`, then `Console` starts running.


# Contact

For any enquiries, please contact
* Fei Yang <fyang1024@gmail.com>