# Utility for generating database documentation

## Config
You should change `application.yaml` config params
  
**[datasource]**: `<db_name>`, `<schema_name>`, `<username>`, `<passsword>`
  
**[serializer]**: `<path>` - path to folder where you want to get your result files

## Build jar
Execute `./mvnw package -Dmaven.test.skip=true` terminal command in repository root folder
  
  
<**[only for macOS]**: you can find the built `db-parser-0.0.1.jar`  in the `examples` folder 

## Run
Execute `java -jar db-parser-0.0.1.jar` command near the `.jar` file

## Result
`<db_name>-<schema_name>-meta.json` with `json` metadata
  
`<db_name>-<schema_name>-meta.txt` with `uml` metadata
  
`<db_name>-<schema_name>-meta.png` with `png` diagram
