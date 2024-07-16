import ServiceP.Angajat;
import ServiceP.Departament;
import ServiceP.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FormService {
    public JPanel rootPanel;
    private JButton mecanicaButton;
    private JButton vopsitorieButton;
    private JButton tinichigerieButton;
    private JButton afisareAngajatiButton;
    private JButton inserareAngajatButton;
    private JButton stergereAngajatButton;
    private JButton mutaAngajatButton;
    private JPanel listPanel;
    private JButton selectareAngajatButton1;
    private JLabel LabelPic;
    private int k = -1; // ID departament

    public FormService(){

        BufferedImage bufferedImage;
        try {                                                           //nume
            bufferedImage = ImageIO.read(new File("C:\\Users\\paul_\\IdeaProjects\\Proiect_OOP3\\asd.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Image image = bufferedImage.getScaledInstance(800, 400, Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(image);
        LabelPic.setIcon(icon);
        Service.getInstance().addDepartment(new Departament(0,"Vopsitorie",new ArrayList<Angajat>()));
        Service.getInstance().addDepartment(new Departament(1,"Tinichigerie",new ArrayList<Angajat>()));
        Service.getInstance().addDepartment(new Departament(2,"Mecanica",new ArrayList<Angajat>()));

        String[] listElems = new String[100];
        final JList list = new JList(listElems);
        final JScrollPane pane = new JScrollPane(list);

        vopsitorieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                k =Service.getInstance().getDepartamente().get(0).getIdDepartament();
            }
        });

        tinichigerieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                k =Service.getInstance().getDepartamente().get(1).getIdDepartament();
            }
        });

        mecanicaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                k = Service.getInstance().getDepartamente().get(2).getIdDepartament();
            }
        });

        afisareAngajatiButton.addActionListener(new ActionListener() {
            @Override//----------------------------Afisare lista de angajati
            public void actionPerformed(ActionEvent e) {
                if (k != -1) {

                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    for (int i = 0; i < Service.getInstance().getDepartamente().get(k).getAngajatiDepartament().size(); i++) {
                        listModel.addElement(Service.getInstance().getDepartamente().get(k).getAngajatiDepartament().get(i).toString());
                    }
                    list.setModel(listModel);
                    listPanel.removeAll();
                    listPanel.setLayout(new BorderLayout());
                    listPanel.add(pane, BorderLayout.CENTER);
                    listPanel.revalidate();

                } else {
                    JOptionPane.showMessageDialog(null, "Please select a department first.");
                }
            }
        });

        inserareAngajatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(k==-1){JOptionPane.showMessageDialog(null, "Please select a department first.");}
                else {
                    JFrame frame = new JFrame("Inserare ServiceP.Angajat");
                    frame.setContentPane(new FormAngajat(k).rootAngajat);
                    frame.setSize(450, 200);
                    frame.setVisible(true);
                    frame.setLocationRelativeTo(null);
                }
        } });
            selectareAngajatButton1.addActionListener(new ActionListener() {
                @Override
//___________________________________________Window de afisare date ale angajatului si lista de clienti pe care ii are
                public void actionPerformed(ActionEvent e) {

                    try {
                        //  if(Service.getInstance().getDepartamente().get(k).getAngajatiDepartament().isEmpty()){
                        String selectedElem = "";
                        int selectedIndices[] = list.getSelectedIndices();
                        int elemID = 0;
                        for (int j = 0; j < selectedIndices.length; j++) {
                            String elem =
                                    (String) list.getModel().getElementAt(selectedIndices[j]);
                            elemID = selectedIndices[j];
                            selectedElem += "\n" + elem;
                        }
                        JFrame frame = new JFrame("Afisare Angajat");
                        frame.setContentPane(new AfisareAngajat(k, elemID).afisareAngajat);
                        frame.setSize(525, 215);
                        frame.setVisible(true);
                        frame.setLocationRelativeTo(null);
                        //}
                        //else{JOptionPane.showMessageDialog(null, "Worker invalid");}
                    }catch(Exception exSelect)
                    {
                        JOptionPane.showMessageDialog(null, "Please add an employee first.");
                    }

                }});
        stergereAngajatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedElem = "";
                    int selectedIndices[] = list.getSelectedIndices();
                    int elemID = 0;
                    for (int j = 0; j < selectedIndices.length; j++) {
                        String elem =
                                (String) list.getModel().getElementAt(selectedIndices[j]);
                        elemID = selectedIndices[j];
                        selectedElem += "\n" + elem;

                    }
                    Service.getInstance().getDepartamente().get(k).removeAngajatByIndex(elemID);
                    afisareAngajatiButton.doClick();
                } catch(Exception eStergere){
                    JOptionPane.showMessageDialog(null, "Delete error!.");
                }
            }
        });
        mutaAngajatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedElem = "";
                int selectedIndices[] = list.getSelectedIndices();
                int angajatId = -1;
                for (int j = 0; j < selectedIndices.length; j++) {
                    String elem =
                            (String) list.getModel().getElementAt(selectedIndices[j]);
                    angajatId = selectedIndices[j];
                    selectedElem += "\n" + elem;
                }
                JFrame formMove = new JFrame("Introducere ID");
                formMove.setContentPane(new FormMove(k, angajatId).panel1);
                formMove.setSize(200, 215);
                formMove.setVisible(true);
                formMove.setLocationRelativeTo(null);

            }
        });
    }
}
