# Утилита запаковывает структуру базы данных в JSON

## Build jar
`mvn -Dmaven.test.skip=true install`

## Run
`java -jar db-parser-0.0.1.jar --spring.datasource.url=<url> --spring.datasource.username=<username> --spring.datasource.password=<password>`
<br /><br />Where you should change &lt;url&gt;, &lt;username&gt;, &lt;password&gt; to your params without triangular brackets
