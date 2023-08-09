[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![Maven Central](https://img.shields.io/maven-central/v/org.finos.legend.depot/legend-depot-server.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22legend-depot)
![Build CI](https://github.com/finos/legend-depot/workflows/Build%20CI/badge.svg)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=security_rating&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=finos_legend-depot&metric=bugs&token=69394360757d5e1356312ddfee658a6b205e2c97)](https://sonarcloud.io/dashboard?id=legend-depot)

# Legend store migration 
## 1. Project Data to Version Data to its own collection (for version 1.5.0 and up)
### What is Project Data?
Project Data is a way of storing information about projects with its respective versions and their dependencies and properties.
A simple example: for Project A, we would store all its valid versions we find in central repository, all the dependencies and properties of all the versions.
### Why is this migration required?
The current form of storage is not the most scalable way to store this useful information,
as we simply mesh all properties and all dependencies of all versions for a particular project. We want to simplify operations like dependency analysis,
but the current storage form doesn't provide any advantage. Since, metadata of any studio project is versioned, we want to start storing project information with respect to versions separately.
### What does this migration mean?
We will create a new collection called "versions" in mongo store, which would have decomposed version of project data.
The collection would be composed of objects for each version of a particular project and its respective dependencies and properties.
We will also have the older collection "project-configurations" present, which would now only store the coordinates (projectId, groupId, artifactId) of the project
## Steps to follow:
1. Use the API /migrations/migrateToVersionData, to populate this new "versions" collection.
2. Once done, using the /indexes (create indexes if absent) you can create the index for this collection.
3. Test all the changes and once sure you can use /migrations/cleanupProjectData, to clean up the project data we store.

## 2. Transitive dependencies to Version Data Collection (for version 1.10.1 and up)
### Why is this migration required?
Currently, we have an in memory cache for realization of transitive dependencies for a particular version. Other resources use this to retrieve the transitive dependencies and it makes the process performant.
Sometimes the cache gets corrupted leading to inconsistencies in the result. It also gets recreated every time we start the server.
The solution here is storing the transitive dependencies in the store itself. This would enhance the process of retrieving transitive dependencies by ensuring that there is always a deterministic result.
### Steps to follow:
1. Use the API /migrations/calculateDependenciesForVersions/all, to populate a new collection "versionsTemp".
2. Use the API /migrations/addTransitiveDependenciesToVersionData, to update the "versions" collection with "versionsTemp" collection delta, which in this case is transitive dependencies.
3. After verifying everything works, dop the collection "versionsTemp", using API /collections/{id}
PS: The first step ensures that we are not corrupting the old data in cases on rollback.

## 3. Deletion of versioned entities from entities collection (for version 1.20.0 and up)
### Why is this deletion required?
Versioned Entities are entities stored with the versioned flag being true. They have extra information of versioning in their path.
We want to segregate the versioned entities from the entities collection and have separate implementations for both.
The driver for this change is to increase the performance of querying entities whether it be versioned or not versioned.
### Steps to follow:
1. Use the API /migrations/migrations/deleteVersionedEntities, this will delete all versioned entities from the "entities" collection.

## 4. Storing entity's content as string (for version 1.22.0 and up)
### Why is this stringification required for entity's content?
Currently, with Mongo implementation there is limit on how much nesting of an object we can store. Entities sometimes tend to be huge nested objects. Due to Mongo's constraint we can't store such entities and hence it creates a bottleneck in what we can and can't store.
In order to store such entities and also make the storage  of entities scalable to other databases, we are going to be storing the content as string.
With this change we have also introduced three kinds of entity storage which will further help the cause of scalability.
### Steps to follow:
1. Use the API /migrations/migrateToStoredEntityData, this will update the entities collection to be identified as one form of entity data.
2. Once done, using the /indexes (create indexes if absent) you can create the index for this collection.
3. Delete the old indexes of the entities collection.
