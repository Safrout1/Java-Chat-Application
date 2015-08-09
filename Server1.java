import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Server1 {

	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;

	static ArrayList<clientThread> threads;

	public static void main(String args[]) {

		int portNumber = 6000;
		threads = new ArrayList<clientThread>();
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				clientThread x = new clientThread(clientSocket, threads);
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
}

class clientThread extends Thread {

	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	ArrayList<clientThread> threads;
	String name;

	public clientThread(Socket clientSocket, ArrayList<clientThread> threads2) {
		this.clientSocket = clientSocket;
		this.threads = threads2;
	}

	public void run() {
		ArrayList<clientThread> threads = this.threads;

		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Enter your name.");

			while (true) {
				name = is.readLine().trim();
				boolean found = false;
				for (int i = 0; i < threads.size(); i++) {
					if (threads.get(i).name.equalsIgnoreCase(name)) {
						if (threads.get(i) != this) {
							found = true;
							break;
						}
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
				// System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				if (line.trim().equalsIgnoreCase("QUIT")
						|| line.trim().equalsIgnoreCase("BYE")) {
					break;
				} else

				if (line.startsWith("MEMBERS")) {
					// System.out.println("hello not server");
					if (name.equals("server")) {
						// System.out.println("hello server");
						StringTokenizer tok = new StringTokenizer(line, "@");
						tok.nextToken();
						String s = tok.nextToken();
						os.println("@" + s + ": <" + name + ": "
								+ " The online members are: ");
						os.print("@" + s + ": <" + name + ":  ");
						for (int i = 0; i < threads.size() - 1; i++) {
							if (!threads.get(i).name.equals("server"))
								os.print(threads.get(i).name + ", ");
						}
						if (threads.size() > 0)
							if (!threads.get(threads.size() - 1).name
									.equals("server"))
								os.println(threads.get(threads.size() - 1).name
										+ ".");
						os.println("You can talk to any of these.");
					} else {
						os.println("The online members on this server are: ");
						for (int i = 0; i < threads.size() - 1; i++) {
							if (!threads.get(i).name.equals("server"))
								os.print(threads.get(i).name + ", ");
						}
						if (threads.size() > 0)
							if (!threads.get(threads.size() - 1).name
									.equals("server"))
								os.println(threads.get(threads.size() - 1).name
										+ ".");
					}
				} else if (line.trim().equalsIgnoreCase("OTHERMEMBERS")) {
					for (int i = 0; i < threads.size(); i++) {
						if (threads.get(i).name.equalsIgnoreCase("server")) {
							threads.get(i).os.println("MEMBERS @" + name);
						}
					}
				} else {

					String name2 = st.nextToken();
					if (name2.charAt(0) != '@') {
						os.println("Please enter the name of the person you want to talk with preceeded with an @ without any spaces and write your sentence again.");
						continue;
					}
					name2 = name2.substring(1);
					String name4 = name2.substring(1);
					// System.out.println(name2);
					String l2 = line.substring(name2.length() + 1);
					// System.out.println(line);
					boolean found = false;
					for (int i = 0; i < threads.size(); i++) {
						if (threads.get(i).name.equalsIgnoreCase(name2)
								|| threads.get(i).name.equalsIgnoreCase(name4)) {
							found = true;
							if (line.contains("The online members")) {
								threads.get(i).os.println(l2);
							} else if (line.startsWith("#")) {
								name2 = name2.substring(1);
								l2 = line.substring(name2.length() + 1);
								threads.get(i).os.println(l2);
							} else {
								found = true;
								if (name.equals("server")) {
									StringTokenizer st1 = new StringTokenizer(
											line);
									String name22 = st1.nextToken();
									name22 = name22.substring(1);
									String l22 = line
											.substring(name22.length() + 1);

									st1 = new StringTokenizer(l22);
									String name222 = st1.nextToken();
									name222 = name222.substring(1);
									String l222 = l22.substring(name222
											.length() + 4);

									threads.get(i).os.println("<" + name222
											+ ": " + l222);
								} else
									threads.get(i).os.println("<" + name + ": "
											+ l2);
								break;
							}
						}
					}

					if (!found) {
						clientThread x = null;
						if (!name.equals("server"))
							for (int i = 0; i < threads.size(); i++) {
								if (threads.get(i).name
										.equalsIgnoreCase("server")) {
									found = true;
									x = threads.get(i);
									x.os.println("@" + name2 + ": <" + name
											+ ": " + l2);
									break;
								}
							}
						if (!found) {

							if (name.equals("server")) {
								StringTokenizer lolo = new StringTokenizer(l2);
								String name3 = lolo.nextToken();
								name3 = name3.substring(1);
								String l3 = l2.substring(name3.length() + 1);
								os.println("@"
										+ name3
										+ ": <"
										+ name
										+ ": "
										+ " The person you are talking to is not online");
								os.println("@" + name3 + ": <" + name + ": "
										+ " The online members are: ");
								os.print("@" + name3 + ": <" + name + ":  ");
								for (int i = 0; i < threads.size() - 1; i++) {
									if (!threads.get(i).name.equals("server"))
										os.print(threads.get(i).name + ", ");
								}
								if (threads.size() > 0)
									if (!threads.get(threads.size() - 1).name
											.equals("server"))
										os.println(threads.get(threads.size() - 1).name
												+ ".");
								os.println("You can talk to any of these.");
							} else {
								os.println("The person you are talking to is not online");
								os.println("The online members are: ");
								for (int i = 0; i < threads.size() - 1; i++) {
									if (!threads.get(i).name.equals("server"))
										os.print(threads.get(i).name + ", ");
								}
								if (threads.size() > 0)
									if (!threads.get(threads.size() - 1).name
											.equals("server"))
										os.println(threads.get(threads.size() - 1).name
												+ ".");
								os.println("You can talk to any of these.");
							}
						}
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