# try_Spliterator

## Maven

### 確認

```shell
$ mvn -version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 17.0.13, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-arm64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "5.15.153.1-microsoft-standard-wsl2", arch: "aarch64", family: "unix"
$
```

### 新規プロジェクト作成 with Cucumber

- [Cucumber Tutorial](https://cucumber.io/docs/guides/10-minute-tutorial/?lang=java)

```shell
$ mvn archetype:generate                     \
"-DarchetypeGroupId=io.cucumber"           \
"-DarchetypeArtifactId=cucumber-archetype" \
"-DarchetypeVersion=7.20.1"                \
"-DgroupId=hellocucumber"                  \
"-DartifactId=hellocucumber"               \
"-Dpackage=hellocucumber"                  \
"-Dversion=1.0.0-SNAPSHOT"                 \
"-DinteractiveMode=false"
$
```

### 新規プロジェクト作成

```shell
$ mvn archetype:generate -DgroupId=com.example -DartifactId=try-spliterator -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
$ 
```

### 基本コマンド

```shell
$ cd hellocucumber/
$ mvn clean
$ mvn test
$
```
