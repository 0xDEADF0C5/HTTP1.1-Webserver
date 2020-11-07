import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("Server is Online");
        
        //load properties from file
        FileReader fr = new FileReader("./config.properties");
        Properties prop = new Properties();
        prop.load(fr);
        
        //get port from properties file
        ServerSocket receiverSocket = new ServerSocket(Integer.parseInt(prop.getProperty("port")));
        
        //runs the server until manually terminated, hence the while(true), because while(true) will run forever
        while(true)
        {
        	Socket acceptionSocket = receiverSocket.accept();
        	HTTPRequest req = new HTTPRequest(acceptionSocket);
        	
        	//thread for each connection
        	Thread thread = new Thread(req);
        	thread.start();
        }
    }
}
