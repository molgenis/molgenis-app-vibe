package org.molgenis.vibe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.molgenis.vibe.core.formats.GeneDiseaseCollection;
import org.molgenis.vibe.core.formats.GeneDiseaseCollectionDeserializer;
import org.molgenis.vibe.core.formats.GeneDiseaseCollectionSerializer;

/**
 * @deprecated Use {@link
 *     org.molgenis.vibe.core.formats.serialization.json.gene_disease_collection.GeneDiseaseCollectionJsonConverter}
 *     instead.
 */
@Deprecated
public abstract class VibeSerializer {
  private static final Gson gson;

  static {
    GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    gsonBuilder.registerTypeAdapter(
        GeneDiseaseCollection.class, new GeneDiseaseCollectionSerializer());
    gsonBuilder.registerTypeAdapter(
        GeneDiseaseCollection.class, new GeneDiseaseCollectionDeserializer());
    gson = gsonBuilder.create();
  }

  private VibeSerializer() {}

  public static String serializeGeneDiseaseCollection(GeneDiseaseCollection geneDiseaseCollection) {
    return gson.toJson(geneDiseaseCollection);
  }

  public static GeneDiseaseCollection deserializeGeneDiseaseCollection(
      JsonReader geneDiseaseCollectionJson) {
    return gson.fromJson(geneDiseaseCollectionJson, GeneDiseaseCollection.class);
  }
}
