import ServiceP.Masina;
import ServiceP.Service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FormAngajat {
    private JTextField numeAngajatText;
    public JPanel rootAngajat;
    private JButton inserareButton;
    private JTextField tipAngajat;
    private JTextField marcaAngajat;
    private JTextField serieAngajat;
    private JTextField anAngajat;
    private JCheckBox masinaAngajatCheckBox;
    private JLabel marcaLabel;
    private JLabel serieLabel;
    private JLabel anLabel;
    private JLabel tipLabel;

    public FormAngajat(int depID){
        masinaAngajatCheckBox.doClick();
        masinaAngajatCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(masinaAngajatCheckBox.isSelected()){
                    serieAngajat.setVisible(true);
                    anAngajat.setVisible(true);
                    tipAngajat.setVisible(true);
                    marcaAngajat.setVisible(true);
                    serieLabel.setVisible(true);
                    marcaLabel.setVisible(true);
                    anLabel.setVisible(true);
                    tipLabel.setVisible(true);
                }else{
                    serieAngajat.setVisible(false);
                    anAngajat.setVisible(false);
                    tipAngajat.setVisible(false);
                    marcaAngajat.setVisible(false);
                    serieLabel.setVisible(false);
                    marcaLabel.setVisible(false);
                    anLabel.setVisible(false);
                    tipLabel.setVisible(false);
                }
            }
        });

        inserareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    if (numeAngajatText.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(rootAngajat,
                                "Date incomplete");
                    } else if(serieAngajat.getText().isEmpty()&& marcaAngajat.getText().isEmpty()&&anAngajat.getText().isEmpty()&&tipAngajat.getText().isEmpty()){
                        Service.getInstance().getDepartamente().get(depID).addAngajat(numeAngajatText.getText(),null);
                        numeAngajatText.setText(null);

                    }else {
                        Service.getInstance().getDepartamente().get(depID).addAngajat(numeAngajatText.getText(), new Masina(Integer.parseInt(serieAngajat.getText()), marcaAngajat.getText(), Integer.parseInt(anAngajat.getText()), tipAngajat.getText()));
                        numeAngajatText.setText(null);
                        anAngajat.setText(null);
                        serieAngajat.setText(null);
                        tipAngajat.setText(null);
                        marcaAngajat.setText(null);
                    }

            }

        });

    }
}
