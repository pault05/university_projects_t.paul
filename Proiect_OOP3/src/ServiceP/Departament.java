package ServiceP;

import java.util.ArrayList;

public class Departament {
    //------------------------------Atribute
    private int idDepartament;
    public int getIdDepartament() {return idDepartament;}

    private String numeDepartament;
    public String getNumeDepartament() {return numeDepartament;}
    public void setNumeDepartament(String numeDepartament) {this.numeDepartament = numeDepartament;}

    private ArrayList<Angajat> angajatiDepartament;
    public ArrayList<Angajat> getAngajatiDepartament() {return angajatiDepartament;}
    public void setAngajatiDepartament(ArrayList<Angajat> angajatiDepartament) {this.angajatiDepartament = angajatiDepartament;}
//----------------------------------------Constructori
    public Departament(int idDepartament, String numeDepartament, ArrayList<Angajat> angajatiDepartament) {
        this.idDepartament = idDepartament;
        this.numeDepartament = numeDepartament;
        this.angajatiDepartament = angajatiDepartament;
    }
    //---------------------------------Metode
    public void addAngajat(String numeAngajat, Masina car){
        if(car==null){
        angajatiDepartament.add(new Angajat(numeAngajat));}
        else {angajatiDepartament.add(new Angajat(numeAngajat,car));}
    }
    public void removeAngajatByIndex(int index) {
        angajatiDepartament.remove(index);
    }
}
