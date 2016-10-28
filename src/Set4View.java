import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.util.Observer;                  
import java.util.Observable;                  
import java.awt.Cursor;
import java.awt.Graphics;                                
import java.awt.Graphics2D;
import java.awt.Point; 
import java.awt.Rectangle;                               
import java.awt.Component;                               
import java.awt.Font;
                               
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.PrinterException;

import java.awt.geom.Line2D;    
import java.awt.event.MouseEvent;    
import java.awt.event.ActionEvent;    
import java.awt.event.ActionListener;    
import javax.swing.event.MouseInputAdapter;    
import static Constants.Set4Constants.*;

class Set4View extends JComponent implements Observer, ActionListener, Printable, Pageable {
  public Set4View(Set4 theApp) 
  {
	      this.theApp = theApp;
	      
    MouseHandler handler = new MouseHandler();        // create the mouse listener
    addMouseListener(handler);                        // Listen for button events
    addMouseMotionListener(handler);                  // Listen for motion events

    // Add the pop-up menu items
    moveItem = elementPopup.add(new JMenuItem("Move"));
    deleteItem = elementPopup.add(new JMenuItem("Delete"));
    rotateItem = elementPopup.add(new JMenuItem("Rotate"));
    sendToBackItem = elementPopup.add(new JMenuItem("Send-to-back"));

    // Add the menu item listeners
    moveItem.addActionListener(this);
    deleteItem.addActionListener(this);
    rotateItem.addActionListener(this);
    sendToBackItem.addActionListener(this);
   
  }

    
  public void show(String msg)
  { /*
	 switch(mode)
	 {case NORMAL :
	  
	  theApp.getWindow().SetStatusText("NORMAL");break;
	 case MOVE :
		  
		  theApp.getWindow().SetStatusText("MOVE");break;
		  
	 case RESIZE :
		  
		  theApp.getWindow().SetStatusText("RESIZE");break;
	 case ROTATE:
	 
		  
		  theApp.getWindow().SetStatusText("ROTATE");break;
	
	 default:
		theApp.getWindow().SetStatusText("NULL");break;
	  
	 }
	 */
	  
	  theApp.getWindow().SetStatusText(msg);
	  
  }
  
  // Method to return page count - always two pages
  public int getNumberOfPages() {
    return 2;
  }

  // Method to return the Printable object that will render the page
  public Printable getPrintable(int pageIndex) {
    if(pageIndex == 0) {                         // For the first page
      return new Set4CoverPage(theApp);        // return the cover page
    } else {
      return this;                               
    }
  }

  // Method to return a PageFormat object to suit the page
  public PageFormat getPageFormat(int pageIndex) {
    if(pageIndex==0) {               // If it's the cover page...
                                     // ...make the margins twice the size
      // Create a duplicate of the current page format
      PageFormat pageFormat = (PageFormat)(theApp.getWindow().getPageFormat().clone());
      Paper paper = pageFormat.getPaper();

      // Get top and left margins - x & y coordinates of top-left corner
      // of imageable area are the left & top margins  
      double leftMargin = paper.getImageableX();
      double topMargin = paper.getImageableY();

      // Get right and bottom margins
      double rightMargin = paper.getWidth()-paper.getImageableWidth()-leftMargin;
      double bottomMargin = paper.getHeight()-paper.getImageableHeight()-topMargin;

      // Double the margin sizes
      leftMargin *= 2.0;
      rightMargin *= 2.0;
      topMargin *= 2.0;
      bottomMargin *= 2.0;

      // Set new printable area for the paper
      paper.setImageableArea(leftMargin, topMargin,
                           paper.getWidth()-leftMargin-rightMargin,
                           paper.getHeight()-topMargin-bottomMargin);

      pageFormat.setPaper(paper);                // Restore the paper
      pageFormat.setOrientation(PageFormat.LANDSCAPE);      
      return pageFormat;                             // Return the page format
    }
    // For pages after the first, use the object from the app window
    return theApp.getWindow().getPageFormat();
  }

