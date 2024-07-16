package ServiceP;

public class Client {
    //------------------------------------atribute
    private static int nextId=0;//incrementare ID
    private int idClient;
    private int getIdClient(){return idClient;}
    private void setIdClient(int idClient){this.idClient=idClient;}

    private String numeClient;
    public String getNumeClient() { return numeClient; }
    public void setNumeClient(String numeClient){ this.numeClient=numeClient; }

    private String telefonClient;
    public String getTelefonClient() { return telefonClient; }
    public void setTelefonClient(String telefonClient) { this.telefonClient = telefonClient; }

    private Masina masinaClient;
    public Masina getMasinaClient() { return masinaClient; }
    public void setMasinaClient(Masina masinaClient) { this.masinaClient = masinaClient; }

//--------------------------------------Constructori
    public Client(String numeClient, String telefonClient, Masina masinaClient){
        this.idClient=nextId;
        this.masinaClient=masinaClient;
        this.telefonClient=telefonClient;
        this.numeClient=numeClient;
        nextId++;
    }

    public Client(Client copy){
        this.idClient=copy.idClient;
        this.masinaClient=copy.masinaClient;
        this.telefonClient=copy.telefonClient;
        this.numeClient=copy.numeClient;

    }
//--------------------------------------Metode
    public String toString(){
        return "ID : " +idClient + " , Nume : "+ numeClient + " , Masina  : " + masinaClient + " "+ "stare : " +getMasinaClient().statusVehicul();
    }
}