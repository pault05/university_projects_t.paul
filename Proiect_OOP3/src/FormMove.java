import ServiceP.Angajat;
import ServiceP.Service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormMove  {
    private JTextField textField1;
    public JPanel panel1;
    private JButton button1;

    public FormMove(int k, int angajatId){

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
               // ID.getInstance().setOk(false);
                ID.getInstance().setId(Integer.parseInt(textField1.getText()));
                System.out.println(textField1.getText());
                Angajat copy= new Angajat(Service.getInstance().getDepartamente().get(k).getAngajatiDepartament().get(angajatId));
                Service.getInstance().getDepartamente().get(ID.getInstance().getId()).getAngajatiDepartament().add(copy);
                Service.getInstance().getDepartamente().get(k).getAngajatiDepartament().remove(angajatId);
               // ID.getInstance().setOk(true);// ----------------Validare stergere
               // System.out.println(ID.getInstance().isOk());

            }
            catch(Exception exception){
                JOptionPane.showMessageDialog(null, "ID introdus gresit");
            }}
        });


        }

    }

