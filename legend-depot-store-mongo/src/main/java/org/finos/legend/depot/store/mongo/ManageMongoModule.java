package org.finos.legend.depot.store.mongo;

import com.google.inject.PrivateModule;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;

public class ManageMongoModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(UpdateEntities.class).to(EntitiesMongo.class);
        bind(UpdateProjects.class).to(ProjectsMongo.class);
        bind(UpdateFileGenerations.class).to(FileGenerationsMongo.class);
    }
}
