[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
![legend-build](https://github.com/finos/legend-depot/workflows/legend-build/badge.svg)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=legend-depot&metric=security_rating&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=legend-depot&metric=bugs&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)


# legend-depot
The Legend Depot Server provides a rich REST API allowing users to query metadata fast and realiably which has been authored in Legend Studio/SDLC
Legend Depot has two main components:
- Depot Server: read only metadata query REST API
- Depot Store Server: manages internal metadata cache and sources it from maven style repository where models artifacts have been published.

## Usage example

### Depot Store Server

Start by creating a json configuration file based on your particular environment. A sample configuration file is included to help you get started. You will need to supply some information, such as the host and port your store server is running on.

You need an instance of mongoDb where your metadata will be stored. Add the mongodb URL and database name to the mongo section of your config file. No prior setup its required and you can choose any database name.

You will also need to configure a maven settings.xml config file in order to let the server know from what maven repository to fetch and cache the published metadata artifacts. An example file has been provided to help you.

Once you have your configuration file, you can run the server with Java 8 or later. You can use a command such as this to start the server:

java -cp $SHADED_JAR_PATH org.finos.legend.depot.store.server.LegendDepotStoreServer server $CONFIG_DIR/config.json
If you want to use the shaded JAR built by mvn install in this project, you can get it from legend-depot-store-server/target/legend-depot-store-server-*.jar. 

You can test by trying http://127.0.0.1:8076 in a web browser. The swagger page can be accessed at http://127.0.0.1:8076/depot-store/api/swagger.

Certain store APIs required elevated permissions.

#### Registeting metadata projects with depot store server

Metadata projects need to be registered in depot store so that the server can start feching and caching the models for this project.
This is a one off task and can be done:
- manually: using the following end point; api/projects/{projectId}/{groupId}/{artifactId}
- automaticaly: more to come on this space

Crucially, key information are the maven coordinates the modeling project its publishing its artifacts to.


### Depot Server

Start by creating a json configuration file based on your particular environment. A sample configuration file is included to help you get started. You will need to supply some information, such as the host and port your store server and more importanly mongodb where store server its caching metadata.

Once you have your configuration file, you can run the server with Java 8 or later. You can use a command such as this to start the server:

java -cp $SHADED_JAR_PATH org.finos.legend.depot.server.LegendDepotServer server $CONFIG_DIR/config.json
If you want to use the shaded JAR built by mvn install in this project, you can get it from legend-depot-server/target/legend-depot-server-*.jar. 

You can test by trying http://127.0.0.1:8076 in a web browser. The swagger page can be accessed at http://127.0.0.1:8075/depot/api/swagger.

## Development setup

This application uses Maven 3.6+ and JDK 11 to build. Simply run `mvn install` to compile.
In order to start the store and depot server follow the instructions above.

## Roadmap

Visit our [roadmap](https://github.com/finos/legend#roadmap) to know more about the upcoming features.

## Contributing

Visit Legend [Contribution Guide](https://github.com/finos/legend/blob/master/CONTRIBUTING.md) to learn how to contribute to Legend.

## License

Copyright 2020 Goldman Sachs

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
