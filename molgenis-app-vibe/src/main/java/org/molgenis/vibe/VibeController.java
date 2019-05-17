package org.molgenis.vibe;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vibe.VibeJobExecutionMetadata.VIBE_JOB_EXECUTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.molgenis.data.DataService;
import org.molgenis.data.UnknownEntityException;
import org.molgenis.data.file.FileStore;
import org.molgenis.jobs.JobExecutor;
import org.molgenis.jobs.model.JobExecution;
import org.molgenis.jobs.model.JobExecution.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vibe")
class VibeController {
  private final JobExecutor jobExecutor;
  private final VibeJobExecutionFactory vibeJobExecutionFactory;
  private final DataService dataService;
  private final FileStore fileStore;

  VibeController(
      JobExecutor jobExecutor,
      VibeJobExecutionFactory vibeJobExecutionFactory,
      DataService dataService,
      FileStore fileStore) {
    this.jobExecutor = requireNonNull(jobExecutor);
    this.vibeJobExecutionFactory = requireNonNull(vibeJobExecutionFactory);
    this.dataService = requireNonNull(dataService);
    this.fileStore = requireNonNull(fileStore);
  }

  @PostMapping
  @ResponseBody
  public JobExecution executeVibe(@RequestParam("p") List<String> phenotypes) {
    VibeJobExecution vibeJobExecution = vibeJobExecutionFactory.create();
    vibeJobExecution.setPhenotypes(phenotypes);
    jobExecutor.submit(vibeJobExecution);
    return vibeJobExecution;
  }

  @GetMapping
  @ResponseBody
  public List<String> previewVibeResult(@RequestParam("id") String vibeJobExecutionId)
      throws IOException {
    VibeJobExecution vibeJobExecution =
        dataService.findOneById(VIBE_JOB_EXECUTION, vibeJobExecutionId, VibeJobExecution.class);
    if (vibeJobExecution == null) {
      throw new UnknownEntityException(VIBE_JOB_EXECUTION, vibeJobExecutionId);
    }
    if (vibeJobExecution.getStatus() != Status.SUCCESS) {
      throw new RuntimeException("No preview available");
    }
    String resultUrl = vibeJobExecution.getResultUrl();
    if (resultUrl == null) {
      throw new RuntimeException("No preview available");
    }
    String fileId = resultUrl.substring("/files/".length());
    File file = fileStore.getFile(fileId);
    List<String> lines;
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

      lines = new ArrayList<>();
      for (int i = 0; i <= 10; ++i) {
        String line = bufferedReader.readLine();
        if (line == null) {
          break;
        }
        lines.add(line);
      }
    }
    return lines;
  }
}
