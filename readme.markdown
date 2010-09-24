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



