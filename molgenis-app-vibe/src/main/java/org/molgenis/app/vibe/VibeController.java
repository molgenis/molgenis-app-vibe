package org.molgenis.app.vibe;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.molgenis.vibe.VibeApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vibe")
public class VibeController {

  @GetMapping
  public String test(@RequestParam("p") List<String> phenotypes) throws IOException {
    File tempOutputFile = File.createTempFile("vibe", ".tsv");
    String outputPath = tempOutputFile.getPath();
    tempOutputFile.delete();

    try {
      List<String> argsList = new ArrayList<>();
      argsList.add("-v");
      argsList.add("-t");
      argsList.add("C:\\Users\\Dennis\\Downloads\\disgenetv5.0-rdf-v5.0.0-dump-TDB\\TDB");
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
      return Files.asCharSource(tempOutputFile, StandardCharsets.UTF_8).read();
    } finally {
      tempOutputFile.delete();
    }
  }
}
