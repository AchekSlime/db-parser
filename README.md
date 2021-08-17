#  packages the database structure in JSON

## Build jar
Execute `./mvnw -Dmaven.test.skip=true install` in repository root folder

## Run
`java -jar db-parser-0.0.1.jar --spring.datasource.url=<url> --spring.datasource.username=<username> --spring.datasource.password=<password>`
<br /><br />where you should change `<url>`, `<username>`, `<password>` values to your params without triangular brackets

## Result
`<database name>_structure.json` file will be generated near the `.jar` file
