/**
 * Created by jinfenglin on 3/29/17.
 */

import javafx.application.Application;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Simulator extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create a Box
        Box box = new Box(100, 100, 100);
        box.setCullFace(CullFace.NONE);
        box.setTranslateX(250);
        box.setTranslateY(100);
        box.setTranslateZ(400);

        // Create a Camera to view the 3D Shapes
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(100);
        camera.setTranslateY(-50);
        camera.setTranslateZ(300);

        // Add a Rotation Animation to the Camera
        RotateTransition rotation = new RotateTransition(Duration.seconds(2), camera);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setFromAngle(0);
        rotation.setToAngle(90);
        rotation.setAutoReverse(true);
        rotation.setAxis(Rotate.X_AXIS);
        rotation.play();

        // Create a red Light
        PointLight redLight = new PointLight();
        redLight.setColor(Color.RED);
        redLight.setTranslateX(250);
        redLight.setTranslateY(-100);
        redLight.setTranslateZ(250);

        // Create a green Light
        PointLight greenLight = new PointLight();
        greenLight.setColor(Color.GREEN);
        greenLight.setTranslateX(250);
        greenLight.setTranslateY(300);
        greenLight.setTranslateZ(300);

        // Add the Box and the Lights to the Group
        Group root = new Group(box, redLight, greenLight);
        // Enable Rotation for the Group
        root.setRotationAxis(Rotate.X_AXIS);
        root.setRotate(30);

        // Create a Scene with depth buffer enabled
        Scene scene = new Scene(root, 300, 400, true);
        // Add the Camera to the Scene
        scene.setCamera(camera);

        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("An Example with a Camera");
        // Display the Stage
        stage.show();
    }

}
