[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![Maven Central](https://img.shields.io/maven-central/v/org.finos.legend.depot/legend-depot-server.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22legend-depot)
![Build CI](https://github.com/finos/legend-depot/workflows/Build%20CI/badge.svg)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=security_rating&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=bugs&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)


# legend-depot
The Legend Depot servers provide a rich `REST API` allowing users to query metadata fast and reliably which has been authored in `Legend Studio` and `Legend SDLC`
Legend Depot has two main components:
- **Depot Server:** provides read only metadata query REST API
- **Depot Store Server:** manages internal metadata cache and sources it from maven style repository where models artifacts have been published.

## Getting started

### Development setup

This application uses `Maven 3.6+` and `JDK 11` to build. Simply run `mvn install` to compile.
In order to start the `Depot Server` and `Depot Store Server`, follow the instructions below.

#### Setup Gitlab OAuth

Follow the instructions [here](https://legend.finos.org/docs/getting-started/installation-guide#maven-install) to set up `Gitlab authentication`
Add following callback url to config: `http://127.0.0.1:6201/depot-store/callback`

> Certain store APIs required elevated permissions, add your `Gitlab handle` to `authorisedIdentities.json`

#### Depot Store Server

- Create a JSON configuration: _check out the [sample config](https://github.com/finos/legend-depot/blob/master/legend-depot-server/src/test/resources/sample-server-config.json)_
- Configure your Artifacts Repository provider (artifactRepositoryProviderConfiguration)  _Check out the [instructions here](https://github.com/finos/legend-depot/blob/master/legend-depot-artifacts-refresh/README.md)_
- Start an instance of `Mongo DB`: this is where your metadata will be stored: Add the `MongoDB URL` and `database name` to the `mongo` section of your config file
- Start the server:

```sh
java -cp $SHADED_JAR_PATH org.finos.legend.depot.store.server.LegendDepotStoreServer server $CONFIG_DIR/config.json
```

- Test by opening http://127.0.0.1:6201/depot-store/api/info or the `Swagger` [page](http://127.0.0.1:6201/depot-store/api/swagger)

#### Depot Server

- Create a JSON configuration: Make sure to specify the `Mongo DB` where store server would cache metadata. _Check out the [sample config](https://github.com/finos/legend-depot/blob/master/legend-depot-store-server/src/test/resources/sample-server-config.json)_
- Start the server:

```sh
java -cp $SHADED_JAR_PATH org.finos.legend.depot.server.LegendDepotServer server $CONFIG_DIR/config.json
```

- Test by opening http://127.0.0.1:6200/depot/api/info or the `Swagger` [page](http://127.0.0.1:6200/depot/api/swagger)


### Register metadata projects with Depot Store Server

Metadata projects need to be registered in depot store so that the server can start fetching and caching the models for this project.
This is a one off task and can be done:
- **Manually:** using the end point `api/projects/{projectId}/{groupId}/{artifactId}`
- **Automatically:** more to come on this space

Crucially, key information are the `maven coordinates` and the modeling project its publishing its artifacts to.

## Roadmap

Visit our [roadmap](https://github.com/finos/legend#roadmap) to know more about the upcoming features.

## Contributing

Visit Legend [Contribution Guide](https://github.com/finos/legend/blob/master/CONTRIBUTING.md) to learn how to contribute to Legend.

## License

Copyright 2020 Goldman Sachs

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
