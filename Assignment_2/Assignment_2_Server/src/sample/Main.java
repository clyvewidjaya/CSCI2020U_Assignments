package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main extends Application {
    static File serverShareFolder = new File("serverShare");
    ServerSocket serverSockett;
    @Override
    public void start(Stage primaryStage) throws IOException{
        serverSockett = new ServerSocket(9053);
        Runnable showRightTab = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        ServerSocket serverSocket = new ServerSocket(2726);
                        while(true){
                            Socket client = serverSocket.accept();
                            File[] filesInDir = serverShareFolder.listFiles();
                            PrintWriter out = new PrintWriter(client.getOutputStream());
                            for(int i = 0; i < filesInDir.length; i++){
                                String name = filesInDir[i].getName();
                                //System.out.println(name);
                                out.println(name);
                            }
                            String stop = "stop";
                            out.println(stop);
                            out.close();
                            client.close();
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };
        Thread rightTab = new Thread(showRightTab);
        rightTab.start();

        Runnable receiveFile = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        ServerSocket serverSocket = new ServerSocket(2819);
                        while(true){
                            Socket client = serverSocket.accept();
                            DataInputStream dis = new DataInputStream(client.getInputStream());
                            String fileName = dis.readLine();

                            FileWriter out = new FileWriter(serverShareFolder + "/" + fileName);

                            String inside;
                            while((inside = dis.readLine()) != null){
                                out.write(inside + "\n");
                            }
                            out.close();
                            dis.close();
                            client.close();
                            updateOthers();
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };

        Thread serverReceiveFile = new Thread(receiveFile);
        serverReceiveFile.start();

        Runnable sendFile = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        ServerSocket serverSocket = new ServerSocket(1997);
                        while(true){
                            Socket client = serverSocket.accept();
                            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String fileAsked = br.readLine();
                            br.close();
                            client.close();

                            client = serverSocket.accept();
                            DataOutputStream out = new DataOutputStream(client.getOutputStream());

                            br = new BufferedReader(new FileReader(serverShareFolder + "/" + fileAsked));
                            String all;
                            while((all = br.readLine()) != null){
                                out.writeBytes(all + "\n");
                            }
                            br.close();
                            out.close();
                            client.close();
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };

        Thread serverSendFile = new Thread(sendFile);
        serverSendFile.start();

        Runnable deleteFile = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        ServerSocket serverSocket = new ServerSocket(1904);
                        while(true){
                            Socket client = serverSocket.accept();
                            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String fileAsked = br.readLine();
                            File fileToDelete = new File(serverShareFolder + "/" + fileAsked);
                            fileToDelete.delete();
                            br.close();
                            client.close();
                            updateOthers();
                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };

        Thread delete = new Thread(deleteFile);
        delete.start();

    }

    public void updateOthers(){
        Runnable sendFileNames = new Runnable() {
            @Override
            public void run() {
                try{
                    //ServerSocket serverSocket = new ServerSocket(9053);
                    while(true){
                        Socket client = serverSockett.accept();
                        File[] filesInDir = serverShareFolder.listFiles();
                        PrintWriter out = new PrintWriter(client.getOutputStream());
                        for(int i = 0; i < filesInDir.length; i++){
                            String name = filesInDir[i].getName();
                                //System.out.println(name);
                            out.println(name);
                        }
                        String stop = "stop";
                        out.println(stop);
                        out.close();
                        client.close();
                    }
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        };

        Thread sendFileN = new Thread(sendFileNames);
        sendFileN.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}