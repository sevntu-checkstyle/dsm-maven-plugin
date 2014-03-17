# dsm-maven-plugin [![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/sevntu-checkstyle/dsm-maven-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge") [![Coverage Status](https://coveralls.io/repos/sevntu-checkstyle/dsm-maven-plugin/badge.png)](https://coveralls.io/r/sevntu-checkstyle/dsm-maven-plugin) [![Travis](https://secure.travis-ci.org/sevntu-checkstyle/dsm-maven-plugin.png)](http://travis-ci.org/sevntu-checkstyle/dsm-maven-plugin)


Maven plugin to create HTML report to show dependecies in DSM view.


### How to use plugin:

Add <reporting> section to your pom.xml and <plugin> inside it.

For example:

    <reporting>
    
        <plugins>
            <plugin>
                <groupId>org.sevntu</groupId>
                <artifactId>dsm-maven-plugin</artifactId>
                <version>2.3</version>
            </plugin>

            <!--  other reportin plugins  -->

        </plugins>

    </reporting>
    
Run "mvn site". Enjoy :)
