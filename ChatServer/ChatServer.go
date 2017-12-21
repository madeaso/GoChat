/* Go group
 * CS 408 - Team Project
 *
 * ChatServer.go
 * Last Edited: March 5, 2017
 * A TCP server running our chat application. Allows many
 * client connections and broadcasts incoming messages to all
 * connections. Listening for connections on port 40800.
 */

package main

import (
	"fmt"
	"net"
)

// Struct: client
// A new instance of the client struct is created for each
// new connection.
type client struct {
	username string
	conn     net.TCPConn
}

// Define constants.
const MAX_CLIENTS = 100
const CHAN_SIZE = 100
const LIST_PORT = "40800"

// Declare global variables.
var clients []client
var messageBuffer chan string

func main() {
	tcpAddress, err := net.ResolveTCPAddr("tcp", ":"+LIST_PORT)
	checkError(err)
	listener, err := net.ListenTCP("tcp", tcpAddress)
	checkError(err)

	clients = make([]client, MAX_CLIENTS)
	messageBuffer = make(chan string, CHAN_SIZE)

	// Start goroutine to broadcast any messages that enter the
	// messageBuffer channel.
	go broadcastMessages()

	// Create a new goroutine for each client that connects to
	// listen for incoming messages.
	for {
		connection, err := listener.AcceptTCP()
		checkError(err)
		go handleClient(connection)
	}
}

// Function: broadcastMessages()
// Runs an infinite loop that will send a message to all connected
// clients whenever a message enters the message buffer channel.
func broadcastMessages() {
	for {
		message := <-messageBuffer
		for i := 0; i < len(clients); i++ {
			clients[i].conn.Write([]byte(message))
		}
	}
}

// Function: handleClient(*net.TCPConn)
// Gets a client's connection and username and registers them in the
// clients slice. Also begins a loop that passes the client's
// messages to the messageBuffer until the client disconnects.
func handleClient(conn *net.TCPConn) {
	defer conn.Close()
	var inBuffer = make([]byte, 1024)

	// Register the client.
	length, err := conn.Read(inBuffer)
	checkError(err)
	thisClient := client{string(inBuffer[0:length]), *conn}
	clients = append(clients, thisClient)

	messageBuffer <- ("Client [" + thisClient.username + "] connected.\n")
	fmt.Println("Client [" + thisClient.username + "] connected.")

	// Pass the client's messages to messageBuffer.
	for {
		length, err = conn.Read(inBuffer)
		if err != nil {
			break
		}
		messageBuffer <- ("[" + thisClient.username + "]: " + string(inBuffer[0:length]) + "\n")
	}
	messageBuffer <- ("Client [" + thisClient.username + "] disconnected.\n")
	fmt.Println("Client [" + thisClient.username + "] disconnected.")
}

// Simple function that will cause a panic if an error occurs.
func checkError(err error) {
	if err != nil {
		panic(err)
	}
}
