package org.molgenis.vibe;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.meta.AttributeType.TEXT;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;
import static org.molgenis.jobs.model.JobPackage.PACKAGE_JOB;

import org.molgenis.data.meta.SystemEntityType;
import org.molgenis.jobs.model.JobExecutionMetaData;
import org.molgenis.jobs.model.JobPackage;
import org.springframework.stereotype.Component;

@Component
public class VibeJobExecutionMetadata extends SystemEntityType {
  private static final String SIMPLE_NAME = "VibeJobExecution";
  static final String PHENOTYPES = "phenotypes";

  public static final String VIBE_JOB_EXECUTION = PACKAGE_JOB + PACKAGE_SEPARATOR + SIMPLE_NAME;

  private final JobExecutionMetaData jobExecutionMetaData;
  private final JobPackage jobPackage;

  static final String VIBE_JOB_TYPE = "VibeJob";

  VibeJobExecutionMetadata(JobExecutionMetaData jobExecutionMetaData, JobPackage jobPackage) {
    super(SIMPLE_NAME, PACKAGE_JOB);
    this.jobExecutionMetaData = requireNonNull(jobExecutionMetaData);
    this.jobPackage = requireNonNull(jobPackage);
  }

  @Override
  public void init() {
    setLabel("Vibe job execution");
    setExtends(jobExecutionMetaData);
    setPackage(jobPackage);
    addAttribute(PHENOTYPES)
        .setLabel("Phenotypes")
        .setDataType(TEXT)
        .setDescription("Comma-separated list of phenotypes.")
        .setNillable(false);
  }
}
