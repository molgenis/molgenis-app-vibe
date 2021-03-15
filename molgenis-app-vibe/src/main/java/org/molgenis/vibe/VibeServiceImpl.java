package org.molgenis.vibe;

import static java.util.Objects.requireNonNull;
import static org.molgenis.core.ui.file.FileDownloadController.URI;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import org.molgenis.data.DataService;
import org.molgenis.data.file.FileStore;
import org.molgenis.data.file.model.FileMeta;
import org.molgenis.data.file.model.FileMetaFactory;
import org.molgenis.data.file.model.FileMetaMetadata;
import org.molgenis.jobs.Progress;
import org.molgenis.util.AppDataRootProvider;
import org.molgenis.vibe.core.GeneDiseaseCollectionRetrievalRunner;
import org.molgenis.vibe.core.formats.GeneDiseaseCollection;
import org.molgenis.vibe.core.formats.Phenotype;
import org.molgenis.vibe.core.io.input.ModelReaderFactory;
import org.molgenis.vibe.core.io.input.VibeDatabase;
import org.springframework.stereotype.Service;

@Service
class VibeServiceImpl implements VibeService {
  private final DataService dataService;
  private final FileStore fileStore;
  private final FileMetaFactory fileMetaFactory;

  VibeServiceImpl(DataService dataService, FileStore fileStore, FileMetaFactory fileMetaFactory) {
    this.dataService = requireNonNull(dataService);
    this.fileStore = requireNonNull(fileStore);
    this.fileMetaFactory = requireNonNull(fileMetaFactory);
  }

  @Override
  public FileMeta retrieveGeneDiseaseCollection(
      Set<Phenotype> phenotypes, String filename, Progress progress) throws IOException {
    GeneDiseaseCollection collection =
        new GeneDiseaseCollectionRetrievalRunner(retrieveDatabase(), phenotypes).call();
    String collectionJson = VibeSerializer.serializeGeneDiseaseCollection(collection);

    FileMeta fileMeta;
    try (InputStream inputStream =
        new ByteArrayInputStream(collectionJson.getBytes(StandardCharsets.UTF_8))) {
      File file = fileStore.store(inputStream, filename);
      fileMeta = createFileMeta(file);
      dataService.add(FileMetaMetadata.FILE_META, fileMeta);
    }
    progress.increment(1);
    progress.status("Done.");
    return fileMeta;
  }

  private VibeDatabase retrieveDatabase() throws IOException {
    InputStream propertiesStream =
        getClass().getClassLoader().getResourceAsStream("molgenis-app-vibe.properties");
    Properties properties = new Properties();
    properties.load(propertiesStream);

    Path databasePath =
        Paths.get(
            AppDataRootProvider.getAppDataRoot().toString(),
            "data",
            properties.getProperty("vibe-hdt.file"));

    return new VibeDatabase(databasePath, ModelReaderFactory.HDT);
  }

  private FileMeta createFileMeta(File file) {
    FileMeta fileMeta = fileMetaFactory.create(file.getName());
    fileMeta.setContentType("application/json");
    fileMeta.setSize(file.length());
    fileMeta.setFilename("vibe-results.json");
    fileMeta.setUrl(URI + "/" + file.getName());
    return fileMeta;
  }
}
