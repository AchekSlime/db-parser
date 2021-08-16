#  packages the database structure in JSON

## build jar
`mvn -Dmaven.test.skip=true install`

## run
`java -jar db-parser-0.0.1.jar --spring.datasource.url=<url> --spring.datasource.username=<username> --spring.datasource.password=<password>`
<br /><br />where you should change &lt;url&gt;, &lt;username&gt;, &lt;password&gt; to your params without triangular brackets
