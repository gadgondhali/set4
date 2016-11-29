// Sketching application
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class Set4 {
  public static void main(String[] args) {
    theApp = new Set4();
    SwingUtilities.invokeLater(
       new Runnable() {                          // Anonymous Runnable class object
                        public void run() {      // Run method executed in thread
                          theApp.creatGUI();     // Call static GUI creator
                        }
                      }       );
  }

  // Method to create the application GUI
  private void creatGUI() {
    window = new Set4Frame("Set4", this);  // Create the app window
    Toolkit theKit = window.getToolkit();        // Get the window toolkit
    Dimension wndSize = theKit.getScreenSize();  // Get screen size

    // Set the position to screen center & size to half screen size
    window.setBounds(wndSize.width/4, wndSize.height/4,        // Position
                      wndSize.width/2, wndSize.height/2);      // Size

    window.addWindowListener(new WindowAdapter() {// Add window listener
                               public void windowClosing(WindowEvent e) {
                                 window.checkForSave();
                               }
                             }  );
    sketch = new Set4Model();                   // Create the model
    view = new Set4View(this);                  // Create the view
    sketch.addObserver(view);                     // Register view with the model
    sketch.addObserver(window);                   // Register window with the model
    window.getContentPane().add(view, BorderLayout.CENTER);
    window.setVisible(true);
  }

  // Insert a new sketch model
  public void insertModel(Set4Model newSketch) {
    sketch = newSketch;                          // Store the new sketch
    sketch.addObserver(view);                    // Add the view as observer
    sketch.addObserver(window);                  // Add the app window as observer
    view.repaint();                              // Repaint the view
  }

  // Return a reference to the application window
  public Set4Frame getWindow() { 
     return window; 
  }

  // Return a reference to the model
  public Set4Model getModel() { 
     return sketch; 
  }

  // Return a reference to the view
  public Set4View getView() { 
     return view; 
  }

  private Set4Model sketch;                    // The data model for the sketch
  private Set4View view;                       // The view of the sketch

  private  Set4Frame window;                   // The application window
  private static  Set4 theApp;               // The application object
}


