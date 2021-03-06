# Learning project for datomic

Two things I found valuable to know about datomic as I got started:

- There is no such thing as a left join or a right join in the datalog query language.  You will have to either write a transaction function or have the application logic do it.  This is not as big a problem as it might seem.  Consider that since the database is immutable, you can break up your work into separate steps without anything getting out of sync.
- You can't change the data type of an attribute, you have to create a new attribute instead.  If you foresee the need for this or want that flexibility, you can embed the name of the data type into the name of the attribute.  For example, instead of an attribute named :inventory/part-number with type long, you can have attribute named :inventory/part-number/long.  That way, if you need to change the type to string, you can create another attribute named :inventory/part-number/string of type string.  All of your history will be preserved from when the data type was long, so you can either write transaction functions or application logic to handle historical data in a sensible way.

## Install Datomic

- Don't forget to install the maven artifact to your local repository
- Get datomic from [Datomic Free Downloads](https://my.datomic.com/downloads/free) and follow the installation instructions
- In the datomic directory, run bin/maven-install
- Edit the pom.xml file so that the datomic version matches, for example:


        <dependency>
            <groupId>com.datomic</groupId>
            <artifactId>datomic-free</artifactId>
            <version>0.9.5302</version>
        </dependency>

## Launch Datomic

- This is not necessary if you are using the embedded in-memory database, datomic:mem://sample
- In the datomic directory run


        bin/transactor config/samples/free-transactor-template.properties
