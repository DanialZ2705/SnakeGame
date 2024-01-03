package com.example.snakegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder, SurfaceHolder.Callback {

    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;

    //surface holder to draw snake on surface canvas
    private SurfaceHolder surfaceHolder;

    //snake moving position. value = RLTB
    //by default snake move to right first
    private String movingPosition = "right";

    private int score = 0;
    private static final int pointSize = 30;
    private static final int defaultTalePoints = 3;

    private static final int snakeColor = Color.YELLOW;

    private static final int snakeMovingSpeed = 800;

    private int positionX, positionY;

    //timer
    private Timer timer;

    private Canvas canvas = null;

    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the initial content view to start_game layout
        setContentView(R.layout.start_game);

        // Getting the start button from XML
        ImageView startButton = findViewById(R.id.startImage);

        // Set click listener for the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the main activity layout
                setContentView(R.layout.activity_game_page);

                // Initialize UI components from activity_main.xml
                surfaceView = findViewById(R.id.surfaceView);
                scoreTV = findViewById(R.id.scoreTV);
                final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
                final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);
                final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
                final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);

                // Add callback to surfaceView
                surfaceView.getHolder().addCallback(MainActivity.this);

                // Set click listeners for buttons
                topBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Update movingPosition to "top" when the top button is clicked
                        movingPosition = "top";
                        Log.d("UserInput", "Top button clicked. MovingPosition: " + movingPosition);
                    }
                });

                bottomBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Update movingPosition to "bottom" when the bottom button is clicked
                        movingPosition = "bottom";
                        Log.d("UserInput", "Bottom button clicked. MovingPosition: " + movingPosition);
                    }
                });

                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Update movingPosition to "right" when the right button is clicked
                        movingPosition = "right";
                        Log.d("UserInput", "Right button clicked. MovingPosition: " + movingPosition);
                    }
                });

                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Update movingPosition to "left" when the left button is clicked
                        movingPosition = "left";
                        Log.d("UserInput", "Left button clicked. MovingPosition: " + movingPosition);
                    }
                });
            }
        });
    }


    @Override
    public void addCallback(Callback callback) {

    }

    @Override
    public void removeCallback(Callback callback) {

    }

    @Override
    public boolean isCreating() {
        return false;
    }

    @Override
    public void setType(int i) {

    }

    @Override
    public void setFixedSize(int i, int i1) {

    }

    @Override
    public void setSizeFromLayout() {

    }

    @Override
    public void setFormat(int i) {

    }

    @Override
    public void setKeepScreenOn(boolean b) {

    }

    @Override
    public Canvas lockCanvas() {
        return null;
    }

    @Override
    public Canvas lockCanvas(Rect rect) {
        return null;
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {

    }

    @Override
    public Rect getSurfaceFrame() {
        return null;
    }

    @Override
    public Surface getSurface() {
        return null;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        // when surface is created, get surfaceHolder from it
        this.surfaceHolder = surfaceHolder;

        initiate();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
    private void initiate(){
        //clear points
        snakePointsList.clear();
        scoreTV.setText("0");
        score = 0;
        movingPosition = "right";
        int startPositionX = (pointSize) * defaultTalePoints;

        for(int i = 0; i < defaultTalePoints; i++){
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            startPositionX = startPositionX - (pointSize * 2);
        }

        //add random pointer/food
        addPoint();

        //snake start moving
        moveSnake();

    }

    private void addPoint(){

        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        //round off to even number
        if((randomXPosition % 2) != 0){
            randomXPosition = randomXPosition + 1;
        }
        if((randomYPosition % 2) != 0){
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;

    }
    private void moveSnake() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Getting head position
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                // Check if snake ate a point
                if (headPositionX == positionX && positionY == headPositionY) {
                    // Snake grow
                    growSnake();
                    addPoint();
                }

                // Move the snake's head based on the direction
                switch (movingPosition) {
                    case "right":
                        headPositionX += (pointSize * 2);
                        break;
                    case "left":
                        headPositionX -= (pointSize * 2);
                        break;
                    case "top":
                        headPositionY -= (pointSize * 2);
                        break;
                    case "bottom":
                        headPositionY += (pointSize * 2);
                        break;
                }

                // Check if game over (touch edge/eat itself)
                if (checkGameOver(headPositionX, headPositionY)) {
                    timer.purge();
                    timer.cancel();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Your Score + " + score);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Restart game
                            initiate();
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });

                } else {
                    // Add the new head position to the beginning of the list
                    snakePointsList.add(0, new SnakePoints(headPositionX, headPositionY));

                    // Remove the last point to maintain the snake's length
                    if (snakePointsList.size() > score + defaultTalePoints) {
                        snakePointsList.remove(snakePointsList.size() - 1);
                    }

                    // Lock canvas on surfaceHolder to draw on it
                    canvas = surfaceHolder.lockCanvas();

                    // Clear canvas with white color
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    // Draw snake's body
                    for (SnakePoints point : snakePointsList) {
                        canvas.drawCircle(point.getPositionX(), point.getPositionY(), pointSize, createPointColor());
                    }

                    // Draw random point to be eaten
                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());

                    // Unlock canvas to draw on surfaceView
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }


    private void growSnake(){
        SnakePoints snakePoints = new SnakePoints(0,0);
        snakePointsList.add(snakePoints);
        score++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headPositionX, int headPositionY) {
        boolean gameOver = false;

        if ((headPositionX < 0) || (headPositionY < 0) ||
                (headPositionX >= surfaceView.getWidth()) || (headPositionY >= surfaceView.getHeight())) {
            gameOver = true;
        } else {
            for (int i = 1; i < snakePointsList.size(); i++) { // Start from index 1, not 0
                if (headPositionX == snakePointsList.get(i).getPositionX()
                        && headPositionY == snakePointsList.get(i).getPositionY()) {
                    gameOver = true;
                    break;
                }
            }
        }

        return gameOver;
    }


    private Paint createPointColor(){
        if(pointColor == null){
            pointColor = new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true); //smoothness

            return pointColor;
        }
        return pointColor;
    }

}