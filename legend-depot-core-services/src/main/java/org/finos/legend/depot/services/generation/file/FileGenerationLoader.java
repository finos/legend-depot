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

package org.finos.legend.depot.services.generation.file;

import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileGenerationLoader implements AutoCloseable
{

    public static final String META_INF = "/META-INF/";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileGenerationLoader.class);
    private final List<GenerationFileSearch> searchList;

    private FileGenerationLoader(List<GenerationFileSearch> searchList)
    {
        this.searchList = searchList;
    }


    public static FileGenerationLoader newFileGenerationsLoader(Path path)
    {
        FileGenerationLoader.GenerationFileSearch search = newPathEntityFileSearch(path);
        return new FileGenerationLoader(search == null ? Collections.emptyList() : Collections.singletonList(search));
    }

    public static FileGenerationLoader newFileGenerationsLoader(File path)
    {
        return newFileGenerationsLoader(path.toPath());
    }

    private static FileGeneration readGeneration(Path path)
    {
        try (InputStream stream = Files.newInputStream(path))
        {
            FileGeneration generation;
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
            {
                String content = new BufferedReader(reader).lines().collect(Collectors.joining(System.lineSeparator()));
                generation = new FileGeneration(path.toString(), content);
            }
            return generation;
        }
        catch (IOException var6)
        {
            LOGGER.error("Error reading generation from file: " + path, var6);
            return null;
        }
    }

    private static boolean isPossiblyGenerationFile(Path path)
    {
        return path != null && !path.startsWith(META_INF)
                && Files.isRegularFile(path);
    }


    private static GenerationFileSearch newPathEntityFileSearch(Path path)
    {
        try
        {
            return getGenerationFileSearch(path);
        }
        catch (Exception var5)
        {
            StringBuilder builder = (new StringBuilder("Error handling ")).append(path);
            String eMessage = var5.getMessage();
            if (eMessage != null)
            {
                builder.append(": ").append(eMessage);
            }

            throw new RuntimeException(builder.toString(), var5);
        }
    }

    private static GenerationFileSearch getGenerationFileSearch(Path path) throws IOException
    {
        BasicFileAttributes attributes;
        try
        {
            attributes = Files.readAttributes(path, BasicFileAttributes.class);
        }
        catch (NoSuchFileException var4)
        {
            return null;
        }

        if (attributes.isDirectory())
        {
            return new DirectoryEntityFileSearch(path);
        }
        else
        {
            FileSystem fs = FileSystems.newFileSystem(path, FileGenerationLoader.class.getClassLoader());

            return new DirectoryEntityFileSearchWithCloseable(fs.getPath(fs.getSeparator()), fs);

        }
    }

    public Stream<FileGeneration> getAllFileGenerations()
    {
        try
        {
            return this.getGenerationsInDirectory("");
        }
        catch (Exception var4)
        {
            throw new RuntimeException(var4.getMessage());
        }
    }

    private Stream<FileGeneration> getGenerationsInDirectory(String directoryPath)
    {
        return this.searchList.stream().flatMap(s -> s.getPathsInDirectory(directoryPath)).filter(FileGenerationLoader::isPossiblyGenerationFile).map(FileGenerationLoader::readGeneration).filter(Objects::nonNull);
    }

    public synchronized void close() throws Exception
    {
        Exception exception = null;

        for (GenerationFileSearch generationFileSearch : this.searchList)
        {
            AutoCloseable closeable = generationFileSearch;
            try
            {
                closeable.close();
            }
            catch (Exception var7)
            {
                if (exception == null)
                {
                    exception = var7;
                }
                else
                {
                    exception.addSuppressed(var7);
                }
            }
        }
        if (exception != null)
        {
            throw exception;
        }
    }

    private interface GenerationFileSearch extends AutoCloseable
    {
        Path getPath(String var1);

        Stream<Path> getPathsInDirectory(String var1);
    }

    private static class DirectoryEntityFileSearchWithCloseable extends FileGenerationLoader.DirectoryEntityFileSearch
    {
        private final AutoCloseable closeable;

        private DirectoryEntityFileSearchWithCloseable(Path directory, AutoCloseable closeable)
        {
            super(directory);
            this.closeable = closeable;
        }

        @Override
        public void close() throws Exception
        {
            this.closeable.close();
        }
    }

    private static class DirectoryEntityFileSearch implements GenerationFileSearch
    {
        private final Path directory;

        private DirectoryEntityFileSearch(Path directory)
        {
            this.directory = directory;
        }

        private static Stream<Path> getDirectoryStream(Path dirPath)
        {
            try
            {
                return Files.walk(dirPath, FileVisitOption.FOLLOW_LINKS);
            }
            catch (IOException var2)
            {
                return Stream.empty();
            }
        }

        public Path getPath(String filePath)
        {
            return this.directory.resolve(filePath);
        }

        public Stream<Path> getPathsInDirectory(String dirPath)
        {
            Path resolvedPath = this.directory.resolve(dirPath);
            return Files.isDirectory(resolvedPath) ? getDirectoryStream(resolvedPath) : Stream.empty();
        }

        public void close() throws Exception
        {
        }
    }
}
