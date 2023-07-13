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

package org.finos.legend.depot.core.http.guice;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.hubspot.dropwizard.guicier.DropwizardAwareModule;
import org.finos.legend.depot.core.http.ServersConfiguration;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.StorageConfiguration;
import org.finos.legend.depot.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.tracing.configuration.PrometheusConfiguration;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public abstract class BaseModule<T extends ServersConfiguration> extends DropwizardAwareModule<T>
{

    @Override
    public void configure(Binder binder)
    {
        binder.bind(ProjectsConfiguration.class).toProvider(this::getProjectsConfig);
        binder.bind(StorageConfiguration.class).toProvider(this::getStorageConfig);
        binder.bind(OpenTracingConfiguration.class).toProvider(this::getTracingConfig);
        binder.bind(PrometheusConfiguration.class).toProvider(this::getPrometheusConfig);
    }

    @RequestScoped
    @Provides
    @Named("requestPrincipal")
    public Principal provideUser(HttpServletRequest req)
    {
        return req.getUserPrincipal();
    }


    @Provides
    @Named("applicationName")
    public String getApplicationName(T configuration)
    {
        return configuration.getApplicationName();
    }

    private StorageConfiguration getStorageConfig()
    {
        return getConfiguration().getStorageConfiguration();
    }

    private ProjectsConfiguration getProjectsConfig()
    {
        return getConfiguration().getProjectsConfiguration() != null ? getConfiguration().getProjectsConfiguration() : new ProjectsConfiguration("master");
    }

    private OpenTracingConfiguration getTracingConfig()
    {
        return getConfiguration().getOpenTracingConfiguration() != null ? getConfiguration().getOpenTracingConfiguration() : new OpenTracingConfiguration();
    }

    private PrometheusConfiguration getPrometheusConfig()
    {
        return getConfiguration().getPrometheusConfiguration() != null ? getConfiguration().getPrometheusConfiguration() : new PrometheusConfiguration();
    }

}
