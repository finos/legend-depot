//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.core.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Entities and PureModelContextData (deeply nested lambdas, whole-project models) have no size we can bound, so
 * depot servers lift every Jackson stream limit, as legend-engine's and legend-sdlc's servers do. legend-engine's
 * own helper is package-private, so depot keeps its own copy.
 * <p>
 * As in legend-engine and legend-sdlc, the limits are lifted only on jackson-core 2.18 or later, the first version with
 * every constraint API used here; on an older Jackson {@link #install} does nothing. The constraint classes are only
 * touched through {@link Limits}, so an older Jackson never loads them.
 */
public final class UnlimitedStreamConstraints
{
    private static final boolean JACKSON_HAS_ALL_STREAM_CONSTRAINTS = isAtLeast(PackageVersion.VERSION, 2, 18);

    private UnlimitedStreamConstraints()
    {
    }

    /**
     * Sets the JVM-wide defaults, which cover every mapper built afterwards. A JsonFactory copies those defaults only
     * when it is built, so a mapper that already exists, such as Dropwizard's bootstrap mapper, gets the limits directly.
     */
    public static void install(ObjectMapper alreadyBuilt)
    {
        if (JACKSON_HAS_ALL_STREAM_CONSTRAINTS)
        {
            Limits.applyAsJvmDefaults();
            Limits.applyTo(alreadyBuilt.getFactory());
        }
    }

    private static boolean isAtLeast(Version version, int major, int minor)
    {
        return (version.getMajorVersion() > major) || ((version.getMajorVersion() == major) && (version.getMinorVersion() >= minor));
    }

    private static final class Limits
    {
        private static final StreamReadConstraints READ = StreamReadConstraints.builder()
                .maxNestingDepth(Integer.MAX_VALUE)
                .maxDocumentLength(-1L)
                .maxTokenCount(-1L)
                .maxNumberLength(Integer.MAX_VALUE)
                .maxStringLength(Integer.MAX_VALUE)
                .maxNameLength(Integer.MAX_VALUE)
                .build();
        private static final StreamWriteConstraints WRITE = StreamWriteConstraints.builder()
                .maxNestingDepth(Integer.MAX_VALUE)
                .build();

        private static void applyTo(JsonFactory factory)
        {
            factory.setStreamReadConstraints(READ);
            factory.setStreamWriteConstraints(WRITE);
        }

        private static void applyAsJvmDefaults()
        {
            StreamReadConstraints.overrideDefaultStreamReadConstraints(READ);
            StreamWriteConstraints.overrideDefaultStreamWriteConstraints(WRITE);
        }
    }
}
