# OpsProDatabase
Store the OpsPro car locations in a database and track movements

The OpsPro XML files in your JMRI installation are read and stored in an H2 (Open Source JAVA SQL database). 
The opening dialog asks you to specify you OpsPro home location, the database name, location, userid and password.
That information is stored in a properties file, and each successive use will reuse them (unless you change them).

The first run creates the database. Then each time you press a button (or run the command line version), the car locations 
will be read and stored. Then, using OpsPro to run and terminate one or more trains (which will move cars).
Press the button to store the new locations. You can then view how the cares moved.
