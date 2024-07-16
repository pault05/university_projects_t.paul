import ServiceP.Client;
import ServiceP.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AfisareAngajat {
    private JButton inserareClientButton;
    private JButton mutaClientButton;
    private JButton stergereClientButton;
    private JButton statusClientButton;
    private JTextPane dateAngajat;
    public JPanel afisareAngajat;
    private JPanel listPanel;


    public AfisareAngajat(int depID,int elemID){
        String[] listElems = new String[100];
        final JList list = new JList(listElems);
        final JScrollPane pane = new JScrollPane(list);

        dateAngajat.setText(Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).toString());

        inserareClientButton.addActionListener(new ActionListener() {
            @Override//-----------------------------Window pt inserare client, apeleaza form Inseare CLient
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Introducere Client");
                frame.setContentPane(new InsertClient(depID,elemID).inserareClient);
                frame.setSize(525,215);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
                statusClientButton.doClick();

            }
        });

        statusClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    for (int i = 0; i < Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).getClientiAngajat().size(); i++) {
                        listModel.addElement(Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).getClientiAngajat().get(i).toString());
                    }
                    list.setModel(listModel);
                    listPanel.removeAll();
                    listPanel.setLayout(new BorderLayout());
                    listPanel.add(pane, BorderLayout.CENTER);
                    listPanel.revalidate();
                dateAngajat.setText(Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).toString());

            }

        });

        stergereClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedElem = "";
                    int selectedIndices[] = list.getSelectedIndices();
                    int stergereId = -1;
                    for (int j = 0; j < selectedIndices.length; j++) {
                        String elem =
                                (String) list.getModel().getElementAt(selectedIndices[j]);
                        stergereId = selectedIndices[j];
                        selectedElem += "\n" + elem;
                    }
                    Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).getClientiAngajat().remove(stergereId);
                    statusClientButton.doClick();

                }catch(Exception exStergere){
                    JOptionPane.showMessageDialog(null, "Eroare stergere !");
                }
            }
        });
        try{
        mutaClientButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedElem = "";
                int selectedIndices[] = list.getSelectedIndices();
                int clientId=-1;
                for (int j = 0; j < selectedIndices.length; j++) {
                    String elem =
                            (String) list.getModel().getElementAt(selectedIndices[j]);
                    clientId = selectedIndices[j];
                    selectedElem += "\n" + elem ;
                }
                JFrame formMoveClient = new JFrame("Introducere ID");
                formMoveClient.setContentPane(new FormMoveAngajat(depID,elemID,clientId).panel1);
                formMoveClient.setSize(200,215);
                formMoveClient.setVisible(true);
                formMoveClient.setLocationRelativeTo(null);
            }

        });
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Eroare mutare !");
        }
    }
}
