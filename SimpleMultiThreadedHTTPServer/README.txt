README FILE
Project No : 0
Author(s): Anand Kulkarni
Course : CS557

PURPOSE:
[
  Develop a multithreaded HTTP server to serve only GET requests.
]

TO COMPILE:

[
  Steps for program compilation :
  1. Compile and create an executable named "Server.jar" using following command :	
	make (Note: The executable will created in the current directory)
]

TO RUN:

[
  Steps for program execution :
  1. To start a server run following command from within the folder that contains an executable :
	java -jar Server.jar 
  2. To run client :
	A. Execute a wget command from any empty directory from any machine within the same network.
	wget <RequestURL>
	For ex. wget http://remote01.cs.binghamton.edu:1024/lena_std.tiff
]

Implementation Details:

[
  1. A server runs on a separate thread and spanws a new thread for each requests it receives. A TCP Server socket is used to listen to the new requests.
  2. Once a request is assigned a new thread, it is parsed to extract required information such as  a type, protocol and resourceURI. If the requested resource is available in the Server root      directory then a 200 response is generated and sent over the socket to a client. If the requested resource is not found then 400 response is sent back to the client.
  3. Appropriate headers are set in the response.
  4. A HashMap data structure is used to maintain a track of how many time a particular a resource is requested by clients and is displayed on the server console.
  5. If the root directory is not present on the server or if an in-appropriate request is encoutered then server shuts down after displaying an appropriate error message.
]
