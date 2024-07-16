package ServiceP;

import java.io.Serializable;
import java.util.ArrayList;

public class Service implements Serializable {
    private static Service instance;  // Singleton instance
    private ArrayList<Departament> departamente;
    private String name;

    private Service() {
        this.departamente = new ArrayList<>();
        this.name = "Default ServiceP.Service Name";
    }

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
    public static Service getInstance(Service p){
        if (instance == null) {
            instance = p;
        }
        return instance;
    }

    public void addDepartment(Departament department) {
        departamente.add(department);
    }

    public ArrayList<Departament> getDepartamente() {
        return departamente;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
