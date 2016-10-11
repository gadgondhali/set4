// Sketching application
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.SwingUtilities;

public class set4 {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
       new Runnable() {                          // Anonymous Runnable class object
                        public void run() {      // Run method executed in thread
                          creatGUI();            // Call static GUI creator
                        }
                      }       );
  }

  static void creatGUI() {
    window = new set4Frame("Set4 - Untitled");        // Create the app window
    Toolkit theKit = window.getToolkit();        // Get the window toolkit
    Dimension wndSize = theKit.getScreenSize();  // Get screen size

    // Set the position to screen center & size to half screen size
    window.setBounds(wndSize.width/4, wndSize.height/4,        // Position
                      wndSize.width/2, wndSize.height/2);      // Size

    window.setVisible(true);
  }

  private static set4Frame window;             // The application window
}