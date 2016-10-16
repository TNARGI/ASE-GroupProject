package backend;
import java.io.*;
import java.net.*;
public class BackEnd {
    
    static String filePath = "C:/App Records";
    static int port = 5432;
    
    public static void main(String[] args) throws Exception {
        //If directory does not exist, create directory:
        File appDir = new File(filePath);
        appDir.mkdir();
        //test fileCreator:
        //addRecord("20","2","4","500");
        while(true){
            listen(port);
        }
    }
    
    public static void listen(int portNumber) throws IOException, Exception{
        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            //PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String imei = in.readLine();
            String phoneTime = in.readLine();
            String latitude = in.readLine();
            String longitude = in.readLine();
            addRecord(imei, phoneTime, latitude, longitude);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    
    public static void addRecord(String imei, String phoneTime, String latitude, String longitude) throws Exception {
        //Add new record to directory:
        long longTime = System.currentTimeMillis();
        String serverTime = Long.toString(longTime);
        //Test filepath:
        System.out.println(filePath + "/" + serverTime + ".txt");
        File appRec = new File(filePath + "/" + serverTime + ".txt");
        appRec.createNewFile();
        FileOutputStream out = new FileOutputStream(appRec);
        String text = imei + "\n" + serverTime + "\n" + phoneTime + "\n" + latitude + "\n" + longitude;
        byte buffer [] = text.getBytes();
        out.write(buffer);
        out.close();
    }
    
}
