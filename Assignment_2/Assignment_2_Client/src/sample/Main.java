/*
This Main class will show the UI of the client server,
which will show the files in server folder, and client
folder. The UI will also have buttons, upload and down-
load, with some improvements of delete button, to delete
selected file either on server or client folder. Added a
filemenu bar too for improvement, with an option to select
client's share folder and exit the system

Author : Clyve Widjaya
SID : 100590208
*/
package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    boolean upd = true;
    ObservableList<String> right;
    ObservableList<String> left;
    ObservableList<String> forUpdate;
    static String hostName = "127.0.0.1"; //LocalHost
    //static String hostName = "192.168.0.17"; //Home IP
    File clientShareFolder;
    File current;
    //File clientShareFolder = new File("clientShare");
    ListView<String> leftTab = new ListView<String>();
    ListView<String> rightTab = new ListView<String>();
    @Override

    /*
    This function will show the main UI for client, which has the
    functions of upload to server, download from server, and delete
    files. When the program start, it will read the clientShare folder
    as the initial folder, user can move to another folder from the
    menu bar option provided.

    In this code, I used 2 types of updater, one with scheduled
    executor, and the other one is using thread. For default on
    submission, I activating the thread one, but if you want to
    test with the schedule executor, just comment out the
    thread start, and uncomment the schedule executor.
    @Param Stage primaryStage
    @return -
    */
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Assignment 2 Client");

        clientShareFolder = new File("clientShare");

        left = fileNamesForClient(clientShareFolder);
        leftTab.setItems(left);

        right = fileNamesForServer();
        rightTab.setItems(right);
        GridPane buttons = new GridPane();

        //UNCOMMENT CODE BELOW IF IT NEEDS TO BE TESTED TOO
