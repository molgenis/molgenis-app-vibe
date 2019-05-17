package org.molgenis.vibe;

import java.io.IOException;
import java.util.List;
import org.molgenis.data.file.model.FileMeta;
import org.molgenis.jobs.Progress;

interface VibeService {
  FileMeta executeVibe(List<String> phenotypes, String filename, Progress progress)
      throws IOException;
}
