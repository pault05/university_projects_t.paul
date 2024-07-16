package ServiceP;

public class Masina implements Automobile{
//-----------------------------------Atribute
    private int seria;
    public int getSeria() { return seria; }

    private String marca;
    public String getMarca() { return marca; }

    private int an;
    public int getAn() { return an; }

    private String tip;
    public String getTip() { return tip; }

    private boolean status;
//-------------------------------------------------Constructori
    public Masina(int seria, String marca, int an, String tip)
    {
        this.marca=marca;
        this.seria=seria;
        this.an=an;
        this.tip=tip;
        this.status = false;
    }
//-------------------------------------------------Metode
    public String toString() { return seria + " " + marca + " " + an + " " + tip; }

    @Override
    public void reparatieVehicul() {
        status = true;
    }

    @Override
    public boolean statusVehicul() {
        return status;
}

    @Override
    public void setStatus(boolean status) {
    }

    @Override
    public boolean getStatus() {
        return false;
    }
}