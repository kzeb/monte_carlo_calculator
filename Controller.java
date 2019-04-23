package sample;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class Controller {
    private DrawerTask task;

    @FXML
    private Canvas canvas;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField howMany;

    @FXML
    private Label results;

    @FXML
    public void onEnter(ActionEvent ae){
        handleRunBtnAction();
    }

    private int howMany(){
        int temp = Integer.parseInt(howMany.getText());
        System.out.println("Number of points: " + temp);
        return temp;
    }

    @FXML
    private void handleRunBtnAction(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        task = new DrawerTask(gc, howMany());
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                double res = task.getVarRes();
                MonteCarlo mc = new MonteCarlo(res);
                double integral = mc.calculateIntegral();
                System.out.println(integral);
                results.setText(Double.toString(integral));
            }
        });
        new Thread(task).start();
    }

    @FXML
    private void handleStopBtnAction(){
        task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                double res = task.getVarRes();
                System.out.println("res " + res);
                MonteCarlo mc1 = new MonteCarlo(res);
                double integral = mc1.calculateIntegral();
                System.out.println(integral);
                results.setText(Double.toString(integral));
            }
        });
        task.cancel();
    }

    @FXML
    private void handleClearBtnAction(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 600, 400);
        results.setText("");
        howMany.setText("");
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
    }
}
