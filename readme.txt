This is the API side of the Golf Stars application.
It is built using Gradle, the build automation tool.
It is written in the Kotlin language, a programming language designed to interoperate fully with Java.
It interacts with a MySQL database. A file necessary to create teh appropriate database and data in included with this submissions.
In this API bundle, there is an application.properties file that has the database access information as follows:
spring.datasource.url=jdbc:mysql://localhost:3306/golfStars
spring.datasource.username=root
spring.datasource.password=rootroot
You may need to adjust the database host, username and password to your database settings.

This programme interacts with Okta, a third party resource to handle user management.

The Okta security credentials are stored in a file okta.env
Therefore, before running the programme, it is necessary to run the command: source okta.env
This command incorporates the security credentials into the project.

The API can then be run with the command ./gradlew bootRun



The API interacts with a MYSQL database. It has a DataInitializer class that loads dummy data into the database.
 The DataInitializer is commented out to avoid duplicate entries.
 If you clear the database, you can repopulate some data by uncommenting the DataInitializer class

There are user accounts created that can interact with this API:
johnsmith@fakeemail.com
seanmurphy@fakeemail.com
maryobrien@fakeemail.com
marthaoreilly@fakeemail.com
jamesjameson@fakeemail.com
ultanhijinks@fakeemail.com
ursulateeoff@fakeemail.com
gobnaitgreenfees@fakeemail.com
mauriceirons@fakeemail.com

The password for all of the above is Password1