  // Method to print the sketch
  public int print(Graphics g,              // Graphics context for printing
                   PageFormat pageFormat,   // The page format
                   int pageIndex)           // Index number of current page
             throws PrinterException {
    Graphics2D g2D = (Graphics2D) g;
    // Get sketch bounds
    Rectangle rect = theApp.getModel().getModelExtent();

    // Calculate the scale to fit sketch to page
    double scaleX = pageFormat.getImageableWidth()/rect.width;
    double scaleY = pageFormat.getImageableHeight()/rect.height;

    // Get minimum scale factor
    double scale = Math.min(scaleX, scaleY);  

    // Move origin to page printing area corner
    g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

    g2D.scale(scale, scale);              // Apply scale factor

    g2D.translate(-rect.x, -rect.y);      // Move origin to rect top left

    paint(g2D);                           // Draw the sketch
    return PAGE_EXISTS;
  }

  // Method called by Observable object when it changes
  public void update(Observable o, Object rectangle) {
    if(rectangle != null & rectangle instanceof Rectangle) {
      repaint((Rectangle)rectangle);
    } else {
      repaint();
    }
  }

  // Handle context menu events
  public void actionPerformed(ActionEvent e ) {
    Object source = e.getSource();
    
    selectedElement = highlightElement;
    if(source == moveItem) {
      mode = MOVE;
      
    } else if(source == deleteItem) {
      if(highlightElement != null) {                    // If there's an element
        theApp.getModel().remove(highlightElement);     // then remove it
        selectedElement = highlightElement=null;                        // Remove the reference
      }

    } else if(source == rotateItem) {
      mode = ROTATE;
      selectedElement = highlightElement;
    } else if(source == sendToBackItem) {
      if(highlightElement != null) {
        theApp.getModel().remove(highlightElement);
        theApp.getModel().add(highlightElement);
        highlightElement.setHighlighted(false);
        highlightElement = null;
        repaint();
      }
    }
  }
  
  
  public void DrawSelectionBorder(Graphics2D g2)
  {  Rectangle out=new Rectangle(SelectionRect);
     
  out.grow(4,4);
	  g2.draw(out);
	  
  }
  
  

  public void paint(Graphics g) {
    Graphics2D g2D = (Graphics2D)g;                     // Get a 2D device context
    for(Element element : theApp.getModel()) {          // Go through the list
      element.draw(g2D);                                // Element draws itself
    }
    if(SelectionRect!=null)  DrawSelectionBorder(g2D);
  }

  private Set4 theApp;                              // The application object
  private Element highlightElement;                     // Highlighted element
  private JPopupMenu elementPopup = new JPopupMenu("Element");
  private JMenuItem moveItem, deleteItem,rotateItem, sendToBackItem;
  private int mode = NORMAL;
  private Element selectedElement;
  private Rectangle SelectionRect;
//  private Rectangle[] points = { new Rectangle(), new Rectangle()};
  private int SIZE = 8;
 
  
  
  class MouseHandler extends MouseInputAdapter 
  { //   private int pos=-1;
      // Process pop-up trigger event
      
	  boolean dragging = false;
	    // Give user some leeway for selections.
	    final int PROX_DIST = 7;

	  
	  
	  public void processPopupTrigger(MouseEvent e) {
        start = e.getPoint();                           // Save the cursor position in start
        if(highlightElement == null) {
          theApp.getWindow().getPopup().show((Component)e.getSource(),start.x, start.y);
        } else {
          elementPopup.show((Component)e.getSource(), start.x, start.y);
        }    
        start = null;
        
      }

    public void mousePressed(MouseEvent e)  
    {//show();
      
    	   if(getCursor() != Cursor.getDefaultCursor()) 
    	   {    dragging = true;  
    	  
    	       tempElement=selectedElement;
    	   }             // If cursor is set for resizing, allow dragging.
    	  // else
    	  //	   { if (isOverRect(e.getPoint())) mode=MOVE; else mode=NORMAL; }
    	   
    	
    	
    	if(e.isPopupTrigger()) {
        processPopupTrigger(e); 
      } else if((button1Down = (e.getButton() == MouseEvent.BUTTON1))) {
        start = e.getPoint();                  // Save the cursor position in start
        g2D = (Graphics2D)getGraphics();                // Get graphics context
        g2D.setXORMode(getBackground());                // Set XOR mode
       // selectedElement.draw(g2D);   // draw to erase it
      }

      
      /*
      Point p = e.getPoint();

      for (int i = 0; i < points.length; i++) {
        if (points[i].contains(p)) {
          pos = i;
        show("in");
          return;
        }
      }

      */
      
    } 

