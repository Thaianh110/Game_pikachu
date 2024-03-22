package controller;

//import com.apple.eawt.Application;
import utils.Utils;
import view.MenuView;
import view.Pikachu;
import view.PlayGameView;
import view.SplashView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import static utils.Utils.MAP_COL;
import static utils.Utils.MAP_ROW;
import model.Matrix;
import model.JDBC;
import view.PauseMenuView;
import view.PauseMenuView.PauseMenuListener;
import view.ResultView;
import view.ResultView.ResultListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */
public class GameController extends JFrame {
    private SplashView splashView;
    private MenuView menuView;
    private PlayGameView playGameView;
    private PauseMenuView pauseMenuView;
    private ResultView resultview;
    private Matrix matrix;
    private Timer timer;
    private int countDown;
    private int score;  // luu score cong them 100 moi lan chon dung
    private int scoreSum; //luu score cua man choi truoc do, scoreHienTai = scoreSum + score;
    private int mapNumber;
    private int coupleDone;
    private ActionListener timeAction;

    /**
     *
     * @param title Hiá»ƒn thá»‹ tÃªn cá»­a sá»• game má»›i
     * @throws HeadlessException KhÃ´ng bÃ¡o lá»—i
     */
    public GameController(String title) throws HeadlessException {
        super(title);
        Image icon = (new ImageIcon(getClass().getResource("../resources/pika_icon.png"))).getImage();
        setIconImage(icon);
//        Application.getApplication().setDockIconImage(icon);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    protected void frameInit() {
        super.frameInit();

        //Khá»Ÿi táº¡o splash view
        this.splashView = new SplashView("../resources/Plash_backgroud.png");
        this.splashView.setSize(Utils.WINDOW_WIDTH, Utils.WINDOW_HEIGHT);
        
        
        //Khá»Ÿi táº¡o menu view
        this.menuView = new MenuView("../resources/menu_bg.png");
        this.menuView.setSize(Utils.WINDOW_WIDTH, Utils.WINDOW_HEIGHT);

        //Khá»Ÿi táº¡o mÃ n chÆ¡i
        this.playGameView = new PlayGameView(MAP_ROW, MAP_COL);
        this.playGameView.setSize(Utils.WINDOW_WIDTH, Utils.WINDOW_HEIGHT);
        // 
        this.pauseMenuView = new PauseMenuView("../resources/menu_bg.png");
        this.pauseMenuView.setSize(Utils.WINDOW_WIDTH, Utils.WINDOW_HEIGHT);
        
        this.resultview = new ResultView("../resources/menu_bg.png");
        this.resultview.setSize(Utils.WINDOW_WIDTH, Utils.WINDOW_HEIGHT);
        //Khá»Ÿi táº¡o ma tráº­n thuáº­t toÃ¡n
        this.matrix = new Matrix(MAP_ROW, MAP_COL);

        this.timeAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                --countDown;
                playGameView.updateProgress(countDown);
                playGameView.updateTimer("Time: " + countDown);
                if (countDown == 0) {
                    
                        timer.stop();
                        JOptionPane.showMessageDialog(null, "TIME OUT, GAME OVER!");
                    try{
                        updateResult();
                        playGameView.setVisible(false);
                        menuView.setVisible(true);
                    } catch (SQLException ex) {
                        Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            
        };

        this.timer = new Timer(1000, timeAction);

        this.splashView.setLoadingListener(new SplashView.OnLoadingListener() {
            @Override
            public void onStartLoading() {

            }
            @Override
            public void onLoading() {

            }

            @Override
            public void onStopLoading() {
                splashView.setVisible(false);
                menuView.setVisible(true);
            }
        });

        //
        menuView.setOnClickMenuListener(new MenuView.OnClickMenuListener() {
            @Override
            public void onNewGameClicked(int type) {
                menuView.setVisible(false);

                //Khá»Ÿi táº¡o mÃ n chÆ¡i má»›i
                playGameView.renderMap(matrix.renderMatrix());

                int i = (new Random()).nextInt(5);
                playGameView.setBackgroundImage("../resources/bg_"+i+".png");
                score = 0;
                scoreSum = 0;
                mapNumber = 1;
                coupleDone = 0;

                switch(type){
                    case MenuView.TYPE_EASY:
                        countDown = 120;
                        break;
                    case MenuView.TYPE_MEDIUM:
                        countDown = 100;
                        break;
                    case MenuView.TYPE_HARD:
                        countDown = 80;
                        break;
                    default:
                        break;
                }
                playGameView.updateMaxProgress(countDown);
                playGameView.updateScore("Score: "+score);
                playGameView.updateTimer("Time: "+countDown);
                playGameView.updateMapNum("Map: "+mapNumber);
                playGameView.setVisible(true);
                timer.start();
            }

            @Override
            public void onSettingClicked() {
                Utils.debug(getClass(), "setting");
            }

            @Override
            public void onQuitClicked() {
                dispose();
            }

            @Override
            public void onResultClicked() {
                menuView.setVisible(false);
                try {
                    resultview.loadTT();
                    resultview.setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
              
            }
            }
        });

        this.playGameView.setPlayGameListener(new PlayGameView.PlayGameListener() {
            @Override
            public void onReplayClicked() {
                playGameView.updateMap(matrix.renderMatrix());
                score = scoreSum;
                coupleDone = 0;
                countDown = playGameView.getMaxCountDown();
                playGameView.updateMaxProgress(countDown);
                playGameView.updateScore("Score: "+score);
                playGameView.updateTimer("Time: "+countDown);
                playGameView.updateMapNum("Map: "+mapNumber);
            }

            @Override
            public void onPauseClicked() {
                timer.stop();
                playGameView.setVisible(false);
                pauseMenuView.setVisible(true);
            }

            @Override
            public void onPikachuClicked(int clickCounter, Pikachu... pikachus) {
                if (clickCounter == 1) {
                    pikachus[0].drawBorder(Color.red);
                } else if (clickCounter == 2) {
                    pikachus[1].drawBorder(Color.red);
                    if (matrix.algorithm(pikachus[0], pikachus[1])) {

                        //áº¨n pikachu náº¿u chá»�n Ä‘Ãºng
                        matrix.setXY(pikachus[0], 0);
                        matrix.setXY(pikachus[1], 0);

                        pikachus[0].removeBorder();
                        pikachus[1].removeBorder();

                        pikachus[0].setVisible(false);
                        pikachus[1].setVisible(false);

                        //TÄƒng sá»‘ cáº·p chá»�n Ä‘Ãºng lÃªn 1
                        coupleDone++;

                        score += 10;

                        playGameView.updateScore("Score: " + score);

                        if (!matrix.canPlay() && coupleDone < (matrix.getRow()-2) * (matrix.getCol()-2) / 2){
                            timer.stop();
                            JOptionPane.showMessageDialog(null, "KhÃ´ng thá»ƒ chÆ¡i tiáº¿p!");
                            playGameView.setVisible(false);
                            menuView.setVisible(true);
                        }

                        if (coupleDone == (matrix.getRow()-2) * (matrix.getCol()-2) / 2) {
                            ++mapNumber;
                            if (mapNumber <= 3) {
                                score += 50;
                                scoreSum += score;
                                score = scoreSum;
                                countDown = playGameView.getMaxCountDown() - 10 * mapNumber;
                                coupleDone = 0;

                                playGameView.updateMaxProgress(countDown);
                                playGameView.updateMap(matrix.renderMatrix());
                                playGameView.updateTimer("Time: "+countDown);
                                playGameView.updateMapNum("Map: "+mapNumber);
                                playGameView.updateScore("Score: "+ score);
                            }else{
                                // TODO : chuc mung chien thang!
                                timer.stop();
                                JOptionPane.showMessageDialog(null, " CHUC MUNG WINNER !");
                                playGameView.setVisible(false);
                                menuView.setVisible(true);
                            }
                        }
                    } else {
                        pikachus[0].removeBorder();
                        pikachus[1].removeBorder();
                        playGameView.setCountClicked(0);
                    }
                }
            }
        });
        
        this.pauseMenuView.setPauseMenuListener(new PauseMenuListener(){
            @Override
            public void onContinueCliked() {
                pauseMenuView.setVisible(false);
                playGameView.setVisible(true);
                timer.start();
            }

            @Override
            public void onBackMenuClicked() {
                try {
                    pauseMenuView.setVisible(false);
                    updateResult();
                    // luu ma tran tro choi
                    menuView.setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onQuitClicked() {
                dispose();
            }
            
        });
        this.resultview.setresultListener(new ResultListener(){
            @Override
            public void onBackMenuClicked() {
                resultview.setVisible(false);
                menuView.setVisible(true);
            }
            
        });
        
        this.add(splashView, BorderLayout.CENTER);
        this.add(menuView, BorderLayout.CENTER);
        this.add(playGameView, BorderLayout.CENTER);
        this.add(pauseMenuView, BorderLayout.CENTER);
        this.add(resultview,BorderLayout.CENTER);
        
    }
     public int getScore() {
        return score;
    }
    

  public void updateResult() throws SQLException{
      Connection c = JDBC.getCon();
            String sql = "insert into Table_1 values(?)";
            PreparedStatement ps = c.prepareStatement(sql);
            String s;
            int i=getScore();
            s=String.valueOf(i);
            ps.setString(1, s);
            ps.executeUpdate();
            JDBC.closeCon(c);
  }

    public void start() {
        splashView.start();
        setVisible(true);
    }
}
