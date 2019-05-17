package org.molgenis.vibe;

import java.util.Arrays;
import java.util.List;
import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.jobs.model.JobExecution;

public class VibeJobExecution extends JobExecution {
  @SuppressWarnings("unused")
  public VibeJobExecution(Entity entity) {
    super(entity);
    setType(VibeJobExecutionMetadata.VIBE_JOB_TYPE);
  }

  @SuppressWarnings("unused")
  public VibeJobExecution(EntityType entityType) {
    super(entityType);
    setType(VibeJobExecutionMetadata.VIBE_JOB_TYPE);
  }

  @SuppressWarnings("unused")
  public VibeJobExecution(String identifier, EntityType entityType) {
    super(identifier, entityType);
    setType(VibeJobExecutionMetadata.VIBE_JOB_TYPE);
  }

  public void setPhenotypes(List<String> phenotypes) {
    set(VibeJobExecutionMetadata.PHENOTYPES, String.join(",", phenotypes));
  }

  public List<String> getPhenotypes() {
    return Arrays.asList(getString(VibeJobExecutionMetadata.PHENOTYPES).split(","));
  }
}
