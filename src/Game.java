import java.awt.*;
import javax.swing.*;

public class Game implements Runnable {
    public void run() {

        final JFrame frame = new JFrame("Charming Chromodynamics");
        frame.setLocation(100, 100);
        
        String instructions = "Controls:\n"
        		+ "Move Left: Left Key (Default: Left Arrow)\n"
        		+ "Move Right: Right Key (Default: Right Arrow)\n"
        		+ "Jump/Double Jump: Up Key (Default: Up Arrow)\n"
        		+ "Fall Faster: Down Key (Default: Down Arrow)\n"
        		+ "Aim Left: AimLeft Key (Default: Comma)\n"
        		+ "Aim Right: AimRight Key (Default: Period)\n"
        		+ "Shoot: Hold and Release Shoot Key (Default: Slash)\n"
        		+ "Reset: Delete\n"
        		+ "\n"
        		+ "Rules:\n"
        		+ "Shooting an Enemy: +1 Point\n"
        		+ "Shooting an Enemy off the Stage (directly or indirectly): +3 Points\n"
        		+ "Falling off the Stage: -3 Points\n"
        		+ "First to 20 Points Wins";
        JOptionPane.showMessageDialog(frame, instructions);

        final JPanel status_panel = new JPanel(new GridLayout(1,2));
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("");
        status_panel.add(status);
        final JLabel transientstatus = new JLabel("");
        status_panel.add(transientstatus);

        final GameCourt court = new GameCourt(status,transientstatus);
        frame.add(court, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        court.reset();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}