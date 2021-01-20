package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Main extends Application{
    public static void main(String[] args) {
        Application.launch(args);
    }


    public SerialPort port=SerialPort.getCommPorts()[1];

    public String direction = "left";
    public int foodX=4, foodY=5;
    boolean gameOver=false;
    public  ArrayList<int[]> snake = new ArrayList<int[]>();
    public void start(Stage stage) {

        Button up = new Button("UP");
        Button down = new Button("DOWN");
        Button left = new Button("LEFT");
        Button right = new Button("RIGHT");
        up.setPrefSize(45, 20);
        up.setLayoutX(100); // Sets the X co-ordinate
        up.setLayoutY(200); // Sets the Y co-ordinate
        up.setFocusTraversable(false);
        down.setFocusTraversable(false);
        left.setFocusTraversable(false);
        right.setFocusTraversable(false);
        up.setOnAction(event -> handleAction("up"));
        down.setOnAction(event -> handleAction("down"));
        left.setOnAction(event -> handleAction("left"));
        right.setOnAction(event -> handleAction("right"));
        FlowPane root = new FlowPane(Orientation.VERTICAL, 0, 50, up, down, left, right);
        Scene scene = new Scene(root, 350, 350);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case DOWN: handleAction("down"); break;
                case UP: handleAction("up"); break;
                case LEFT: handleAction("left"); break;
                case RIGHT: handleAction("right"); break;
            }
        });
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Snake");
        stage.show();
        game();
    }
        void handleAction(String action){

            switch (action){
                case "left":{
                    if(direction!="right") direction = "left";
                    break;
                }
                case "right":{
                    if(direction!="left") direction = "right";
                    break;
                }
                case "up":{
                    if(direction!="down") direction = "up";
                    break;
                }
                case "down":{
                    if(direction!="up") direction = "down";
                    break;
                }
            }
            System.out.println("Direction = "+direction);
        }
        void snakePushPoint(int x, int y){
            int arr[] = new int[2];
            arr[0]=x;
            arr[1]=y;
            snake.add(arr);
        }
        void snakeAddTail(int x,int y,int i){
            int arr[] = new int[2];
            arr[0]=x;
            arr[1]=y;
            snake.add(i,arr);
        }
        void setup(){
            port.setComPortParameters(9600,8,1,0);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            port.openPort();
            snakePushPoint(16,4);
            snakePushPoint(17,4);
            snakePushPoint(18,4);
            snakePushPoint(19,4);
            try {Thread.sleep(300); } catch(Exception e) {}
            generateFood();
            setLed(foodX,foodY,1);
            setLed(foodX,foodY,1);

            //System.out.println("Setup end");
        };
        void setLed(int x, int y, int state){
            try {Thread.sleep(30); } catch(Exception e) {}
            PrintWriter output = new PrintWriter(port.getOutputStream());
            //System.out.println(x+" "+y+" "+state);
            output.print(x+" "+y+" "+state);
            output.flush();


        }
        void drawSnake(){
            int previousX, previousY, curX=0, curY=0, tailX=0,tailY=0;
            int i=0;
            boolean catchFood=false;

            for(int[] arr:snake){
                //System.out.println("Snake X="+arr[0]+"  Snake Y="+arr[0] );
                previousX=curX;
                previousY=curY;
                curX=arr[0];
                curY=arr[1];

                if(i==0){
                    if(arr[0]<32&&arr[0]>=0&&arr[1]<8&&arr[1]>=0) {

                        switch (direction) {
                            case "left":
                                arr[0]--;
                                break;
                            case "right":
                                arr[0]++;
                                break;
                            case "up":
                                arr[1]++;
                                break;
                            case "down":
                                arr[1]--;
                                break;
                        }
                        if (foodX == arr[0] && foodY == arr[1]) {
                            catchFood = true;
                            generateFood();
                        }
                        setLed(arr[0], arr[1], 1);
                    }else gameOver=true;
                }else {
                    if(i==snake.size()-1){
                         if(catchFood){
                             tailX=arr[0];
                             tailY=arr[1];  //System.out.println("Added tail");
                         }
                        else setLed(arr[0],arr[1],0);
                    }
                    arr[0] = previousX;
                    arr[1] = previousY;
                }
                i++;
            }
            if(catchFood) snakePushPoint(tailX,tailY);
        }
        void generateFood(){
            boolean correct=true;
            do{
                foodX=((int)(Math.random()*32));
                foodY=((int)(Math.random()*8));
                for(int[] arr:snake){
                    if(arr[0]!=foodX&&arr[1]!=foodY){
                        correct=false;
                        break;
                    }
                }
            }while (correct);
            try {Thread.sleep(100); } catch(Exception e) {}
            setLed(foodX,foodY,1);
            System.out.println("Food X="+foodX+"  Food Y="+foodY );
        }
        void clearScreen(){
            for(int i=0;i<32;i++)
                for(int j=0;j<8;j++) setLed(i,j,1);
        }
        void showAlert(){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game over");
                    alert.setHeaderText("Your score: "+(snake.size()-4));
                    alert.setContentText("I have a great message for you!");

                    alert.showAndWait();
                }
            });

        }
        void game(){
            Thread thread = new Thread("Game thread") {
                public void run(){
                    setup();
                    do{
                        try {Thread.sleep(100); } catch(Exception e) {}
                        drawSnake();
                    }while (!gameOver);
                    clearScreen();
                    showAlert();
                }
            };
            thread.start();
        }
    }



























    /*
                        setLed(29,1,1);
                        setLed(29,2,1);
                        setLed(29,3,1);
                        setLed(29,4,1);
                        setLed(29,5,1);
                        setLed(29,6,1);
                        setLed(30,4,1);
                        setLed(30,6,1);
                        setLed(31,1,1);
                        setLed(31,2,1);
                        setLed(31,3,1);
                        setLed(31,5,1);
     */