Having trouble compiling the oracle dialect via maven? Feeling down?

What you need is a nice bit of ojdbc14.jar action!

Unfortunately Oracle in their wisdom don't publish their drivers to maven repos so you'll have to go and get them yourself. To do this visit:

    http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html

Or if they've changed the urls explore the database area until you find the jdbc drivers. The driver that I used to develop the oracle dialect was the ojdbc14 driver that is available under the the 10g release 2 page. Agree to the licence, jump through the login hoops and you will eventually end up with the driver downloaded.

Once you've got this you will need to insall the driver into your local maven repository. to do this execute:

    mvn install:install-file -DgroupId=oracle.sql -DartifactId=ojdbc14.jar -Dversion=10.2 -Dpackaging=jar -Dfile=/path/to/file

Replacing /path/to/file with the erm, path to the downloaded file (although if you didn't work that out I guess your lack of jdbc driver may be the least of your problems).

It should all compile fine now, at least it did on my machine. Enjoy.
