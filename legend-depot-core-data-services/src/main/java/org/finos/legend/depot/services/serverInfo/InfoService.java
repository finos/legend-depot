//  Copyright 2023 Goldman Sachs
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

package org.finos.legend.depot.services.serverInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.legend.depot.domain.DatesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class InfoService 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoService.class);
    private static final String PLATFORM_VERSION_FILE = "version.json";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final long MEGABYTE = 1024L * 1024L;
    
    private ServerInfo serverInfo;

    @Inject
    public InfoService()
    {
        String hostName = tryGetValue(InfoService::getLocalHostName);
        InfoService.PlatformVersionInfo platformVersionInfo = tryGetValue(InfoService::getPlatformVersionInfo);
        this.serverInfo = new InfoService.ServerInfo(hostName, platformVersionInfo);
    }

    private static <T> T tryGetValue(InfoService.SupplierWithException<T> supplier)
    {
        try
        {
            return supplier.get();
        }
        catch (Exception var2)
        {
            LOGGER.warn("Error getting info property", var2);
            return null;
        }
    }

    private static String getLocalHostName() throws UnknownHostException
    {
        return InetAddress.getLocalHost().getHostName();
    }

    private static InfoService.PlatformVersionInfo getPlatformVersionInfo() throws IOException
    {
        URL url = InfoService.class.getClassLoader().getResource(PLATFORM_VERSION_FILE);
        return url == null ? null : JSON.readValue(url, PlatformVersionInfo.class);
    }

    private interface SupplierWithException<T>
    {
        T get() throws Exception;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PlatformVersionInfo
    {

        @JsonProperty("git.build.version")
        public String version;
        @JsonProperty("git.build.time")
        public String buildTime;
        @JsonProperty("git.commit.id")
        public String gitRevision;

        private PlatformVersionInfo()
        {
        }
    }

    public static class ServerPlatformInfo
    {
        private final String version;
        private final String buildTime;
        private final String buildRevision;

        private ServerPlatformInfo(String version, String buildTime, String buildRevision)
        {
            this.version = version;
            this.buildTime = buildTime;
            this.buildRevision = buildRevision;
        }

        private ServerPlatformInfo()
        {
            this(null, null, null);
        }

        public String getVersion()
        {
            return this.version;
        }

        public String getBuildTime()
        {
            return this.buildTime;
        }

        public String getBuildRevision()
        {
            return this.buildRevision;
        }
    }

    public class ServerInfo
    {
        private final String hostName;
        private final long totalMemory;
        private final long maxMemory;
        private final long usedMemory;
        private final String serverTimeZone;
        private final InfoService.ServerPlatformInfo platform;

        private ServerInfo(String hostName, InfoService.PlatformVersionInfo platformVersionInfo)
        {
            this.hostName = hostName;
            this.platform = platformVersionInfo == null ? new InfoService.ServerPlatformInfo() : new InfoService.ServerPlatformInfo(platformVersionInfo.version, platformVersionInfo.buildTime, platformVersionInfo.gitRevision);
            this.totalMemory = Runtime.getRuntime().totalMemory() / MEGABYTE;
            this.maxMemory = Runtime.getRuntime().maxMemory() / MEGABYTE;
            this.usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MEGABYTE;
            this.serverTimeZone = DatesHandler.ZONE_ID.getId();
        }

        public String getHostName()
        {
            return this.hostName;
        }

        public long getMaxMemory()
        {
            return maxMemory;
        }

        public long getUsedMemory()
        {
            return usedMemory;
        }

        public long getTotalMemory()
        {
            return totalMemory;
        }

        public InfoService.ServerPlatformInfo getPlatform()
        {
            return this.platform;
        }

        public String getServerTimeZone()
        {
            return serverTimeZone;
        }
    }

    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }
}
