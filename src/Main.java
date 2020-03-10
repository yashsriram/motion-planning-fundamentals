import camera.QueasyCam;
import math.Vec3;
import physical.Graph;
import physical.SphericalAgent;
import physical.SphericalObstacle;
import physical.Vertex;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final int SIDE = 100;

    final Vec3 startPosition = Vec3.of(0, SIDE * (9f / 10), SIDE * (-9f / 10));
    final Vec3 finishPosition = Vec3.of(0, SIDE * (-9f / 10), SIDE * (9f / 10));
    SphericalAgent sphericalAgent;
    SphericalObstacle sphericalObstacle;
    Graph graph;

    QueasyCam cam;

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public void setup() {
        surface.setTitle("Processing");
        colorMode(RGB, 1.0f);
        rectMode(CENTER);
        noStroke();

        cam = new QueasyCam(this);
        sphericalObstacle = new SphericalObstacle(
                this,
                Vec3.of(0, 0, 0),
                SIDE * (2f / 20),
                Vec3.of(1, 0, 0)
        );
        sphericalAgent = new SphericalAgent(
                this,
                startPosition,
                20f,
                SIDE * (0.5f / 20),
                Vec3.of(1)
        );
        // vertex sampling
        List<Vec3> vertexPositions = new ArrayList<>();
        for (int i = 0; i < 200; ++i) {
            vertexPositions.add(Vec3.of(0, random(-SIDE, SIDE), random(-SIDE, SIDE)));
        }
        graph = new Graph(this, startPosition, finishPosition);
        graph.generateVertices(vertexPositions, sphericalAgent, sphericalObstacle);
        graph.generateAdjacencies(80, sphericalAgent, sphericalObstacle);
    }

    public void draw() {
        if (keyPressed) {
            if (keyCode == RIGHT) {
                sphericalAgent.stepForward();
            }
            if (keyCode == LEFT) {
                sphericalAgent.stepBackward();
            }
        }
        long start = millis();
        // update
        sphericalAgent.update(0.1f);
        long update = millis();
        // draw
        background(0);
        // agent
        sphericalAgent.draw();
        // obstacle
        sphericalObstacle.draw();
        // graph
        graph.draw();
        long draw = millis();

        surface.setTitle("Processing - FPS: " + Math.round(frameRate) + " Update: " + (update - start) + "ms Draw " + (draw - update) + "ms");
    }

    public void keyPressed() {
        if (key == 'h') {
            sphericalObstacle.isDrawn = !sphericalObstacle.isDrawn;
        }
        if (key == 'j') {
            Vertex.DRAW_EDGES = !Vertex.DRAW_EDGES;
        }
        if (key == 'p') {
            sphericalAgent.isPaused = !sphericalAgent.isPaused;
        }
        if (key == '1') {
            sphericalAgent.setPath(graph.dfs());
        }
        if (key == '2') {
            sphericalAgent.setPath(graph.bfs());
        }
        if (key == '3') {
            sphericalAgent.setPath(graph.ucs());
        }
        if (key == '4') {
            sphericalAgent.setPath(graph.aStar());
        }
        if (key == '5') {
            sphericalAgent.setPath(graph.weightedAStar(1.5f));
        }
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"Main"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
