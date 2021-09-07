package org.molgenis.vibe;

import static java.util.Objects.requireNonNull;
import static org.molgenis.core.ui.file.FileDownloadController.URI;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.util.Set;
import org.molgenis.data.DataService;
import org.molgenis.data.file.FileStore;
import org.molgenis.data.file.model.FileMeta;
import org.molgenis.data.file.model.FileMetaFactory;
import org.molgenis.data.file.model.FileMetaMetadata;
import org.molgenis.jobs.Progress;
import org.molgenis.vibe.core.database_processing.GenesForPhenotypeRetriever;
import org.molgenis.vibe.core.formats.Phenotype;
import org.molgenis.vibe.core.formats.serialization.json.gene_disease_collection.GeneDiseaseCollectionJsonConverter;
import org.molgenis.vibe.core.io.input.ModelReader;
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
      ModelReader reader, Set<Phenotype> phenotypes, String filename, Progress progress)
      throws IOException {
    GenesForPhenotypeRetriever retriever = new GenesForPhenotypeRetriever(reader, phenotypes);
    retriever.run();

    FileMeta fileMeta;
    try (PipedInputStream in = new PipedInputStream()) {
      try (PipedOutputStream out = new PipedOutputStream(in)) {
        new Thread(
                () -> {
                  try {
                    GeneDiseaseCollectionJsonConverter.writeJsonStream(
                        out, retriever.getGeneDiseaseCollection());
                  } catch (IOException e) {
                    throw new UncheckedIOException(e);
                  }
                })
            .start();

        File file = fileStore.store(in, filename);
        fileMeta = createFileMeta(file);
        dataService.add(FileMetaMetadata.FILE_META, fileMeta);
      }
    }

    progress.increment(1);
    progress.status("Done.");
    return fileMeta;
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
