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

package org.finos.legend.depot.core.services.tracing;

public class ResourceLoggingAndTracing
{
    public static final String GET_ALL_PROJECTS = "get all projects";
    public static final String GET_VERSIONS_BY_LASTUPDATE_DATE = "get versions by lastupdate date";
    public static final String GET_PROJECT_CONFIG_BY_GA = "get project configuration by ga";
    public static final String FIND_PROJECT_VERSIONS = "find project versions";
    public static final String GET_PROJECT_VERSION_BY_GAV = "get project version by gav";
    public static final String DELETE_PROJECT = "delete project";
    public static final String CREATE_UPDATE_PROJECT = "create update project";
    public static final String GET_PROJECT_DEPENDENCIES = "get upstream project dependencies";
    public static final String GET_PROJECT_DEPENDENCY_TREE = "get project dependency tree";
    public static final String GET_DEPENDANT_PROJECTS = "get downstream projects";
    public static final String UPDATE_ALL_SNAPSHOTS = "refresh all snapshots";
    public static final String UPDATE_PROJECT_TRANSITIVE_DEPENDENCIES = "update project transitive dependencies";
    public static final String GET_VERSION_ENTITIES = "get version entities";
    public static final String GET_VERSION_DEPENDENCY_ENTITIES = "get version dependencies entities";
    public static final String GET_VERSIONS_DEPENDENCY_ENTITIES = "get versions dependencies entities";
    public static final String GET_VERSION_ENTITIES_AS_PMCD = "get version entities as PMCD";
    public static final String GET_VERSIONS_DEPENDENCY_ENTITIES_AS_PMCD = "get versions dependencies entities as PMCD";
    public static final String GET_VERSION_ENTITY = "get version entity";
    public static final String GET_VERSION_ENTITY_FROM_DEPENDENCIES = "get version entity from dependencies";
    public static final String GET_VERSION_ENTITIES_BY_FILTER = "get version entities by filter";
    public static final String GET_VERSIONS = "get versions";
    public static final String UPDATE_ALL_VERSIONS = "refresh all versions";
    public static final String UPDATE_VERSION = "refresh version";
    public static final String EXCLUDE_PROJECT_VERSION = "exclude project version";
    public static final String UPDATE_ALL_PROJECT_VERSIONS = "refresh all project versions";
    public static final String EVICT_OLD_VERSIONS = "evict old versions";
    public static final String DELETE_VERSION = "delete version";
    public static final String DEPRECATE_VERSION = "deprecate version";
    public static final String EVICT_VERSION = "evict version";
    public static final String EVICT_VERSIONS_NOT_USED = "evict versions not used";
    public static final String GET_VERSION_FILE_GENERATION_ENTITIES = "get version generation entities";
    public static final String GET_VERSION_FILE_GENERATION = "get version file generations";
    public static final String GET_VERSION_FILE_GENERATION_BY_TYPE = "get version file generations by type";
    public static final String GET_VERSION_FILE_GENERATION_BY_ELEMENT_PATH = "get version file generations by element path";
    public static final String GET_VERSION_FILE_GENERATION_BY_FILEPATH = "get version file generations by file";
    public static final String GET_VERSION_FILE_GENERATION_CONTENT = "get version file generation content";
    public static final String GET_ALL_EVENTS_IN_QUEUE = "get all queue events";
    public static final String FIND_PAST_EVENTS = "find past events";
    public static final String ENQUEUE_EVENT = "queue event";
    public static final String HANDLE_EVENTS_IN_QUEUE = "handle queue events";
    public static final String SCHEDULES_STATUS = "get schedule status";
    public static final String SCHEDULES_RUNS = "get schedule runs";
    public static final String TRIGGER_SCHEDULE = "trigger schedule";
    public static final String TOGGLE_SCHEDULE = "toggle schedule";
    public static final String TOGGLE_SCHEDULES = "toggle schedules";
    public static final String GET_ENTITIES_BY_CLASSIFIER_PATH = "get entities by classifier path";
    public static final String REPOSITORY_PROJECT_VERSIONS = "repo project versions";
    public static final String GET_PROJECT_CACHE_MISMATCHES = "version mismatch";
    public static final String FIND_EVENT_BY_ID = "find event";
    public static final String GET_EVENT_IN_QUEUE = "find event in queue";
    public static final String DELETE_SCHEDULE = "delete schedule";
    public static final String DELETE_SCHEDULES = "delete schedules";
    public static final String GET_QUEUE_COUNT = "queue count";


    private ResourceLoggingAndTracing()
    {
    }

}
