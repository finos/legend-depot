//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.tracing.resources;

public class ResourceLoggingAndTracing
{
    public static final String GET_ALL_PROJECTS = "get all projects";
    public static final String GET_PROJECT_BY_ID = "get project by id";
    public static final String GET_PROJECT_BY_GA = "get project by ga";
    public static final String DELETE_PROJECT = "delete project";
    public static final String DELETE_PROJECT_ID = "delete projects by id";
    public static final String CREATE_EMPTY_PROJECT = "create empty project";
    public static final String GET_PROJECT_DEPENDENCIES = "get upstream project dependencies";
    public static final String GET_PROJECT_DEPENDENCY_TREE = "get project dependency tree";
    public static final String GET_DEPENDANT_PROJECTS = "downstream projects";
    public static final String GET_REVISION_ENTITIES = "get revision entities";
    public static final String GET_REVISION_ENTITIES_AS_PMCD = "get revision entities as PMCD";
    public static final String GET_REVISION_ENTITY = "get revision entity";
    public static final String GET_REVISION_ENTITIES_BY_PACKAGE = "get revision entities by package";
    public static final String UPDATE_ALL_MASTER_REVISIONS = "refresh all master revisions";
    public static final String UPDATE_LATEST_PROJECT_REVISION = "refresh project master revision";
    public static final String GET_VERSION_ENTITIES = "get version entities";
    public static final String GET_VERSION_DEPENDENCY_ENTITIES = "get version dependencies entities";
    public static final String GET_REVISION_DEPENDENCY_ENTITIES = "get latest dependencies entities";
    public static final String GET_VERSION_STORE_ENTITIES = "get stored version entities";
    public static final String DELETE_STORE_ENTITIES = "delete entities";
    public static final String GET_VERSION_ENTITIES_AS_PMCD = "get version entities as PMCD";
    public static final String GET_VERSION_ENTITY = "get version entity";
    public static final String GET_VERSION_ENTITIES_BY_PACKAGE = "get version entities by package";
    public static final String GET_VERSIONS = "get versions";
    public static final String UPDATE_ALL_VERSIONS = "refresh all versions";
    public static final String UPDATE_VERSION = "refresh version";
    public static final String UPDATE_ALL_PROJECT_VERSIONS = "refresh all project versions";
    public static final String PURGE_ALL_VERSIONS = "purge old versions";
    public static final String DELETE_VERSION = "delete project version";
    public static final String GET_REVISION_FILE_GENERATION_ENTITIES = "get revision generation entities";
    public static final String GET_VERSION_FILE_GENERATION_ENTITIES = "get version generation entities";
    public static final String GET_REVISION_FILE_GENERATION = "get revision file generations";
    public static final String GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH = "get revision file generations by element path";
    public static final String GET_REVISION_FILE_GENERATION_BY_FILEPATH = "get revision file generations by file";
    public static final String GET_VERSION_FILE_GENERATION = "get version file generations";
    public static final String GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH = "get version file generations by element path";
    public static final String GET_VERSION_FILE_GENERATION_BY_FILEPATH = "get version file generations by file";
    public static final String GET_ALL_EVENTS_IN_QUEUE = "get all queue events";
    public static final String FIND_PAST_EVENTS = "find past events";
    public static final String ENQUEUE_EVENT = "enqueue event";
    public static final String HANDLE_EVENTS_IN_QUEUE = "handle queue events";
    public static final String STORE_STATUS = "get store status";
    public static final String TOGGLE_STORE_STATUS = "toggle store status";
    public static final String SCHEDULES_STATUS = "get schedule status";
    public static final String TRIGGER_SCHEDULE = "trigger schedule";
    public static final String GET_CACHE_STATUS = "cache status";
    public static final String GET_PROJECT_CACHE_STATUS = "project cache status";
    public static final String GET_VERSIONS_BY_LAST_USED = "versions last used";
    public static final String TOGGLE_SCHEDULE = "toggle schedule";
    public static final String ENQUEUE_REFRESH_ALL_EVENT = "queue refresh all";
    public static final String ORPHAN_STORE_ENTITIES = "get orphaned entities";
    public static final String GET_ENTITIES_BY_CLASSIFIER_PATH = "get entities by classifier path";
    public static final String REPOSITORY_PROJECT_VERSIONS = "repo versions";
    public static final String GET_PROJECT_CACHE_MISMATCHES = "version mismatch";
    public static final String UPDATE_SCHEDULE = "update schedule";
    public static final String FIX_PROJECT_CACHE_MISMATCHES = "fix version mismatch";


    private ResourceLoggingAndTracing()
    {
    }

}
