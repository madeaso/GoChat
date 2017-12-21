CS 408 Team Project
Marc Deaso
Joshua Itagaki
Chris Kang

Go

Steps for installing go and running programs:
1)	Download the binary distribution for your operating system from https://golang.org/dl/
2)	Run the setup wizard to install the tools needed to run go programs.
3)	If not done by the installer, set GOROOT environment variable to the Go directory, and add the Go/bin directory to the PATH environment variable.
4)	Move the ChatServer and ChatClient folders to the Go/src directory.
5)	In a command prompt, use the following commands to create executable files in the Go/bin directory.

>go install ChatServer

>go install ChatClient

6)    Run the application by first running the server, then connecting with the client.
7)    If running the server on your local machine the address to connect should be: localhost:40800

The Java version of the client can be run using the following commands from a command prompt in the Java/ChatClient directory, assuming Java is installed on the system:

>javac ChatClient.java

>java ChatClient
