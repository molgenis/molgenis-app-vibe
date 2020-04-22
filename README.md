# molgenis-app-vibe
User interface for [VIBE](https://github.com/molgenis/vibe): a pipeline-friendly software tool for
genome diagnostics to prioritize genes by matching patient symptoms to literature.

## For developers

### Create local test environment
1. Follow the instructions as described on <a href="https://molgenis.gitbooks.io/molgenis/content/v/8.1/guide-development.html#setting-your-molgenis-server-properties">Developing MOLGENIS</a> and <a href="https://molgenis.gitbooks.io/molgenis/content/v/8.1/guide-using-an-ide.html">Using an IDE (Intellij)</a> (select Java 11 JDK & language level 11 instead).
2. Extract the content of <a href="https://molgenis.org/downloads/vibe/vibe-3.1.0-tdb.tar.gz">vibe-3.1.0-tdb.tar.gz</a> to `<MOLGENIS_HOME>/data/vibe`.
3. Launch the application.
4. Configure the homepage:
    1. Sign in as superuser.
    2. Navigate to the data explorer.
    3. Select 'Static content' in the entity type dropdown.
    4. Edit the row with id 'home'.
    5. Copy the content of <a href="./molgenis-app-vibe/src/test/resources/vibe.html">this file</a> into the Content field.
    6. Click on the save button.

### Build new version for deployment.
1. `cd /path/to/vibe`
2. `mvn clean install`
3. `mvn install:install-file -Dfile=/path/to/vibe/app/target/vibe-with-dependencies-<version>.jar -DgroupId=org.molgenis -DartifactId=vibe -Dversion=<version> -Dpackaging=jar`
4. `cp -r /path/to/.m2/repository/org/molgenis/vibe/<version> /path/to/molgenis-app-vibe/molgenis-app-vibe/local-maven-repo/org/molgenis/vibe`
5. `rm /path/to/molgenis-app-vibe/molgenis-app-vibe/local-maven-repo/org/molgenis/vibe/<version>/_remote.repositories`
6. `cd /path/to/molgenis-app-vibe`
7. Edit `molgenis-app-vibe/pom.xml` to use new vibe-version as dependency (and if a new TDB is required, adjust this as well).
8. `mvn clean install`