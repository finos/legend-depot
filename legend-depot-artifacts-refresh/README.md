[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![Maven Central](https://img.shields.io/maven-central/v/org.finos.legend.depot/legend-depot-server.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22legend-depot)
![Build CI](https://github.com/finos/legend-depot/workflows/Build%20CI/badge.svg)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=security_rating&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=bugs&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)


# legend depot store artifacts repository

Model artifacts get published to a repository from which Depot Store will retrieve and store them in its internal cache.  Depot server uses an Artifact Repository Provider api which might have multiple implementations. 

You will have to provide config details of such implementation under the artifactRepositoryProviderConfiguration section in the server config:

- <B>Maven</B> artifacts repository implementation( see configuration instructions [here](https://github.com/finos/legend-depot/blob/master/legend-depot-artifacts-repository-maven-impl/README.md))

## Check your artifact repository config is working

- make sure the host is running Depot Stores can access the repository, ie there is no network issues
- Use the below api to confirm depot store server repository connection is configured properly:


```sh
http://127.0.0.1:6201/depot-store/api/swagger#/Artifacts/getRepositoryVersions

you will have to provide the groupid and artifactid for your model project
it should return the versions that have been publidhed for your model project
```




