package simulationReplay;

import asyncSimulation.ConfigureManger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;

import java.util.List;


public class SimulationReplay extends Application {
    SimReplayManger manger;
    private GraphicsContext g1, g2;
    final int WIDTH = 1200;
    final int HEIGHT = 800;
    boolean stop_animation;

    @Override
    public void init() throws Exception {
        stop_animation = false;
        String simLogPath = ConfigureManger.getConfigureManger().getSimLogPath();
        manger = new SimReplayManger(simLogPath);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Simulation Replay");
        VBox root = new VBox();
        Canvas[] canvass = createContent();
        StackPane stkPan1 = new StackPane();
        stkPan1.getChildren().add(canvass[0]);
        stkPan1.setStyle("-fx-border-color: black");
        root.getChildren().add(stkPan1);
        StackPane stkPan2 = new StackPane();
        stkPan2.getChildren().add(canvass[1]);
        stkPan2.setStyle("-fx-border-color: black");
        root.getChildren().add(stkPan2);

        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }

    public Canvas[] createContent() {
        Canvas canvas1 = new Canvas(WIDTH , HEIGHT/2);
        Canvas canvas2 = new Canvas(WIDTH , HEIGHT/2);
        g1 = canvas1.getGraphicsContext2D();
        g2 = canvas2.getGraphicsContext2D();


        AnimationTimer timer;
        timer = new AnimationTimer() {
            double lastUpdate = 0;
            double sim_time = 0;

            @Override
            public void handle(long now) {
                double duration;
                if (lastUpdate == 0)
                    duration = 0;
                else
                    duration = (now - lastUpdate) / 1000000000.0;
                sim_time += duration;
                lastUpdate = now;
                drawObjects(manger.getBodies(), g1, 7);
                drawObjects(manger.getBodies(), g2, 30);
                stop_animation = manger.updateWorld(duration);
                if (stop_animation) {
                    this.stop();
                    System.out.print("Finished!\n");
                }
            }

            private void drawObjects(List<Body> bodies, GraphicsContext g, double scale) {
                g.clearRect(0, 0, WIDTH , HEIGHT/2);
                Body camerLockBody = bodies.get(0);
                Vector2 coordinateOrigin = camerLockBody.getWorldCenter();
                double xOrigin = 0;
                double yOrigin = 0;
                double screenXOrigin = 0;
                double screenYOrigin = 0;
                if (g == g2) {
                    xOrigin = coordinateOrigin.x;
                    yOrigin = coordinateOrigin.y;
                    screenXOrigin = WIDTH / 2;
                    screenYOrigin = HEIGHT / 4;
                }

                for (Body body : bodies) {
                    Polygon polygon = (Polygon) body.getFixture(0).getShape();
                    Vector2 wordCenter = body.getWorldCenter();
                    Vector2[] vertices = polygon.getVertices();
                    int l = vertices.length;
                    double[] xPoints = new double[l];
                    double[] yPoints = new double[l];
                    for (int i = 0; i < l; i++) {
                        xPoints[i] = (vertices[i].x + wordCenter.x - xOrigin) * scale + screenXOrigin;
                        yPoints[i] = (vertices[i].y + wordCenter.y - yOrigin) * scale + screenYOrigin;
                    }
                    g.setFill(Color.GREEN);
                    g.fillPolygon(xPoints, yPoints, l);
                }
            }
        };
        timer.start();
        return new Canvas[]{canvas1, canvas2};
    }
}
