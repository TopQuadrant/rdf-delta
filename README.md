This branch is version `0.8.2-tq-4`, a patch on top of RDF Delta 0.8.2
that uses Jena 3.16.0 and Log4j 2.17.0. To deploy to TQ's repository:

```
mvn deploy
```

---

# RDF Delta

RDF Delta provides a system for recording and publishing changes to RDF
Datasets. It is built around idea of change logs:

* _RDF Patch_ -  a format for recording changes to an RDF Dataset
* _RDF Patch Log_ - organise patches in to a log of changes 
to an RDF Dataset with HTTP access. 

RDF Patch Logs can be used for:

* Replicated datasets - 2 or more copies of a single dataset for high
availability of the data.
* Incremental backup of a dataset.
* Recording changes 
* Generate alerts based on changes, either to the dataset as a whole or
specific resources within the dataset.

RDF Delta provides a system for keeping copies of an RDF Dataset
up-to-date using the RDF Patch Log as a journal of changes to be applied.

## Documentation

Website: https://afs.github.io/rdf-delta

## High Availablity Apache Jena Fuseki

https://afs.github.io/rdf-delta/ha-fuseki.html

## Software

Artifacts: http://central.maven.org/maven2/org/seaborne/rdf-delta

RDF Delta distribution (patch log server and Apache Jena Fuseki with
replicated dataset support)

http://central.maven.org/maven2/org/seaborne/rdf-delta/rdf-delta-dist

### RDF Patch:

```
    <dependency>
      <groupId>org.seaborne.rdf-delta</groupId>
      <artifactId>rdf-patch</artifactId>
      <version>X.Y.Z</version>
    </dependency>
```

### RDF Delta client library:
```
    <dependency>
      <groupId>org.seaborne.rdf-delta</groupId>
      <artifactId>rdf-delta-client</artifactId>
      <version>X.Y.Z</version>
    </dependency>
```

## Status

[![Build Status](https://api.travis-ci.org/afs/rdf-delta.svg)](https://travis-ci.org/afs/rdf-delta)

## Contributing

See [CONTRIBUTING](.github/CONTRIBUTING.md).
