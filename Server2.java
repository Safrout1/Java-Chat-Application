import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Server2 implements Runnable {

	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	private static Socket clientSocket2 = null;
	static PrintStream os = null;
	private static DataInputStream is = null;
	private static boolean closed = false;
	private static BufferedReader inputLine = null;

	static ArrayList<clientThread2> threads;

	public static void main(String args[]) {

		int portNumber = 7000;
		threads = new ArrayList<clientThread2>();
		try {
			clientSocket2 = new Socket("localhost", 6000);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			os = new PrintStream(clientSocket2.getOutputStream());
			is = new DataInputStream(clientSocket2.getInputStream());
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		if (clientSocket2 != null && os != null && is != null) {
			new Thread(new Server2()).start();
			os.println("server");

		}

		while (true) {
			try {
				clientSocket = serverSocket.accept();
				clientThread2 x = new clientThread2(clientSocket, threads);
				threads.add(x);
				x.start();
			} catch (IOException e) {
				System.out.println(e);
			} catch (Exception e) {
				PrintStream os;
				try {
					os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void run() {
		String responseLine;
		try {
			while ((responseLine = is.readLine()) != null) {
				if (responseLine.startsWith("@")) {
					StringTokenizer st = new StringTokenizer(responseLine);
					String Reciever = st.nextToken("@: ");
					String Sender = st.nextToken("<: ");
					String msg = responseLine.substring(Reciever.length()
							+ Sender.length() + 7);
					if (msg != null)
						for (int i = 0; i < threads.size(); i++) {
							if (threads.get(i).name.equalsIgnoreCase(Reciever)) {
								threads.get(i).os.println("<" + Sender + ": "
										+ msg);
							}
						}
				}
				
				if (responseLine.startsWith("MEMBERS")) {
					StringTokenizer tok = new StringTokenizer(responseLine, "@");
					tok.nextToken();
					String s = tok.nextToken();
					os.println("@" + s
							+ " The online members are: ");
					os.print("@" + s + " @server  ");
					for (int i = 0; i < threads.size() - 1; i++) {
						os.print(threads.get(i).name + ", ");
					}
					if (threads.size() > 0)
						os.println(threads.get(threads.size() - 1).name
								+ ".");
					os.println("You can talk to any of these.");
				}
				if (responseLine.indexOf("m3a el salama") != -1) {
					closed = true;
					break;
				}
			}
			os.close();
			is.close();
			clientSocket.close();
			closed = true;
		} catch (IOException e) {
			System.err.println("IOException:  1" + e);
		}
	}
}

class clientThread2 extends Thread {

	DataInputStream is = null;
	PrintStream os = null;
	private Socket clientSocket = null;
	ArrayList<clientThread2> threads;
	String name;

	public clientThread2(Socket clientSocket, ArrayList<clientThread2> threads2) {
		this.clientSocket = clientSocket;
		this.threads = threads2;
	}

	public void run() {
		ArrayList<clientThread2> threads = this.threads;

		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Enter your name.");

			while (true) {
				name = is.readLine().trim();
				boolean found = false;
				for (int i = 0; i < threads.size(); i++) {
					if (threads.get(i).name.equalsIgnoreCase(name)
							&& threads.get(i) != this) {
						found = true;
						break;
					}
				}
				if (found)
					os.println("The name is taken, Enter another name pleas or say bye or quit if you want to leave");
				else
					break;
			}

			os.println("Hello "
					+ name
					+ ".\nTo leave enter QUIT or BYE followed by pressing enter 2 times.");

			// String name2 = is.readLine().trim();

			while (true) {
				String line = is.readLine();
				StringTokenizer st = new StringTokenizer(line);
				if (line.trim().equalsIgnoreCase("QUIT")
						|| line.trim().equalsIgnoreCase("BYE")) {
					break;
				} else

				if (line.trim().equalsIgnoreCase("MEMBERS")) {
					os.println("The online members on this server are: ");
					for (int i = 0; i < threads.size() - 1; i++) {
						os.print(threads.get(i).name + ", ");
					}
					if (threads.size() > 0)
						os.println(threads.get(threads.size() - 1).name + ".");
				} else if (line.trim().equalsIgnoreCase("OTHERMEMBERS")) {
					
					Server2.os.println("MEMBERS @" + name);
				} else {

					String name2 = st.nextToken();
					if (name2.charAt(0) != '@') {
						os.println("Please enter the name of the person you want to talk with preceeded with an @ without any spaces and write your sentence again.");
						continue;
					}
					name2 = name2.substring(1);
					String l2 = line.substring(name2.length() + 1);

					boolean found = false;

					for (int i = 0; i < threads.size(); i++) {
						if (threads.get(i).name.equalsIgnoreCase(name2)) {
							found = true;
							threads.get(i).os.println("<" + name + ": " + l2);
							break;
						}
					}

					if (!found) {

						Server2.os
								.println("@" + name2 + " @" + name + " " + l2);

						/*
						 * os.println("The person you are talking to is not online"
						 * ); os.println("The online members are: "); for (int i
						 * = 0; i < threads.size() - 1; i++) {
						 * os.print(threads.get(i).name + ", "); } if
						 * (threads.size() > 0)
						 * os.println(threads.get(threads.size() - 1).name +
						 * "."); os.println("You can talk to any of these.");
						 */
					}
				}
			}
			os.println("Bye " + name);
			for (int i = 0; i < threads.size(); i++) {
				if (threads.get(i) == this) {
					threads.remove(i);
					break;
				}
			}
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
}