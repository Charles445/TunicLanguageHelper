# TunicLanguageHelper
 Tool to collect and review language notes in the game Tunic.  
 This tool is _**not a cheat sheet!**_ It will only show you what you put into it, so you can solve the language for yourself.  

## How To Use
  Use your mouse to draw symbols on the top section (input).  
  Left click adds lines, right click removes them. You can hold the mouse buttons down to draw quickly.  
  Clear button clears the input.  
  Arrow buttons shift the input left and right.  
  Make sure your symbols start all the way to the left!

  The middle section is the preview of what you've drawn.  
  Save button saves this symbol to the database. A confirmation box will ask you what to call it.  
  
  The bottom section is the database itself.  
  It will look at your saved symbols and try to guess what you're drawing, even if you haven't drawn it completely.  
  This is the main feature of the program - you can draw a full or partial symbol, and get a quick translation for it, saving you time.  
  Rename button allows you to rename the currently displayed symbol in the database, in case you want to change it.  
  Delete button allows you to delete the currently displayed symbol in the database, in case you saved the wrong symbol or don't want it anymore.  
  Copy button allows you to copy the displayed symbol to your input, so you don't have to draw the whole thing again. Very convenient.  
  
  ... What's that weird thing in the top left?  
  Don't worry about that... You'll know what it is when you need it.  

## Installing

**Make sure to put the jar in its own folder!**  
_(also make sure not to run more than one TunicLanguageHelper application at a time)_

Click on the JAR to run it. If the window pops up, you're ready to go!

The program will create a folder in that directory called "tuniclanghelp_db"  
This folder contains "database.txt", which has all of your stored data. Backups will be placed alongside it.

## Troubleshooting

Every time you make a change to the database, it will save a fresh backup in that same folder.  
In the event of database corruption, you can restore a backup by deleting the old database.txt, making a copy of the latest backup, and renaming it to database.txt.  
You may wish to delete or zip some of the backups eventually (I had hundreds by the end of the game...)

## Preview Image
![tlh_program](https://user-images.githubusercontent.com/1569981/231646921-081fc838-da60-4490-8eb3-5642df7cf59a.png)
