# KStreamsMatchers

Set of [Hamcrest](https://hamcrest.org/) custom matchers for testing Kafka Streams applications.

`kstreamsmatchers` helps you to write fluent and concise tests.

[![Build Status](https://github.com/RoRoche/kstreamsmatchers/actions/workflows/build-java.yml/badge.svg)](https://github.com/RoRoche/kstreamsmatchers/actions)
[![YAML Lint](https://github.com/RoRoche/kstreamsmatchers/actions/workflows/yamllint.yml/badge.svg)](https://github.com/RoRoche/kstreamsmatchers/actions/workflows/yamllint.yml)
![Nodes.js CI](https://github.com/RoRoche/kstreamsmatchers/actions/workflows/build-npm.yml/badge.svg)

![EO principles respected here](https://www.elegantobjects.org/badge.svg)
[![DevOps By Rultor.com](https://www.rultor.com/b/RoRoche/kstreamsmatchers)](https://www.rultor.com/p/RoRoche/kstreamsmatchers)
![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)

[![PDD status](https://www.0pdd.com/svg?name=RoRoche/kstreamsmatchers)](https://www.0pdd.com/p?name=RoRoche/kstreamsmatchers)

[![codecov](https://codecov.io/gh/RoRoche/kstreamsmatchers/branch/main/graph/badge.svg)](https://codecov.io/gh/RoRoche/kstreamsmatchers)

[![Hits-of-Code](https://hitsofcode.com/github/RoRoche/kstreamsmatchers?branch=main)](https://hitsofcode.com/github/RoRoche/kstreamsmatchers/view?branch=main)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=bugs)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=RoRoche_kstreamsmatchers&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=RoRoche_kstreamsmatchers)

![nullfree status](https://youshallnotpass.dev/nullfree/RoRoche/kstreamsmatchers)
![staticfree status](https://youshallnotpass.dev/staticfree/RoRoche/kstreamsmatchers)
![allfinal status](https://youshallnotpass.dev/allfinal/RoRoche/kstreamsmatchers)
![allpublic status](https://youshallnotpass.dev/allpublic/RoRoche/kstreamsmatchers)
![setterfree status](https://youshallnotpass.dev/setterfree/RoRoche/kstreamsmatchers)
![nomultiplereturn status](https://youshallnotpass.dev/nomultiplereturn/RoRoche/kstreamsmatchers)

[![Maven Central](https://img.shields.io/maven-central/v/com.github.roroche/kstreamsmatchers.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.github.roroche/kstreamsmatchers)
[![Javadoc](https://javadoc.io/badge2/com.github.roroche/kstreamsmatchers/javadoc.svg)](https://javadoc.io/doc/com.github.roroche/kstreamsmatchers)

## ✨ Features

- A custom Hamcrest matcher to check the contents of records coming from TopologyTestDriver.
- A custom Hamcrest matcher to check the contents of records consumed from a topic.

## 📥 Installation

Add the dependency to your project:

```xml
<dependency>
    <groupId>com.github.roroche</groupId>
    <artifactId>kstreamsmatchers</artifactId>
    <version>${latest.version}</version>
</dependency>
```

## 🚀 Usage

1. When dealing with `TopologyTestDriver`, you can use the `OutputTopicContains` matcher to check the contents of the output topic:

```java
TopologyTestDriver driver = new TopologyTestDriver(topology, config);
TestOutputTopic<String, Long> outputTopic = driver.createOutputTopic(/*...*/);
MatcherAssert.assertThat(
        outputTopic,
        new OutputTopicContains<>(
                new KeyValue<>("hello", 1L),
                new KeyValue<>("kafka", 2L),
                new KeyValue<>("streams", 1L)
        )
);
```

2. When dealing with a real Kafka topic, you can use the `TopicContains` matcher to check the contents of the topic:

```java
KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(/*...*/);
consumer.subscribe(Collections.singletonList("output-topic"));
MatcherAssert.assertThat(
        consumer,
        new ConsumerPolls<>(
                new MapEntry<>("hello", 1L),
                new MapEntry<>("kafka", 2L),
                new MapEntry<>("streams", 1L)
        )
);
```

## 🤝 Contributing

Contributions are welcome!

If you'd like to report a bug, suggest a feature, or submit a pull request, please read our
👉 **[Contributing Guide](CONTRIBUTING.md)**

It contains everything you need to know about:

- Development setup
- Coding standards
- Commit conventions
- Pull request process
- Quality requirements

Thank you for helping improve `kstreamsmatchers` 🚀

## 📄 License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.
