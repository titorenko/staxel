Staxel
=============

Staxel is a library for permissive XML parsing using [StAX](https://en.wikipedia.org/wiki/StAX). Main goal is to simplify permissive XML parsing with StAX without significant performance sacrifice. 

Permissive parsing means that library supports parsing independent of 
XML element location relative to all parent elements and independent of XML element sequence. The library is designed with 
[Robustness principle](https://en.wikipedia.org/wiki/Robustness_principle) in mind, that dictates to "Be conservative in what you send, be liberal in what you accept".


Maven dependency
--------------

Available from Maven Central:

    <dependency>
        <groupId>uk.elementarysoftware</groupId>
        <artifactId>staxel</artifactId>
        <version>0.1.0</version>
    </dependency>

Example usage
--------------

Prerequisites
--------------
Staxel requires Java 8 and has no other dependencies.

License
--------------
Library is licensed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).
        