/*
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        ex.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //right.clear();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (upd == true){
                            try{
                                right.clear();
                                right = fileNamesForServer();
                                //System.out.println(right);
                                rightTab.setItems(right);
                            } catch (IOException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }
        }, 0, 7, TimeUnit.SECONDS);
*/
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem openDir = new MenuItem("Open Directory");
        openDir.setOnAction( e -> {
            DirectoryChooser directoryChooser2 = new DirectoryChooser();
            directoryChooser2.setTitle("Choose Directory");
            directoryChooser2.setInitialDirectory(new File("."));
            clientShareFolder = directoryChooser2.showDialog(primaryStage);
            left.clear();
            left.setAll(fileNamesForClient(clientShareFolder));
        });
        fileMenu.getItems().add(openDir);
        fileMenu.getItems().add(new SeparatorMenuItem());

        MenuItem exitMenu = new MenuItem("Exit");
        exitMenu.setOnAction( e -> {
            System.exit(0);
        });
        fileMenu.getItems().add(exitMenu);
        menuBar.getMenus().add(fileMenu);

        Button download = new Button();
        download.setText("Download");

        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileNameAsked = rightTab.getSelectionModel().getSelectedItem();
                //System.out.println(fileNameAsked);
                try{
                    Socket socket = new Socket (hostName, 1997);
                    PrintWriter tellServer = new PrintWriter(socket.getOutputStream());
                    tellServer.println(fileNameAsked);
                    tellServer.close();
                    socket.close();

                    socket = new Socket(hostName,1997);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    FileWriter out = new FileWriter(clientShareFolder + "/" + fileNameAsked);

                    String inside;
                    while((inside = dis.readLine()) != null){
                        out.write(inside + "\n");
                    }
                    out.close();
                    dis.close();
                    socket.close();

                    Stage ask = new Stage();
                    Label label1 = new Label();
                    label1.setText("   Save To Current Folder ?");

                    Button yes = new Button();
                    yes.setText("      Yes      ");
                    yes.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            left.clear();
                            left.setAll(fileNamesForClient(clientShareFolder));
                            ask.close();
                        }
                    });

                    Button no = new Button();
                    no.setText("      No      ");
                    no.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            DirectoryChooser directoryChooser2 = new DirectoryChooser();
                            directoryChooser2.setTitle("Choose Directory To Save");
                            directoryChooser2.setInitialDirectory(new File("."));
                            current = directoryChooser2.showDialog(ask);

                            File theFile = new File(clientShareFolder + "/" + fileNameAsked);
                            theFile.renameTo(new File(current + "/" + fileNameAsked));
                            left.clear();
                            left.setAll(fileNamesForClient(clientShareFolder));
                            ask.close();
                        }
                    });
                    GridPane butt = new GridPane();
                    butt.add(yes,0,0);
                    butt.add(no,1,0);

                    BorderPane inAsk = new BorderPane();
                    inAsk.setTop(label1);
                    inAsk.setBottom(butt);
                    inAsk.setPadding(new Insets(25,25,25,25));

                    ask.setScene(new Scene(inAsk, 230, 100));
                    ask.show();
                } catch (IOException ex){
                    ex.printStackTrace();
                }

            }
        });

        Button upload = new Button();
        upload.setText("Upload");

        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String filename = leftTab.getSelectionModel().getSelectedItem();
                //System.out.println(filename);
                try{
                    upd = false;
                    Socket socket = new Socket (hostName, 2819);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeBytes(filename +"\n");

                    BufferedReader br = new BufferedReader(new FileReader(clientShareFolder + "/" + filename));
                    String all;
                    while((all = br.readLine()) != null){
                        out.writeBytes(all + "\n");
                    }
                    br.close();
                    out.close();
                    socket.close();
                    left.clear();
                    left.setAll(fileNamesForClient(clientShareFolder));
                } catch (IOException ex){
                    ex.printStackTrace();
                }
                upd = true;
            }
        });

        Button delete = new Button();
        delete.setText("Delete");

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String clientFile = leftTab.getSelectionModel().getSelectedItem();
                String serverFile = rightTab.getSelectionModel().getSelectedItem();
                if (clientFile == null){
                    try {
                        Socket socket = new Socket (hostName, 1904);
                        PrintWriter tellServer = new PrintWriter(socket.getOutputStream());
                        tellServer.println(serverFile);
                        tellServer.close();
                        socket.close();
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }

                } else if (serverFile == null){
                    File fileToDelete = new File(clientShareFolder + "/" + clientFile);
                    fileToDelete.delete();
                    left.clear();
                    left.setAll(fileNamesForClient(clientShareFolder));
                }
            }
        });

        Runnable update = new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Socket upSocket = new Socket(hostName,9053);
                        BufferedReader line = new BufferedReader(new InputStreamReader(upSocket.getInputStream()));
                        String read = line.readLine();
                        //System.out.println(read);
                        String alll = read;
                        while(read.equals("") == false){
                            while(read.equals("stop") == false){
                                if (read.equals("stop")){
                                    break;
                                }
                                read = line.readLine();
                                alll = alll + "," + read;
                            }
                            final String[] all2 = alll.split(",");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    ObservableList<String> forUpdate = FXCollections.observableArrayList();
                                    for (int i = 0; i < all2.length-1; i++){
                                        forUpdate.add(all2[i]);
                                    }
                                    right.clear();
                                    right.setAll(forUpdate);
                                }
                            });
                            break;
                        }
                        Thread.sleep(7000);
                        upSocket.close();
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                    catch (java.lang.InterruptedException i){}
                }
            }
        };

        //COMMENT THIS PART, IF UPDATE USING SCHEDULED EXECUTOR
        Thread runUpdate = new Thread(update);
        runUpdate.start();

        buttons.add(download,0,0);
        buttons.add(upload,1,0);
        buttons.add(delete, 2,0);

        SplitPane tabs = new SplitPane();

        tabs.getItems().addAll(leftTab,rightTab);

        BorderPane all = new BorderPane();
        all.setTop(menuBar);
        all.setCenter(tabs);
        all.setBottom(buttons);
        all.setPadding(new Insets(5,5,5,5));

        primaryStage.setScene(new Scene(all, 700, 500));
        primaryStage.show();
    }

    /*
    This function will return list of file names from the current client folder
    chosen.
    @Param File clientShareFolder
    @return ObservableList<String> all
    */
    public static ObservableList<String> fileNamesForClient(File clientShareFolder){
        ObservableList<String> all = FXCollections.observableArrayList();

        File[] filesInDir = clientShareFolder.listFiles();

        for (int i = 0; i < filesInDir.length; i++){
            if (filesInDir[i].isFile()){
                all.add(filesInDir[i].getName().toString());
                //System.out.println(filesInDir[i].getName().toString());
            }
        }

        return all;
    }

    /*
    This function will return list of file names from the server share folder.
    This function will open a new socket to receive all the file names from
    server folder.
    @Param -
    @return ObservableList<String> all
    */
    public static ObservableList<String> fileNamesForServer() throws IOException{
        ObservableList<String> all = FXCollections.observableArrayList();
        try{
            Socket socket = new Socket(hostName, 2726);
            while(true){
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String fileName = br.readLine();
                //System.out.println(fileName);
                while(fileName.equals("stop") == false){
                    if (fileName.equals("stop")){
                        break;
                    }
                    all.add(fileName);
                    fileName = br.readLine();
                    //System.out.println(fileName);
                }
                break;
            }
            socket.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return all;
    }

    public static void main(String[] args) {
        launch(args);
    }
}