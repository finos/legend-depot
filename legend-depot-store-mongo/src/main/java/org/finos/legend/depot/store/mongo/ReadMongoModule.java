package org.finos.legend.depot.store.mongo;

import com.google.inject.PrivateModule;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.generation.file.FileGenerations;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;

public class ReadMongoModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(Projects.class).to(ProjectsMongo.class);
        bind(Entities.class).to(EntitiesMongo.class);
        bind(FileGenerations.class).to(FileGenerationsMongo.class);
    }
}
