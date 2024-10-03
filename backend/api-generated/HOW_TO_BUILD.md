For build purpose add this plugin to pom.xml

`<plugin>
<groupId>org.openapitools</groupId>
<artifactId>openapi-generator-maven-plugin</artifactId>
<version>6.0.1</version>
<executions>
<execution>
<goals>
<goal>generate</goal>
</goals>
<configuration>
<inputSpec>${project.basedir}/../api-definition/api.yaml</inputSpec>
<output>${project.basedir}</output>
<generatorName>spring</generatorName>
<apiPackage>com.example.api</apiPackage>
<modelPackage>com.example.model</modelPackage>
<invokerPackage>com.example.invoker</invokerPackage>
<configOptions>
<dateLibrary>java8</dateLibrary>
</configOptions>
</configuration>
</execution>
</executions>
</plugin>`