    public void mouseDragged(MouseEvent e) 
    {
    	
        last = e.getPoint();                              // Save cursor position
    	
        	          
        if(dragging )  ResizeShape();
        
       // else {if(selectedElement==null)
        
        if(button1Down && (theApp.getWindow().getElementType() != TEXT)&& (mode == NORMAL)) {
          if(tempElement == null)
          {                       // Is there an element?
            tempElement = createElement(start, last);     // No, so create one
            selectedElement=tempElement;
          } else {
            selectedElement.draw(g2D);                        // Yes - draw to erase it
            selectedElement.modify(start, last);              // Now modify it
          }
          selectedElement.draw(g2D);                          // and draw it
        } else if(button1Down && mode == MOVE && selectedElement != null) {
          selectedElement.draw(g2D);                    // Draw to erase the element
         
          
          selectedElement.move(last.x-start.x, last.y-start.y);  // Move it
          selectedElement.draw(g2D);                     // Draw in its new position
          
          DrawSelectionBorder(g2D);
          SelectionRect=selectedElement.getBounds();
       //   g2D.setXORMode(getBackground());                // Set XOR mode
          DrawSelectionBorder(g2D);
          
          
      //    n
          start = last;                                  // Make start current point
        } else if(button1Down && mode == ROTATE && selectedElement != null) {
          selectedElement.draw(g2D);                   // Draw to erase the element
          selectedElement.rotate(getAngle(selectedElement.getPosition(),
                                                                      start, last));
          selectedElement.draw(g2D);                  // Draw in its new position
          start = last;                               // Make start current point
        }
        
        /*
        
        if (pos != -1)
            
          //points[pos].setRect(event.getPoint().x,event.getPoint().y,points[pos].getWidth(),
          //    points[pos].getHeight());
        	SelectionRect.x=points[0].x;SelectionRect.y=points[0].y;
        	SelectionRect.width=Math.abs(points[1].x - points[0].x);
        	SelectionRect.height=Math.abs(points[1].y-points[0].y);
          DrawSelectionBorder(g2D);
          repaint();
        */
      }

      // Helper method for calculating the rotation angle
      double getAngle(Point position, Point start, Point last) {
        // Get perpendicular distance from last to the line from position to start
        double perp = Line2D.ptLineDist(position.x, position.y,
                                        last.x, last.y, start.x, start.y);
        // Get the distance from position to start
        double hypotenuse = position.distance(start);
        if(hypotenuse == 0.0) {                      // Make sure it's
          hypotenuse = 1.0;                          // non-zero
        }

        // Angle is the arc sine of perp/hypotenuse. Clockwise is positive angle
        return -Line2D.relativeCCW(position.x, position.y,
                                   start.x, start.y,
                                   last.x, last.y)*Math.asin(perp/hypotenuse);
      }

    public void mouseReleased(MouseEvent e)
    { dragging=false;
    	//pos = -1;show("NORMAL");
      if(e.isPopupTrigger()) {
        processPopupTrigger(e);
      } else if((e.getButton()==MouseEvent.BUTTON1) && (theApp.getWindow().getElementType() != TEXT) && mode == NORMAL) {
        button1Down = false;                     // Reset the button 1 flag
        if(tempElement != null) {
       theApp.getModel().add(selectedElement);       // Add element to the model
       
       
       if(SelectionRect!=null)   ///remove old selection
		  { g2D.setXORMode(getBackground());                // Set XOR mode
            DrawSelectionBorder(g2D);
		  }
       
       selectedElement=tempElement;
       SelectionRect=selectedElement.getBounds();
   //    SelectionRect.grow(4,4);
       DrawSelectionBorder(g2D);   
       tempElement = null;                    // No temporary element now stored
        }
      } else if((e.getButton()==MouseEvent.BUTTON1) &&
                (mode == MOVE || mode == ROTATE)) {
        button1Down = false;                     // Reset the button 1 flag
        if(selectedElement != null) {
          repaint();
        }
        mode = NORMAL;
      }

      if(g2D != null) {
        g2D.dispose();                           // Release graphic context resource
        g2D = null;                              // Set it to null
      }
      start = last = null;                       // Remove the points
      //selectedElement=tempElement;
      //SelectionRect=selectedElement.getBounds();
      //SelectionRect.grow(4,4);
      //DrawSelectionBorder(g2D);
    /*
      if(selectedElement!=null)
	  {                     ///remove old selection
		  
          
	  }
*/
      tempElement=null;      // Reset elements
    }

