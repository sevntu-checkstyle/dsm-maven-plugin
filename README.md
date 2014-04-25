# dsm-maven-plugin [![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/sevntu-checkstyle/dsm-maven-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge") [![Coverage Status](https://coveralls.io/repos/sevntu-checkstyle/dsm-maven-plugin/badge.png)](https://coveralls.io/r/sevntu-checkstyle/dsm-maven-plugin) [![Travis](https://secure.travis-ci.org/sevntu-checkstyle/dsm-maven-plugin.png)](http://travis-ci.org/sevntu-checkstyle/dsm-maven-plugin)


Maven plugin to create HTML report to show dependecies in [DSM view](http://en.wikipedia.org/wiki/Design_structure_matrix).

We use [Dtangler](https://github.com/sysart/dtangler) library to generate DSM matrix.

Example of report for [Checkstyle](http://checkstyle.sourceforge.net/project-reports.html) project: http://checkstyle.sourceforge.net/dsm/index.html


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
                    <version>2.1.1</version>
                </plugin>
                <!--  other reportin plugins  -->
            </plugins>
        </reporting>
        ...
    </project>
```

2) Then execute following commands:
```    
    mvn clean install site
```
Instead of install you can use copmile, package or other stage, that generates class files.

DSM site part will be placed in target/site/dsm directory

3) You can run only this plugin instead of all site plugins:
```
    mvn com.github.sevntu-checkstyle:dsm-maven-plugin:dsm
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
                        <version>2.1.1</version>
                        <configurations>
                            <obfuscatePackageNames>true</obfuscatePackageNames>
                        </configurations>
                    </plugin>
                    <!--  other reportin plugins  -->
                </plugins>
            </reporting>
            ...
        </project>
```

b) Run maven with -DobfuscatePackageNames=true (false)
    
```
         mvn com.github.sevntu-checkstyle:dsm-maven-plugin:dsm -DobfuscatePackageNames=true
```

If you don't use it and have long package name they will be just cutted
(com.mysite.oneproject.somemodule.package -> ..project.somemodule.package)

Enjoy :)
