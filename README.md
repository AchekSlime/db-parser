# Утилита запаковывает структуру базы данных в JSON

## Build jar
`mvn -Dmaven.test.skip=true install`

## Run
`java -jar parserDB-0.0.1.jar --spring.datasource.url=<ur> --spring.datasource.username=&lt;username&gt; --spring.datasource.password=&lt;password&gt;"`
<br /><br />Where you should change &lt;url&gt;, &lt;username&gt;, &lt;password&gt; to your params without triangular brackets
