/* Go group
 * CS 408 - Team Project
 *
 * ChatClient.go
 * Last Edited: March 5, 2017
 * A client implementation for our chat application. Connects to
 * the chat server and allows the user to communicate via text
 * based messages to other clients connected to the server.
 */

package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
)

func main() {
	read := bufio.NewScanner(os.Stdin)

	// Ask user for server address and port.
	fmt.Print("Please enter server address and port: ")
	read.Scan()

	tcpAddress, err := net.ResolveTCPAddr("tcp", read.Text())
	checkError(err)
	serverConnection, err := net.DialTCP("tcp", nil, tcpAddress)
	checkError(err)

	// Buffer required to recive bytes from TCP connection.
	var inBuffer = make([]byte, 1024)

	fmt.Print("Username: ")
	read.Scan()
	serverConnection.Write([]byte(read.Text()))

	// Start Goroutine to recieve messages from server.
	go func() {
		defer serverConnection.Close()
		for {
			length, err := serverConnection.Read(inBuffer)
			checkError(err)
			fmt.Print(string(inBuffer[0:length]))
		}
	}()

	// Getting user input handled by main Goroutine.
	var userIn string = ""
	for userIn != "exit" {
		read.Scan()
		userIn = read.Text()
		if userIn != "exit" {
			serverConnection.Write([]byte(userIn))
		}
	}
}

// Simple function that will cause a panic if an error occurs.
func checkError(err error) {
	if err != nil {
		panic(err)
	}
}
