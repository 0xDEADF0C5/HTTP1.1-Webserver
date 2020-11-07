import java.net.*;
import java.io.*;
import java.util.*;

public class HTTPRequest implements Runnable
{
	private Socket clientSocket;
	private HttpCookie visitCookie = new HttpCookie("ejn35-visits","0");
	
	public HTTPRequest(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
	
	/**
	 * Used to continuously run the HTTP server
	 */
	public void run()
	{
		try {
			read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This is the main method for reading requests and generating responses for the HTTP server
	 * @throws IOException Throws an appropriate exception if one of the streams has a problem with input/output
	 */
	public void read() throws IOException
	{
		BufferedReader dataInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		OutputStream dataOutput = clientSocket.getOutputStream();
		BufferedOutputStream bufferedOut = new BufferedOutputStream(dataOutput);
		
		//load properties from file
        FileReader fr = new FileReader("./config.properties");
        Properties prop = new Properties();
        prop.load(fr);
		
		String httpRequest = dataInput.readLine().trim();				//trim() is used so the spaces are uniform (single spaces)
		StringTokenizer tokenizer = new StringTokenizer(httpRequest);
		String httpHeader = tokenizer.nextToken();
		
		if (httpHeader.equals("GET"))
		{
			String fileRequest = tokenizer.nextToken();
			String fileName = fileRequest.substring(1, fileRequest.length());		//1 is used here to skip the "/" so only the file name is read
			
			boolean fileExists = true;		//used later for sending a 200 OK response ot 404 if the file does not exist
			
			if(fileName.equals(""))
			{
				fileName = "index.html";	//I know this was not part of the project, but I made an index for when no file is requested
			}
			
			FileInputStream fileInput = null;
			try
			{
				fileInput = new FileInputStream("C:\\Users\\Arrick Da Grate\\eclipse-workspace\\HTTP1.1 Server\\src\\" + fileName);
			}
			catch(FileNotFoundException notFound)
			{
				fileExists = false;
			}
			
			String status = "";
			String contentType = "";
			String htmlBody = "";
			String contentLength = "";
			
			if(fileExists)					//this code runs if the file exists that was requested
			{
				status = "HTTP/1.1 200 OK\r\n";
				contentType = "Content-type: text/html\r\n";
				contentLength="Content-Length: " + (fileInput.available()) +"\r\n";
				
				//cookie is incremented if a successful visit to a file on the server occurs
				int visitCount = Integer.parseInt(visitCookie.getValue());
				visitCount++;
				visitCookie.setValue("" + visitCount);
			}
			else							//shows a 404 error that the file cannot be requested
			{
				status = "HTTP/1.1 404 NOT FOUND\r\n";
				contentType = "Content-Type: text/html\r\n";
				htmlBody = "<HTML>" + "<MAIN><BODY><H1>404 Error: Page Not Found</H1></BODY></MAIN>" + "</HTML>";
			}
			
			bufferedOut.write(status.getBytes());	//these lines write and send the html response, whether it was a 200 or a 404
			bufferedOut.write(contentType.getBytes());
			bufferedOut.write("\r\n".getBytes());
			
			if (fileExists)				//sends the files to the browser in bytes
				sendAsBytes(fileInput, bufferedOut);
			else						//prints the 404 error on the browser
				bufferedOut.write(htmlBody.getBytes());
			bufferedOut.close();
			clientSocket.close();
		}
	}
	
	/**
	 * Takes an input stream and outputs that data, with a 1KB buffer
	 * @param input the input data
	 * @param output the data sent from the input data
	 * @throws IOException if one of the streams has a problem, an appropriate input/output exception is thrown
	 */
	private void sendAsBytes(FileInputStream input, OutputStream output) throws IOException
	{
		byte[] bufferBytes = new byte[1024];
		int numberOfBytes = 0;
		while((numberOfBytes = input.read(bufferBytes)) != -1)
		{
			output.write(bufferBytes, 0, numberOfBytes);
		}
	}
}
