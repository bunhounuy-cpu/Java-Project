package gui;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the Vending Machine GUI application.
 * Run this class to start the graphical interface.
 */
public class GUIMain {
    public static void main(String[] args) {
        // Set system look and feel for native appearance
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.out.println("Could not set system look and feel: " + e.getMessage());
        }

        // Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new VendingMachineGUI());
    }
}
