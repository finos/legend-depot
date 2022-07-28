[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![Maven Central](https://img.shields.io/maven-central/v/org.finos.legend.depot/legend-depot-server.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22legend-depot)
![Build CI](https://github.com/finos/legend-depot/workflows/Build%20CI/badge.svg)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=security_rating&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=bugs&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)

# legend depot store maven repository artifacts

This implementation provides support for maven style repositories. For example maven central or the maven api for gitlab packages registry.

You need to provide a `settings.xml`: this will let the server know from which `maven` repository to fetch and cache the published metadata artifacts. _Check out the [sample](https://github.com/finos/legend-depot/blob/master/legend-depot-store-server/src/test/resources/sample-server-config.json)_


## Configuration

Example settings.xml can be found [here]((https://github.com/finos/legend-depot/blob/master/legend-depot-store-server/src/test/resources/sample-repository-settings.xmld))) with examples on how to retrieve packages in the gitlab registry

The implementation will use a local maven repository where artifacts will be downloaded as you would with any maven style builds. This is determine by the localRepository property in the settings.xml file

Make sure the host is running Depot Store has writable access and that location exists.

## Debugging issues

If you are getting resolve exception you could enable below debug logs in the logging section of your server config.

- "org.jboss.shrinkwrap.resolver": "debug"
- "org.eclipse.aether": "debug"