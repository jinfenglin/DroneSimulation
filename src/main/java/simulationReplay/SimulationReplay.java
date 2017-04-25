package simulationReplay;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;

import java.util.List;


public class SimulationReplay extends Application {
    SimReplayManger manger;
    private GraphicsContext g;
    final int WIDTH = 1200;
    final int HEIGHT = 800;
    boolean stop_animation;

    @Override
    public void init() throws Exception {
        stop_animation = false;
        manger = new SimReplayManger("simulationLog/simLog-2017-04-24 23:57:21.log");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Simulation Replay");
        //BorderPane root = new BorderPane();
        StackPane root = new StackPane();
        root.getChildren().add(createContent());
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }

    public Canvas createContent() {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        g = canvas.getGraphicsContext2D();

        AnimationTimer timer;
        timer = new AnimationTimer() {
            double lastUpdate = System.nanoTime();
            double sim_time = 0;

            @Override
            public void handle(long now) {
                double duration = (now - lastUpdate) / 1000000000.0;
                sim_time += duration;
                lastUpdate = now;
                drawObjects(manger.getBodies(), 40);

                //System.out.print(String.format("sim_time=%s duration=%s |", sim_time, duration));
                //System.out.print(manger.getBodies().get(0).getWorldCenter());
                //System.out.print("\n");

                stop_animation = manger.updateWorld(duration);
                if (stop_animation) {
                    this.stop();
                    System.out.print("Finished!\n");
                }
            }

            private void drawObjects(List<Body> bodies, double scale) {
                g.clearRect(0, 0, WIDTH, HEIGHT);
                for (Body body : bodies) {
                    Polygon polygon = (Polygon) body.getFixture(0).getShape();
                    Vector2 wordCenter = body.getWorldCenter();
                    Vector2[] vertices = polygon.getVertices();
                    int l = vertices.length;
                    double[] xPoints = new double[l];
                    double[] yPoints = new double[l];
                    for (int i = 0; i < l; i++) {
                        xPoints[i] = (vertices[i].x + wordCenter.x) * scale;
                        yPoints[i] = (vertices[i].y + wordCenter.y) * scale;
                    }
                    g.setFill(Color.GREEN);
                    g.fillPolygon(xPoints, yPoints, l);
                }
            }

        };
        timer.start();
        return canvas;
    }
}
