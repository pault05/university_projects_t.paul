import ServiceP.Angajat;
import ServiceP.Client;
import ServiceP.Service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormMoveAngajat  {
    private JTextField textField1;
    public JPanel panel1;
    private JButton button1;

    public FormMoveAngajat(int depID, int angajatID, int clientID){
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    //ID.getInstance().setOk(false);
                    ID.getInstance().setId(Integer.parseInt(textField1.getText()));
                    System.out.println(textField1.getText());
                    Client copy= new Client(Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(angajatID).getClientiAngajat().get(clientID));
                    Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(ID.getInstance().getId()).getClientiAngajat().add(copy);
                    Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(angajatID).getClientiAngajat().remove(clientID);
                     //ID.getInstance().setOk(true);// ----------------Validare stergere
                    // System.out.println(ID.getInstance().isOk());

                }
                catch(Exception exception){
                    JOptionPane.showMessageDialog(null, "ID introdus gresit");
                }}
        });


    }

}

