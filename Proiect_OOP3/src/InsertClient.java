import ServiceP.Client;
import ServiceP.Masina;
import ServiceP.Service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.DriverManager;    //biblioteci necesare pt database
import java.sql.ResultSet;        //folosim XAMPP pentru a crea legatura intre intellij si mysql
import java.sql.Statement;

public class InsertClient {
    private JTextField numeClient;
    private JTextField telefonClient;
    private JTextField tipText;
    private JTextField anText;
    private JTextField marcaText;
    private JTextField serieText;
    private JButton inserareButton;
    public JPanel inserareClient;
    private JButton inserareAutobutton;

    //inserare manuala

    public InsertClient(int depID, int elemID) {
        inserareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //validare date inserare manuala client
                try {
                    int serieAUX = Integer.parseInt(serieText.getText());
                    int anAUX = Integer.parseInt(anText.getText());

                Masina carClient = new Masina(serieAUX, marcaText.getText(), anAUX, tipText.getText());
                Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).introducereClient(new Client(numeClient.getText(), telefonClient.getText(), carClient));
                numeClient.setText(null);
                telefonClient.setText(null);
                serieText.setText(null);
                marcaText.setText(null);
                anText.setText(null);
                tipText.setText(null);

                } catch (Exception eINT) {
                    System.out.println("EROARE TIP INTRARE");
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "Nu ati introdus datele corect !",
                            "Eroare Date",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //auto inserare date masini din bd
        inserareAutobutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");                                 //nume bd
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bdmasini", "root", "");

                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from masina");

                    while (resultSet.next()) {

                        Masina carClient = new Masina(resultSet.getInt(3), resultSet.getString(4), resultSet.getInt(5), resultSet.getString(6));
                        Service.getInstance().getDepartamente().get(depID).getAngajatiDepartament().get(elemID).introducereClient(new Client(resultSet.getString(1), resultSet.getString(2), carClient));

                  /*      //scriere in fisier

                        try {
                            FileWriter myWriter = new FileWriter("clienti.txt");
                         //   myWriter = new FileWriter("clienti.txt");
                            myWriter.write(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getInt(3) + " " + resultSet.getString(4) + " " + resultSet.getInt(5) + " " +resultSet.getString(6));
                            myWriter.close();  //!!
                            System.out.println("Successfully wrote to the file.");
                        } catch (IOException eWrite) {
                            System.out.println("ERROR WRITE");
                        }
                */

                        numeClient.setText(null);
                        telefonClient.setText(null);
                        serieText.setText(null);
                        marcaText.setText(null);
                        anText.setText(null);
                        tipText.setText(null);
                    }

                    connection.close();
                } catch (Exception exBD) {
                    System.out.println("EROARE BD");
                }
            }
        });
    }

}