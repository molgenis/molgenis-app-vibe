# molgenis-app-vibe

User interface for [VIBE][vibe]: a pipeline-friendly software tool for
genome diagnostics to prioritize genes by matching patient symptoms to literature.

## For developers

### Create local test environment

1. Set the memory in your docker resources to 4GB.
2. Open the terminal and go to the `molgenis-app-vibe/molgenis-app-vibe` folder.
3. Run:
    ```bash
    mvn clean install
    ```
4. Run: 
   ```shell
   docker-compose up --force-recreate --build
   ```
5. Start a fresh incognito session/clear browser cache.
6. Go to http://localhost:8081/
7. Sign in as superuser (default: `admin` with password `admin`).
8. Update homepage:
    1. Navigate to the data explorer.
    2. Select 'Static content' in the entity type dropdown.
    3. Edit the row with id 'home'.
    4. Copy the content of [this file](./molgenis-app-vibe/src/test/resources/vibe.html) into the Content field.
    5. Click on the save button.
9. Update settings:
    1. Navigate to the data explorer.
    2. Select 'Application Settings' in the entity type dropdown.
    3. Edit the row with Application Title 'MOLGENIS'.
    4. Under 'CSS href', add:  
    `vibe.css`
    5. Click on the save button.


After changes were made to the code, be sure to start from step 3.

If the layout is missing on certain pages, stop the Docker application, run `docker system prune` and start from step 4.



[vibe]: https://github.com/molgenis/vibe
[molgenis_developing]: https://molgenis.gitbooks.io/molgenis/content/v/8.1/guide-development.html
[molgenis_idea_setup]: https://molgenis.gitbooks.io/molgenis/content/v/8.1/guide-using-an-ide.html
[vibe_database]: https://molgenis.org/downloads/vibe/vibe-5.0.0-hdt.tar.gz
