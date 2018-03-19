# dsm-maven-plugin [![Coverage Status](https://coveralls.io/repos/sevntu-checkstyle/dsm-maven-plugin/badge.png)](https://coveralls.io/r/sevntu-checkstyle/dsm-maven-plugin) [![Travis](https://secure.travis-ci.org/sevntu-checkstyle/dsm-maven-plugin.png)](http://travis-ci.org/sevntu-checkstyle/dsm-maven-plugin)
[![][mavenbadge img]][mavenbadge]


Maven plugin to create HTML report to show dependecies in [DSM view](http://en.wikipedia.org/wiki/Design_structure_matrix).

We use [Dtangler](https://github.com/sysart/dtangler) library to generate DSM matrix.

Example of report for [Checkstyle](http://checkstyle.sourceforge.net/project-reports.html) project: http://checkstyle.sourceforge.net/dsm/index.html

![dsm example for packages](https://cloud.githubusercontent.com/assets/812984/2748676/54d3af4c-c7cf-11e3-8c8b-0dc93617e8b8.png "dsm maven plugin report for package example")
![dsm example inside package](https://cloud.githubusercontent.com/assets/812984/14018891/48d199ea-f18d-11e5-9504-7e5b9f20a7f4.png "dsm maven plugin report inside package example")

### How to use plugin:

1) Edit your pom.xml like this:
```
    <project>
        ...
        <reporting>
            <plugins>
                <plugin>
                    <groupId>com.github.sevntu-checkstyle</groupId>
                    <artifactId>dsm-maven-plugin</artifactId>
                    <version>2.2.0</version>
                </plugin>
                <!--  other reporting plugins  -->
            </plugins>
        </reporting>
        ...
    </project>
```

2) Then execute following commands:
```
    mvn clean install site
```
Instead of install you can use compile, package or other stage, that generates class files.

DSM site part will be placed in target/site/dsm directory

3) You can run only this plugin instead of all site plugins:
```
    mvn compile com.github.sevntu-checkstyle:dsm-maven-plugin:dsm
```

4) Also you can use option obfuscatePackageNames, that truncates package names to more short form
(com.mysite.oneproject.somemodule.package -> c.m.o.somemodule.package for example). It is switched off by default.

There are two way to use it:

a) Edit yours pom.xml and add configuration section:
```
     <project>
            ...
            <reporting>
                <plugins>
                    <plugin>
                        <groupId>com.github.sevntu-checkstyle</groupId>
                        <artifactId>dsm-maven-plugin</artifactId>
                        <version>2.2.0version>
                        <configuration>
                            <obfuscatePackageNames>true</obfuscatePackageNames>
                        </configuration>
                    </plugin>
                    <!--  other reporting plugins  -->
                </plugins>
            </reporting>
            ...
        </project>
```

b) Run maven with -DobfuscatePackageNames=true (false)

```
         mvn compile com.github.sevntu-checkstyle:dsm-maven-plugin:dsm -DobfuscatePackageNames=true
```

If you don't use it and have long package name they will be just cutted
(com.mysite.oneproject.somemodule.package -> ..project.somemodule.package)

Enjoy :)


Related projects: [Eclipse Design Structure Matrix plugin](https://github.com/EclipseDSM/Eclipse-DSM-viewer)

[mavenbadge]:http://search.maven.org/#search|gav|1|g%3A%22com.github.sevntu-checkstyle%22%20AND%20a%3A%22dsm-maven-plugin%22
[mavenbadge img]:https://maven-badges.herokuapp.com/maven-central/com.github.sevntu-checkstyle/dsm-maven-plugin/badge.svg
