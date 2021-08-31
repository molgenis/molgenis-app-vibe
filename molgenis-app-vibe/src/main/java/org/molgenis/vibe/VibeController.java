package org.molgenis.vibe;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vibe.VibeJobExecutionMetadata.VIBE_JOB_EXECUTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.molgenis.data.DataService;
import org.molgenis.data.UnknownEntityException;
import org.molgenis.data.file.FileStore;
import org.molgenis.jobs.JobExecutor;
import org.molgenis.jobs.model.JobExecution;
import org.molgenis.jobs.model.JobExecution.Status;
import org.molgenis.vibe.core.formats.Gene;
import org.molgenis.vibe.core.formats.GeneDiseaseCollection;
import org.molgenis.vibe.core.formats.serialization.json.gene_disease_collection.GeneDiseaseCollectionJsonConverter;
import org.molgenis.vibe.core.query_output_digestion.prioritization.gene.GenePrioritizer;
import org.molgenis.vibe.core.query_output_digestion.prioritization.gene.HighestSingleDisgenetScoreGenePrioritizer;
import org.molgenis.vibe.response.GeneDiseaseCollectionResponse;
import org.molgenis.vibe.response.GeneDiseaseCollectionResponseMapper;
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
  public GeneDiseaseCollectionResponse previewVibeResult(
      @RequestParam("id") String vibeJobExecutionId) throws IOException {
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

    // Retrieves GeneDiseaseCollection from json file.
    GeneDiseaseCollection collection =
        GeneDiseaseCollectionJsonConverter.readJsonStream(new FileInputStream(file));

    // Retrieves priority.
    GenePrioritizer prioritizer = new HighestSingleDisgenetScoreGenePrioritizer();
    List<Gene> genePriority = prioritizer.sort(collection);

    // Generates subset.
    GeneDiseaseCollection geneDiseaseCombinationOutput = new GeneDiseaseCollection();

    int outputLimit = 10;
    if (genePriority.size() < outputLimit) {
      outputLimit = genePriority.size();
    }
    for (int i = 0; i < outputLimit; i++) {
      geneDiseaseCombinationOutput.addAll(collection.getByGene(genePriority.get(i)));
    }

    // Returns subset.
    return GeneDiseaseCollectionResponseMapper.mapToResponse(geneDiseaseCombinationOutput);
  }
}
