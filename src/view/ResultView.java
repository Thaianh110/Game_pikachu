/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import model.JDBC;
import utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.GameController;
import java.awt.Color;
import java.awt.Font;
import static utils.Utils.BT_BACK_MENU;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;

/**
 *
 * @author M S I
 */
public class ResultView extends JpanelBackground implements ActionListener{
    private JButton BACK_MENU;
    private JScrollPane jscresult;
    private JTextArea result;
     private GroupLayout layout;
     private JPanel p;
    private ResultListener resultlistener;

     public ResultView() {
        super();
    }

    public ResultView(String imagePath) {
        super(imagePath);
        setVisible(false);
        initUI();
    }

    private void initUI() {
        super.updateUI();
       
        layout = new GroupLayout(this);
        result = new JTextArea();
        result.setSize(90, 90);
        result.setBackground(Color.cyan);
        
     
        Font fontcustom = new Font("Arial", Font.BOLD,16);
        result.setFont(fontcustom);
        
        
        jscresult=new JScrollPane();
        jscresult.setViewportView(result);
        
        BACK_MENU = new JButton(BT_BACK_MENU);
        BACK_MENU.setSize(100,30);
        BACK_MENU.addActionListener(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                    .addContainerGap(180, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jscresult, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE)
                    .addComponent(BACK_MENU, GroupLayout.PREFERRED_SIZE, 350, GroupLayout.PREFERRED_SIZE)
                    )
                    .addGap(180, 180, 180))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12 , 12)
                        .addComponent(jscresult, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5) 
                        .addComponent(BACK_MENU, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addContainerGap(141, Short.MAX_VALUE))
        );

        add(jscresult);
        add(BACK_MENU);
       
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
         Utils.debug(getClass(),e.getActionCommand());
        if (resultlistener!=null){
            switch (e.getActionCommand()){
                case BT_BACK_MENU :resultlistener.onBackMenuClicked(); break;
                default:break;
            }
            
        
    }
    }
    
     public void setresultListener(ResultListener resultlistener) {
        this.resultlistener = resultlistener;
    }
    public interface ResultListener{
        void onBackMenuClicked();
       
    }
    public void loadTT() throws SQLException{
        Connection c = JDBC.getCon();
        String sql = "select * from Table_1 where score > 0 order by score desc";
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);
        String t="";
        String x="_".repeat(38);
        while(rs.next()){
            int score = rs.getInt("score");
            t+= "\t         "+ score + "\n" + x +"\n";
            
        }
        result.setText(t); 
        JDBC.closeCon(c);
    }
    
    
}
