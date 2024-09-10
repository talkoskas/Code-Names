# CodeNames
Project Overview:
Code Names game room server using Java and Tomcat (using client-server architecture). 
Extended version of the Code Names game with support for an admin user who manages various game rooms created from XML files. Additional features include support for more than two teams and multiple definers. The admin user also has the option to watch active games.

Code Names Java Web Application with Tomcat
This project is a Java-based implementation of the popular game "Code Names" using a client-server architecture with Tomcat. The application features an admin interface and a player interface, each managed separately, and includes both web and client-side components.

Project Structure
Game Engine Module
The core class of this module is the Game class, which represents the game itself, and the GameCreator class, which is responsible for properly creating instances of the game. Other classes within this module, such as Team, Card, and Board, handle specific aspects of game functionality.

UI Module
The main class in the UI module is GameManager, which is responsible for managing the game from start to finish. It takes an existing game instance and controls its flow until the game ends.

Web Module
In the web module, the primary classes for storing necessary data in the ServletContext are located in the WebAppObjects package, specifically within the Objects subpackage. The management of these stored objects is handled by manager objects located in the Managers subpackage under the WebAppObjects directory.

Client Module
There are two primary objects in the client module that handle player interaction with the application:

AdminClient (located in the Admin directory): This class manages and runs the admin application. The main method resides here, and the admin application is executed via a batch script (.bat file).

PlayerClient (located in the Player directory): This class manages and runs the player application. Similar to AdminClient, it also has a main method and is run using a .bat script.

Each class has its own configuration (Config class), which handles the static or constant variables required by each module.

Running the Application
Two separate batch files are provided for running the applications:

One for the admin (AdminClient)
One for the player (PlayerClient)

Project Directories
CN2 - Contains the original project files.
Admin - Contains the AdminClient files from the Client module.
Player - Contains the PlayerClient files from the Client module.
Please note that the .bat commands should be run from the root project directory after the project has been built and all artifacts have been generated.
