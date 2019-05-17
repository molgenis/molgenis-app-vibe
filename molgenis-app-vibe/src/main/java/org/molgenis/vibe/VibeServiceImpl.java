package org.molgenis.vibe;

import static java.util.Objects.requireNonNull;
import static org.molgenis.core.ui.file.FileDownloadController.URI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.molgenis.data.DataService;
import org.molgenis.data.file.FileStore;
import org.molgenis.data.file.model.FileMeta;
import org.molgenis.data.file.model.FileMetaFactory;
import org.molgenis.data.file.model.FileMetaMetadata;
import org.molgenis.jobs.Progress;
import org.molgenis.util.AppDataRootProvider;
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
  public FileMeta executeVibe(List<String> phenotypes, String filename, Progress progress)
      throws IOException {
    File tempOutputFile = executeVibeCli(phenotypes);
    FileMeta fileMeta;
    try {
      try (InputStream inputStream = new FileInputStream(tempOutputFile)) {
        File file = fileStore.store(inputStream, filename);
        fileMeta = createFileMeta(file);
        dataService.add(FileMetaMetadata.FILE_META, fileMeta);
      }
    } finally {
      tempOutputFile.delete();
    }
    progress.increment(1);
    progress.status("Done.");
    return fileMeta;
  }

  private File executeVibeCli(List<String> phenotypes) throws IOException {
    Path vibeDataPath = Paths.get(AppDataRootProvider.getAppDataRoot().toString(), "data", "vibe");
    File outputFile = File.createTempFile("vibe", ".tsv");
    String outputPath = outputFile.getPath();
    boolean deleteOk = outputFile.delete();
    if (!deleteOk) {
      throw new IOException(String.format("unable to delete '%s'", outputFile.toString()));
    }

    List<String> argsList = new ArrayList<>();
    argsList.add("-v");
    argsList.add("-t");
    argsList.add(vibeDataPath.toString());
    argsList.add("-s");
    argsList.add("gda_max");
    argsList.add("-o");
    argsList.add(outputPath);
    phenotypes.forEach(
        phenotype -> {
          argsList.add("-p");
          argsList.add(phenotype);
        });
    VibeApplication.main(argsList.toArray(new String[0]));

    return outputFile;
  }

  private FileMeta createFileMeta(File file) {
    FileMeta fileMeta = fileMetaFactory.create(file.getName());
    fileMeta.setContentType("text/tab-separated-values");
    fileMeta.setSize(file.length());
    fileMeta.setFilename("vibe-results.tsv");
    fileMeta.setUrl(URI + "/" + file.getName());
    return fileMeta;
  }
}
