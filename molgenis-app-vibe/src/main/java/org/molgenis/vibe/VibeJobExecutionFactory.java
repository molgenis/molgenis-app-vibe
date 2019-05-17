package org.molgenis.vibe;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.data.populate.EntityPopulator;
import org.springframework.stereotype.Component;

@Component
public class VibeJobExecutionFactory
    extends AbstractSystemEntityFactory<VibeJobExecution, VibeJobExecutionMetadata, String> {
  VibeJobExecutionFactory(
      VibeJobExecutionMetadata vibeJobExecutionMetadata, EntityPopulator entityPopulator) {
    super(VibeJobExecution.class, vibeJobExecutionMetadata, entityPopulator);
  }
}
