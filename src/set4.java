import java.awt.*; 
import javax.swing.*;
import javax.swing.Box;
import javax.swing.ImageIcon;

class menu1 extends JFrame
{
JMenuBar menubar;
JMenu file,ele,color;
JMenuItem m1,m2,m3; 
JRadioButtonMenuItem  jrb1,jrb2,jrb3;

//constructor... designing menu
menu1(String title) 
  {
    super( title );                   
    setLayout( new FlowLayout() );      
    menubar = new JMenuBar();
    
   JMenu file = new JMenu("File");
   JMenu ele=new JMenu("Elements");
   JMenu color= new JMenu("Colors");
JMenu help= new JMenu("Help");
   
   menubar.add(file);
   menubar.add(ele);
   menubar.add(color);
menubar.add(Box.createHorizontalGlue());
menubar.add(help);

   m1 = new JMenuItem("New");
   m2=new JMenuItem("Open");
   m3=new JMenuItem("Close");

   file.add(m1);
   file.add(m2);
   file.add(m3);

// for radiobuttons

	jrb1=new JRadioButtonMenuItem("Red");
	jrb2=new JRadioButtonMenuItem("Green");
	jrb3=new JRadioButtonMenuItem("Yellow");

	ButtonGroup bt1= new ButtonGroup();
	bt1.add(jrb1);
	bt1.add(jrb2);
	bt1.add(jrb3);

	color.add(jrb1);
	color.add(jrb2);
	color.add(jrb3);

 	setJMenuBar(menubar); 

// for toolbar

JToolBar toolbar = new JToolBar();

ImageIcon ic1= new ImageIcon("new1.png");
ImageIcon ic2= new ImageIcon("open1.png");
ImageIcon ic3= new ImageIcon("close1.png");
ImageIcon ic4= new ImageIcon("red.jpg");
ImageIcon ic5= new ImageIcon("green.jpg");
ImageIcon ic6= new ImageIcon("yellow.jpg");

Image img = ic1.getImage() ;  
Image newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
ic1= new ImageIcon( newimg );
JButton newButton = new JButton(ic1);
toolbar.add(newButton);

img = ic2.getImage() ;  
newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
ic2= new ImageIcon( newimg );
JButton openButton = new JButton(ic2);
toolbar.add(openButton);

img = ic3.getImage() ;  
newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
ic3= new ImageIcon( newimg );
JButton exitButton = new JButton(ic3);
toolbar.add(exitButton);

img = ic4.getImage() ;  
newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
ic4= new ImageIcon( newimg );
JButton redButton = new JButton(ic4);
toolbar.add(redButton);

img = ic5.getImage() ;  
newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
ic5= new ImageIcon( newimg );
JButton greenButton = new JButton(ic5);
toolbar.add(greenButton);

img = ic6.getImage() ;  
newimg = img.getScaledInstance( 20, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
ic6= new ImageIcon( newimg );
JButton yellowButton = new JButton(ic6);
toolbar.add(yellowButton);

add(toolbar);
              
setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );   
  }
}

public class set4
{
  public static void main ( String[] args )
  {
   menu1 frm = new menu1("Menu Demo");

    frm.setSize(300, 300 );     
    frm.setVisible( true );   

  }
}