    public void mouseClicked(MouseEvent e)
    { 
     
    	
      if((e.getButton()== MouseEvent.BUTTON1) && (theApp.getWindow().getElementType() == TEXT))
      {
  
        start = e.getPoint();                  // Save cursor position - start of text
        String text = JOptionPane.showInputDialog(
                   (Component)e.getSource(),   // Used to get the frame
                   "Enter Text:",              // The message
                   "Dialog for Text Element",  // Dialog title
                   JOptionPane.PLAIN_MESSAGE); // No icon
   
        if(text != null && text.length()!= 0)  { // If we have text        
                                                 // create the element
          g2D = (Graphics2D)getGraphics();
          Font font = theApp.getWindow().getCurrentFont();
          tempElement = new Element.Text(
            font,                                    // The font
            text,                                    // The text
            start,                                   // Position of the  text
            theApp.getWindow().getElementColor(),    // The text color
            new java.awt.font.TextLayout(text, font, // The bounding rectangle
            g2D.getFontRenderContext()).getBounds().getBounds()
                                         );

          if(tempElement != null) {                  // If we created one
            theApp.getModel().add(selectedElement);      // add it to the model
            tempElement = null;                      // and reset the field
          }
          g2D.dispose();                             // Release context resources
          g2D = null;
          start = null;
        }
      }
      if(highlightElement!=null)
      { 
    	  g2D = (Graphics2D)getGraphics();
    	  if(selectedElement!=null)
    	  {                     ///remove old selection
    		  if(SelectionRect!=null)
    		  {
    			  
    		  DrawSelectionBorder(g2D);
              g2D.setXORMode(getBackground());                // Set XOR mode
              DrawSelectionBorder(g2D);
    	
    		  }
              
    	  }
    	  selectedElement = highlightElement;
    	  {
    	     SelectionRect=selectedElement.getBounds();
             //SelectionRect.grow(4,4);
             DrawSelectionBorder(g2D);
             
    	  }
    	  repaint();
    	  g2D.dispose();                          // Release graphic context resources
          g2D = null;
          return;
      }
    }

    // Handle mouse moves
    public void mouseMoved(MouseEvent e) 
    { 
      Point currentCursor = e.getPoint();  // Get current cursor position
      
     if(SelectionRect!=null) SetCursorShape(currentCursor);
      
      for(Element element : theApp.getModel())  {  // Go through the list
        if(element.getBounds().contains(currentCursor)) { // Under the cursor?
          if(element==highlightElement) {          // If it's already highlighted
            return;                                // we are done
          }
          // The element under the cursor is not highlighted
          g2D = (Graphics2D)getGraphics();         // Get graphics context
 
          
          // Un highlight any old highlighted element
          if(highlightElement!=null)   {           // If an element is highlighted
            highlightElement.setHighlighted(false);// un-highlight it and
            highlightElement.draw(g2D);            // draw it normal color
            
           
          }

          element.setHighlighted(true);           // Set highlight for new element
          highlightElement = element;             // Store new highlighted element
          element.draw(g2D);                      // Draw it highlighted
          
         
          g2D.dispose();                          // Release graphic context resources
          g2D = null;
          return;
        }
      }

      // Here there is no element under the cursor so...
      if(highlightElement!=null)   {            // If an element is highlighted
        g2D = (Graphics2D)getGraphics();        // Get graphics context
        highlightElement.setHighlighted(false); // ...turn off highlighting
        highlightElement.draw(g2D);             // Redraw the element
        
       
        
                
        highlightElement = null;                // No element highlighted
       
        g2D.dispose();                          // Release graphic context resources
        g2D = null;
      }
    }

