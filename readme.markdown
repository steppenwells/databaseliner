DataBaseliner
=============

Introduction
------------

DataBaseliner is a tool that allows a self contained subset of data to be extracted from a database.
By defining key seed data to extract and setting up relationships dataBaseliner will extract a slice of
data from a source database that is completely referentially integral and output this as a SQL script
that can be run into development and test schemas.

DataBaseliner is written using Java and jdbc to connect to databases, so that it can work with any
database that provides a compliant jdbc driver. As different drivers and databases have different
idiosyncrasies and requirements for SQL generation a flexible extension mechanism is used to add
support for different databases. Support for each database is provided as a separate jar / sub module.

Configuring DataBaseliner
=========================

DataBaseliner is configured using an xml document that defines how to connect to the database,
what to extract, and how to output it. The document is made up of:

* databaseConnection - configures the source database
* extractionPlan - configures the data to extract
 * extractions - configure how to seed the extraction
 * relationships - configure how to spider across the database from the seed tables
* manipulations - configure how to modify the extracted data before outputting
* outputDetails - configures the output of the extraction

*skeleton xml*:

    <databaseliner>
        <databaseConnection>...</databaseConnection>
        <outputDetails>...</outputDetails>
        <extractionPlan dryRun="false">...</extractionPlan>
        <manipulations>...</manipulations>
    </databaseliner>

databaseConnection
------------------

This section configures the jdbc connection details of the source database and the dialect for
dataBaseliner to use. For example:

    <databaseConnection>
        <driver>org.postgresql.Driver</driver>
        <connectionUrl>jdbc:connection:url</connectionUrl>
        <user>db user</user>
        <password>password</password>
        <dialect>org.databaseliner.dialect.postgres.PostgresDialect</dialect>
    </databaseConnection>

The currently available dialects are:

* org.databaseliner.dialect.postgres.PostgresDialect
* org.databaseliner.dialect.oracle.OracleDialect

More information about adding dialects can be found on the wiki (shortly).

outputDetails
-------------

This section configures the output from dataBaseliner:

    <outputDetails>
        <outputDirectory>output</outputDirectory>
        <report>postgres_output.html</report>
        <script>postgres_output.sql</script>
        <preserveDatabaseIntegrity>false</preserveDatabaseIntegrity>
    </outputDetails>

setting *preserveDatabaseIntegrity* to true will make the resulting sql script produce insert and update
statements in the order required to be run into a database with all constraints enabled. Note this makes
outputting the script slower and is likely to take longer to insert into your target database.

extractionPlan
--------------

This is the meat of the dataBaseliner configuration. The extraction plan describes the source database
and the data you want to extract. The plan is made up of *ignore*, *extraction* and *relationship* nodes:

    <extractionPlan dryRun="false">
        <ignore ... />
        <ignore ... />
        ...

        <extraction .../>
        <extraction .../>
        ...

        <relationship .../>
        <relationship .../>
        ...
    </extractionPlan>

Setting *dryRun* to true does not extract any data but instead explores the database metadata to check the
configured extraction plan. This produces an html report which you can use to check your plan is correct and
will pull back data from the expected tables.

### ignore

    <ignore schema="SCHEMA" table="TABLE" />

Does what it says on the tin. No data will be automatically extracted from tables marked as ignored.

If you configure an extraction or an explicit relationship with the ignored table data will be extracted
for it, this allows you to control precisely what data gets put in the table.

### extraction

Extractions configure the seed data set for your exrtraction. There are 3 types of extractions:

* all - This extraction pulls in all data in a table, this is commonly used for static data tables.
* matchingIds - This extraction pulls in all data in a table where the value in a given column matches one
of the provided ids. This allows you to extract known data from the database, for example pulling in
known clients from a accounting database.
* query - Seed extraction that will extract rows from a table using an arbitrary SQL query. This is
intended to be used when the data you want can not be simply defined by one table and a finite set of ids.
This includes when the seed data set is defined by joining multiple tables or when you want to extract
data based on more complex 'where' logic as opposed to a known data set (for example extracting all the
data for the last 7 days).

More information about the extractions, including example configuration xml can be found on the wiki and
in the javadocs for the extraction classes.

### relationship

Relationships configure how databaseliner spiders out from the seed extraction tables to produce a
consistent dataset. By default databaseliner will follow all foreign keys defined in you database so that
if you have data in table A that refers to data in table B the rows in table B that are referenced will
be extracted. Databaseliner will continue following foreign keys until a fully referentially consistent
data set is extracted.

Following foreign keys will however only get you do far. In an reasonably complex database schema you will
have relationships between tables where the direction of the foreign keys means that data will not
automatically be extracted. You may also have join tables where you only want to pull in data that matches
data in multiple tables, or even relationships you only want to extract if certain criteria are met.

Additionally if your database does not have any foreign keys configured you can define the relationships
between tables manually using the relationship xml.

To configure how databaseliner spiders data in these scenarios you can add relationships in the extraction
plan. The available relationship types are:

* ignored - Tells databaseliner to explicitly ignore a foreign key relationshiop that would otherwise be
followed. This allows you to configure another relationship to extract the data or simply ignore the
relationship.
* refersTo - Relationship from a single table to another table. This acts like the default foreign key
extraction that gets data from a table that is referred to by the seed table except that no foreign need
exist in the database.

This can be used to model the inverse of an existing foreign key, extracting all the data in a table that
refers to the seed table, or where no foreign key relationship is defined (for example where audit data
about an object outlives the object itself so no foreign key can be defined).

More readme to come soon
========================

in the meantime look at the code or generate the javadocs, the extractions, relationships and manipulations
are described in detail.