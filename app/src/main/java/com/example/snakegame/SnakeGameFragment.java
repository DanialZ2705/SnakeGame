//package com.example.snakegame;
//
//import android.content.DialogInterface;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class SnakeGameFragment extends Fragment implements SurfaceHolder.Callback {
//
//    private int headPositionX, headPositionY;
//    private final List<SnakePoints> snakePointsList = new ArrayList<>();
//    private SurfaceView surfaceView;
//    private TextView scoreTV;
//
//    // surface holder to draw snake on surface canvas
//    private SurfaceHolder surfaceHolder;
//
//    // snake moving position. value = RLTB
//    // by default snake moves to the right first
//    private String movingPosition = "right";
//
//    private int score = 0;
//    private static final int pointSize = 30;
//    private static final int defaultTailPoints = 3;
//
//    private static final int snakeColor = Color.YELLOW;
//
//    private static final int snakeMovingSpeed = 800;
//
//    private int positionX, positionY;
//
//    // timer
//    private Timer timer;
//
//    private Canvas canvas = null;
//
//    private Paint pointColor = null;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.start_game, container, false);
//
//        // Getting the start button from XML
//        ImageView startButton = view.findViewById(R.id.startImage);
//        surfaceView = view.findViewById(R.id.surfaceView); // Assuming your SurfaceView has the id "surfaceView"
//        surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.addCallback(this);
//
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startGame();
//            }
//        });
//
//        return view;
//    }
//
//    @Override
//    public void surfaceCreated(@NonNull SurfaceHolder holder) {
//        // Initialize your canvas-related variables here
//        // This method is called when the SurfaceView is created
//        surfaceHolder = holder;
//        initiate();
//    }
//
//    @Override
//    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
//        // Adjust your canvas-related variables if needed
//        // This method is called when the size or format of the SurfaceView changes
//    }
//
//    @Override
//    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
//        // Release any resources related to the canvas
//        // This method is called when the SurfaceView is destroyed
//    }
//
//    void startGame() {
//        initiate();
//
//        // Check if the fragment is already added to avoid adding it multiple times
//        if (getFragmentManager().findFragmentByTag("SnakeGameFragmentTag") == null) {
//            // Replace the current fragment with SnakeGameFragment
//            FragmentTransaction transaction = requireFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragmentContainer, new SnakeGameFragment(), "SnakeGameFragmentTag");
//            transaction.addToBackStack(null);  // Optional: Add the transaction to the back stack
//            transaction.commit();
//        }
//
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                moveSnake();
//                drawGame();
//
//                if (checkGameOver(headPositionX, headPositionY)) {
//                    stopGame();
//                    showGameOverDialog();
//                }
//            }
//        }, 1000 - getSnakeMovingSpeed(), 1000 - getSnakeMovingSpeed());
//    }
//
//
//    private void stopGame() {
//        if (timer != null) {
//            timer.cancel();
//            timer.purge();
//        }
//    }
//
//    private void showGameOverDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setMessage("Your Score: " + score);
//        builder.setTitle("Game Over");
//        builder.setCancelable(false);
//        builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                startGame();
//            }
//        });
//
//        builder.show();
//    }
//
//    private void initiate() {
//        // Clear points
//        snakePointsList.clear();
//        score = 0;
//        int startPositionX = (pointSize) * defaultTailPoints;
//
//        for (int i = 0; i < defaultTailPoints; i++) {
//            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
//            snakePointsList.add(snakePoints);
//            startPositionX = startPositionX - (pointSize * 2);
//        }
//
//        // Add random pointer/food
//        addPoint();
//    }
//
//    private void addPoint() {
//        int surfaceWidth = requireView().getWidth() - (pointSize * 2);
//        int surfaceHeight = requireView().getHeight() - (pointSize * 2);
//
//        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
//        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);
//
//        // Round off to even number
//        if ((randomXPosition % 2) != 0) {
//            randomXPosition = randomXPosition + 1;
//        }
//        if ((randomYPosition % 2) != 0) {
//            randomYPosition = randomYPosition + 1;
//        }
//
//        positionX = (pointSize * randomXPosition) + pointSize;
//        positionY = (pointSize * randomYPosition) + pointSize;
//    }
//
//    private void moveSnake() {
//        // Getting head position
//        int headPositionX = snakePointsList.get(0).getPositionX();
//        int headPositionY = snakePointsList.get(0).getPositionY();
//
//        // Check if snake ate a point
//        if (headPositionX == positionX && positionY == headPositionY) {
//            // Snake grows
//            growSnake();
//            addPoint();
//        }
//
//        // Move the snake's head based on the direction
//        switch (movingPosition) {
//            case "right":
//                headPositionX += (pointSize * 2);
//                break;
//            case "left":
//                headPositionX -= (pointSize * 2);
//                break;
//            case "top":
//                headPositionY -= (pointSize * 2);
//                break;
//            case "bottom":
//                headPositionY += (pointSize * 2);
//                break;
//        }
//
//        // Check if game over (touch edge/eat itself)
//        if (checkGameOver(headPositionX, headPositionY)) {
//            timer.cancel();
//            timer.purge();
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//            builder.setMessage("Your Score + " + score);
//            builder.setTitle("Game Over");
//            builder.setCancelable(false);
//            builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    // Restart game
//                    initiate();
//                    startGame();
//                }
//            });
//
//            requireActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    builder.show();
//                }
//            });
//
//        } else {
//            // Add the new head position to the beginning of the list
//            snakePointsList.add(0, new SnakePoints(headPositionX, headPositionY));
//
//            // Remove the last point to maintain the snake's length
//            if (snakePointsList.size() > score + defaultTailPoints) {
//                snakePointsList.remove(snakePointsList.size() - 1);
//            }
//
//            // Lock canvas on surfaceHolder to draw on it
//            canvas = surfaceHolder.lockCanvas();
//
//            // Clear canvas with white color
//            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
//
//            // Draw snake's body
//            for (SnakePoints point : snakePointsList) {
//                canvas.drawCircle(point.getPositionX(), point.getPositionY(), pointSize, createPointColor());
//            }
//
//            // Draw random point to be eaten
//            canvas.drawCircle(positionX, positionY, pointSize, createPointColor());
//
//            // Unlock canvas to draw on surfaceView
//            surfaceHolder.unlockCanvasAndPost(canvas);
//        }
//    }
//
//    private void drawGame() {
//        // Ensure the surface is valid
//        if (surfaceHolder.getSurface().isValid()) {
//            // Lock the canvas
//            Canvas canvas = surfaceHolder.lockCanvas();
//
//            // Clear the canvas
//            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
//
//            // Draw snake's body
//            for (SnakePoints point : snakePointsList) {
//                canvas.drawCircle(point.getPositionX(), point.getPositionY(), pointSize, createPointColor());
//            }
//
//            // Draw random point to be eaten
//            canvas.drawCircle(positionX, positionY, pointSize, createPointColor());
//
//            // Unlock the canvas
//            surfaceHolder.unlockCanvasAndPost(canvas);
//        }
//    }
//
//
//    private boolean checkGameOver(int headPositionX, int headPositionY) {
//        boolean gameOver = false;
//
//        if ((headPositionX < 0) || (headPositionY < 0) ||
//                (headPositionX >= surfaceView.getWidth()) || (headPositionY >= surfaceView.getHeight())) {
//            gameOver = true;
//        } else {
//            for (int i = 1; i < snakePointsList.size(); i++) { // Start from index 1, not 0
//                if (headPositionX == snakePointsList.get(i).getPositionX()
//                        && headPositionY == snakePointsList.get(i).getPositionY()) {
//                    gameOver = true;
//                    break;
//                }
//            }
//        }
//
//        return gameOver;
//    }
//
//    private int getSnakeMovingSpeed() {
//        // Replace this with your desired speed logic
//        return 800;
//    }
//
//    private Paint createPointColor() {
//        if (pointColor == null) {
//            pointColor = new Paint();
//            pointColor.setColor(snakeColor);
//            pointColor.setStyle(Paint.Style.FILL);
//            pointColor.setAntiAlias(true); // smoothness
//
//            return pointColor;
//        }
//        return pointColor;
//    }
//
//    private void growSnake() {
//        SnakePoints snakePoints = new SnakePoints(0, 0);
//        snakePointsList.add(snakePoints);
//        score++;
//
//        requireActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                scoreTV.setText(String.valueOf(score));
//            }
//        });
//    }
//}