    private Element createElement(Point start, Point end) {
      switch(theApp.getWindow().getElementType()) {
        case LINE:
          return new Element.Line(start, end, theApp.getWindow().getElementColor());   

        case RECTANGLE:
          return new Element.Rectangle(start, end, theApp.getWindow().getElementColor());

        case CIRCLE:
          return new Element.Circle(start, end, theApp.getWindow().getElementColor());

        case ELLIPSE:
            return new Element.Ellipse(start, end, theApp.getWindow().getElementColor());  
          
        case CURVE:
          return new Element.Curve(start, end, theApp.getWindow().getElementColor());

        default:
          assert false;                     // We should never get to here
       }
       return null;
    }

    
    private void SetCursorShape(Point p)
    { show("Set cursor fn");
        if(!isOverRect(p)) {
            if(getCursor() != Cursor.getDefaultCursor()) {
                // If cursor is not over rect reset it to the default.
                setCursor(Cursor.getDefaultCursor());
            }
            show("Not Over");
            return;
        }
        
        show("OVER RET");
        // Locate cursor relative to center of rect.
        int outcode = getOutcode(p);
        Rectangle r = new Rectangle(SelectionRect);
        switch(outcode) {
            case Rectangle.OUT_TOP:
            	show("OT");
                if(Math.abs(p.y - r.y) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.N_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_TOP + Rectangle.OUT_LEFT: show("TL");
                if(Math.abs(p.y - r.y) < PROX_DIST &&
                   Math.abs(p.x - r.x) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.NW_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_LEFT:show("L");
                if(Math.abs(p.x - r.x) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.W_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_LEFT + Rectangle.OUT_BOTTOM: show("BL");
                if(Math.abs(p.x - r.x) < PROX_DIST &&
                   Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                   setCursor(Cursor.getPredefinedCursor(
                                        Cursor.SW_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_BOTTOM:show("B");
                if(Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.S_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_BOTTOM + Rectangle.OUT_RIGHT:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST &&
                   Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.SE_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_RIGHT:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.E_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_RIGHT + Rectangle.OUT_TOP:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST &&
                   Math.abs(p.y - r.y) < PROX_DIST) {
                    setCursor(Cursor.getPredefinedCursor(
                                        Cursor.NE_RESIZE_CURSOR));
                }
                break;
            default:    // center
                setCursor(Cursor.getDefaultCursor());
        }
    	
    }
    
    
    
    
    
    /**
     * Make a smaller Rectangle and use it to locate the
     * cursor relative to the Rectangle center.
     */
    private int getOutcode(Point p) {
       Rectangle r = new Rectangle(SelectionRect);
        r.grow(-PROX_DIST, -PROX_DIST);
        return r.outcode(p.x, p.y);        
    }

    /**
     * Make a larger Rectangle and check to see if the
     * cursor is over it.
     */
    private boolean isOverRect(Point p) {
    	Rectangle r = new Rectangle(SelectionRect);
        r.grow(PROX_DIST, PROX_DIST);
        return r.contains(p);
    }

    
    void ResizeShape()
    {
     
            Rectangle r = SelectionRect;
            int type = getCursor().getType();
            int dx = last.x - r.x;
            int dy = last.y - r.y;
            switch(type) {
                case Cursor.N_RESIZE_CURSOR:
                    int height = r.height - dy;
                    r.setRect(r.x, r.y+dy, r.width, height); 
                    break;
                case Cursor.NW_RESIZE_CURSOR:
                    int width = r.width - dx;
                    height = r.height - dy;
                    r.setRect(r.x+dx, r.y+dy, width, height);
                    break;
                case Cursor.W_RESIZE_CURSOR:
                    width = r.width - dx;
                    r.setRect(r.x+dx, r.y, width, r.height);
                    break;
                case Cursor.SW_RESIZE_CURSOR:
                    width = r.width - dx;
                    height = dy;
                    r.setRect(r.x+dx, r.y, width, height);
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    height = dy;
                    r.setRect(r.x, r.y, r.width, height);
                    break;
                case Cursor.SE_RESIZE_CURSOR:
                    width = dx;
                    height = dy;
                    r.setRect(r.x, r.y, width, height);
                    break;
                case Cursor.E_RESIZE_CURSOR:
                    width = dx;
                    r.setRect(r.x, r.y, width, r.height);
                    break;
                case Cursor.NE_RESIZE_CURSOR:
                    width = dx;
                    height = r.height - dy;
                    r.setRect(r.x, r.y+dy, width, height);
                    break;
                default:
                    System.out.println("unexpected type: " + type);
            }
            
            SelectionRect=r;
            start.x=SelectionRect.x;start.y=SelectionRect.y;
            last.x=start.x+SelectionRect.width;last.y=start.y+SelectionRect.height;
            repaint();
    	
    	
    	}
    	
    	
    	
    	
    
    
    
    
    
    private Point start;                     // Stores cursor position on press
    private Point last;                      // Stores cursor position on drag
    private Element tempElement;             // Stores a temporary element
    private boolean button1Down = false;     // Flag for button 1 state
    private Graphics2D g2D;                  // Temporary graphics context

  
  
  
  
  
  }
  

  
  
}