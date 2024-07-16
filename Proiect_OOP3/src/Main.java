import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame rootPanel = new JFrame("ServiceP.Service");
        rootPanel.setContentPane(new Login().formLogin);
        rootPanel.setSize(380,200);
        rootPanel.setVisible(true);
        rootPanel.setLocationRelativeTo(null);
        rootPanel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}

// muta client - eroarea