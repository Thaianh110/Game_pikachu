package view;

import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class JpanelBackground extends JPanel{
    protected Image backgroundImage = null;

    public JpanelBackground(){
        this(null);
    }

    public JpanelBackground(String imagePath){
        setOpaque(false);
        if(imagePath!=null)
            this.backgroundImage = new ImageIcon( getClass().getResource(imagePath) ).getImage();
        else Utils.debug(getClass(),"Can't load null Image!");
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(backgroundImage!=null){
            int height,width;
            height = this.getSize().height;
            width = this.getSize().width;
            g.drawImage(backgroundImage, 0, 0, width,height,this);
        }
    }

    // thiet lap background bằng ảnh
    public void setBackgroundImage(String imagePath){
        if(imagePath!=null)
            this.backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        else Utils.debug(getClass(),"Can't load null Image!");
        this.repaint();
    }
}
