package com.shpp.p2p.cs.lhryshchenko.assignment4;
import acm.graphics.*;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;
import java.awt.*;
import java.awt.event.MouseEvent;

/** Classic arcade game "Breakout" */
public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 403;
    public static final int APPLICATION_HEIGHT = 500;

    /**
     * The amount of time to pause between frames (48fps).
     */
    private static final double PAUSE_TIME = 1000.0 / 48;

    /**
     * Dimensions of the paddle
     */
    private static final double PADDLE_WIDTH = 60;
    private static final double PADDLE_HEIGHT = 10;
    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 50;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;


    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH = (APPLICATION_WIDTH - (NBRICKS_PER_ROW - 1)
            * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;


    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    /* Counter for number of turns */
    private int nturns = NTURNS + 1;
    /* Counter for number of brick */
    private int countOfBrick = NBRICKS_PER_ROW * NBRICK_ROWS;

    @Override
    public void run() {
        startGame();
    }

    /**
     * Actions which prepares all game elements.
     */
    private void startGame() {
        addMouseListeners();
        builtWall();
        addPaddle();
        createBall();
        while (true) {
            addNumberOfTurns(nturns);
            newTurn(paddle, ball);
            if (nturns == 0 || countOfBrick == 0) break;
            waitForClick();
            moveBall(ball);
        }
        removeAll();
        if (countOfBrick == 0) {
            labelWin();
            labelRetry();

        } else {
            labelLouse();
            labelRetry();
        }
        /* After click, you can start another game*/
        waitForClick();
        removeAll();
        nturns = NTURNS + 1;
        countOfBrick = NBRICKS_PER_ROW * NBRICK_ROWS;
        startGame();
    }
/** Method that sets a new try */
    private void newTurn(GRect paddle, GOval ball) {
        ball.setLocation(getWidth() / 2. - BALL_RADIUS, getHeight() / 2. - BALL_RADIUS);
        paddle.setLocation((getWidth() - PADDLE_WIDTH) / 2.0, getHeight() + PADDLE_Y_OFFSET);
        nturns--;
    }

    /* Ball velocity */
    RandomGenerator rand = RandomGenerator.getInstance();

    /**
     * Method that include paddle collision handling,
     * collision with a brick and as a result movement of ball changes.
     */
    public void moveBall(GOval ball) {
        double vx = rand.nextDouble(1.0, 3.0);
        double vy = 4;
        if (rand.nextBoolean(0.5)) vx = -vx;
        while (nturns != 0 && countOfBrick != 0) {
            /* Move the ball by the current velocity. */
            ball.move(vx, vy);
            if (ballBehindLeft(ball) || ballBehindRight(ball)) vx = -vx;
            if (ballBehindCeiling(ball)) vy = -vy;
            if (ballBehindFloor(ball)) {
                remove(numOfTurns);
                break;
            }
            /* Get a colliding object calls collider */
            GObject collider = getCollidingObject();
            /* Ball jumps off the paddle */
            if (collider == paddle) {
                vy = -vy;
            }
            /* Ball crushes the brick and jump off it */
            if (collider != paddle && collider != numOfTurns && collider != null) {
                countOfBrick--;
                remove(collider);//update num of lives
                vy = -vy * 1.01;
                if (rand.nextBoolean(0.5)) vx = rand.nextDouble(1.0, 3.0);
            }
            pause(PAUSE_TIME);
        }
    }

    /**
     * Catch and analyze a colliding object .
     *
     * @return GObject which catch in points.
     */
    public GObject getCollidingObject() {
//Scanning points near paddles corners check is it ball
        if (getElementAt(ball.getX() + BALL_RADIUS * 2 + 5, ball.getY() + BALL_RADIUS) != null)
            return getElementAt(ball.getX() + BALL_RADIUS * 2 + 5, ball.getY() + BALL_RADIUS);

        if (getElementAt(ball.getX() - 5, ball.getY() + BALL_RADIUS) != null)
            return getElementAt(ball.getX() - 5, ball.getY() + BALL_RADIUS);

        if (getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 5) != null)
            return getElementAt(ball.getX() + BALL_RADIUS, ball.getY() - 5);

        if (getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2 + 5) != null)
            return getElementAt(ball.getX() + BALL_RADIUS, ball.getY() + BALL_RADIUS * 2 + 5);
        else return null;
    }

    /**
     * Determines whether the ball touched the walls.
     *
     * @param ball The ball to test.
     * @return Whether it's touch the wall.
     */
    public boolean ballBehindRight(GOval ball) {
        return ball.getX() + ball.getWidth() >= getWidth();
    }

    public boolean ballBehindLeft(GOval ball) {
        return ball.getX() <= 0;
    }

    public boolean ballBehindCeiling(GOval ball) {
        return ball.getY() < 0;
    }

    public boolean ballBehindFloor(GOval ball) {
        return ball.getY() >= getHeight();
    }

    /**
     * Method create rows of bricks (NBRICK_ROWS x NBRICKS_PER_ROW)
     * Each two rows has a new color in the sequence: RED, ORANGE, YELLOW, GREEN, CYAN.
     */
    public void builtWall() {
        Color color;
        for (int i = 0; i < NBRICKS_PER_ROW; i++) {
            for (int j = 0; j < NBRICK_ROWS; j++) {
                if (j < 2) color = Color.red;
                else if (j < 4) color = Color.orange;
                else if (j < 6) color = Color.yellow;
                else if (j < 8) color = Color.green;
                else color = Color.blue;
                buildBrick(i, j, color);
            }
        }
    }

    /**
     * Method create one brick
     */
    private void buildBrick(int i, int j, Color color) {
        double x = getWidth() / 2. - (NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP) / 2.) + BRICK_SEP / 2.;
        GRect brick = new GRect(i * (BRICK_WIDTH + BRICK_SEP) + x,
                j * (BRICK_HEIGHT + BRICK_SEP) + BRICK_SEP + BRICK_Y_OFFSET,
                BRICK_WIDTH, BRICK_HEIGHT);
        brick.setFilled(true);
        brick.setColor(color);
        add(brick);
    }

    /**
     * Method which overrides  mouseMoved. The paddle is controlled by the mouse,
     * so whenever the user does not move the mouse,
     * the paddle will move with the cursor, while it is centered relative to the cursor.
     * The paddle always has the same coordinate along the Y axis, it never moves up and down.
     * When the user takes the mouse out of the screen, the paddle remains completely on the screen.
     */
    public void mouseMoved(MouseEvent mouseEvent) {
        double y_p = getHeight() - PADDLE_Y_OFFSET;

        paddle.setLocation(mouseEvent.getX() - PADDLE_WIDTH / 2, y_p);
        if (mouseEvent.getX() > APPLICATION_WIDTH - PADDLE_WIDTH / 2)
            paddle.setLocation(getWidth() - PADDLE_WIDTH, y_p);
        else if (mouseEvent.getX() < PADDLE_WIDTH / 2)
            paddle.setLocation(0, y_p);
    }

    /**
     * Create a paddle
     */
    GRect paddle = null;// used to be able to change  anywhere in the class

    public void addPaddle() {
        paddle = new GRect((getWidth() - PADDLE_WIDTH) / 2.0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setColor(Color.BLACK);
        add(paddle);
    }

    /**
     * Create a ball
     */
    GOval ball = null; // used to be able to change  anywhere in the class
    public void createBall() {
        ball = new GOval(getWidth() / 2. - BALL_RADIUS, getHeight() / 2. - BALL_RADIUS, BALL_RADIUS * 2.0, BALL_RADIUS * 2.0);
        ball.setFilled(true);
        ball.setColor(Color.BLACK);
        add(ball);
    }

    /**
     * Method that  print num of rest lives label.
     */
    GLabel numOfTurns;// used to be able to change  anywhere in the class
    public void addNumberOfTurns(int nturns) {
        int nturn = nturns - 1;
        String num = nturn + " lives left";
        numOfTurns = new GLabel(num);
        numOfTurns.setFont("Serif-20");
        numOfTurns.setColor(Color.black);
        numOfTurns.setLocation(getWidth() / 2. - numOfTurns.getWidth() / 2, getHeight() - numOfTurns.getDescent());
        add(numOfTurns);
    }

    /**
     * Method that  print victory label if all bricks are destroyed.
     */

    public void labelWin() {
        GLabel win = new GLabel("You win!");
        win.setFont("Serif-50");
        win.setColor(Color.green);
        add(win, getWidth() / 2. - win.getWidth() / 2, getHeight() / 2. - win.getDescent());

    }

    /**
     * Method that  print Louse label if all bricks are destroyed.
     */

    public void labelLouse() {
        GLabel ween = new GLabel("You louse!");
        ween.setFont("Serif-50");
        ween.setColor(Color.red);
        add(ween, getWidth() / 2. - ween.getWidth() / 2, getHeight() / 2. - ween.getDescent() * 2);

    }
    /**
     * Method that  print Retry label if the game over.
     */
    public void labelRetry() {
        GLabel retry = new GLabel("Retry!");
        retry.setFont("Serif-30");
        retry.setColor(Color.black);
        add(retry, getWidth() / 2. - retry.getWidth() / 2, getHeight() - getHeight() / 3.);

    }
}
