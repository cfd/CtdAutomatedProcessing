CtdAutomatedProcessing
======================

The CTD Automated Processing System (CAPS) is a program that parses various .xmlcon files into a format that is more efficient and readable. 
The properties of the sensors for each instrument are shown. The program will output six .psa files for each .xmlcon file which will contain 
various data used for the greater purpose for those who actually care :P. The process will run a batch file that calls the Seabird program 
in order to convert the data into its final form.Some data is stored in an SQL Lite database in which the java program connects to to receive 
important information.