package mediadorchat;

import java.util.ArrayList;
import java.util.List;

public class Topic {

    public List<Conexion> suscriptores = new ArrayList();
    public String topicTitle;
    public String name;

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public void Publish(String msg) {

        for (int i = 0; i < suscriptores.size(); i++) {
            if (i != suscriptores.indexOf(this)) {
                suscriptores.get(i).EnviarMensaje("Mensaje de " + topicTitle + ":");
                suscriptores.get(i).EnviarMensaje(msg);
            }
        }

    }

    public List<Conexion> getSuscriptores() {
        return suscriptores;
    }

    public void setSuscriptores(List<Conexion> suscriptores) {
        this.suscriptores = suscriptores;
    }

    public void setAdminName(String admin) {
        this.name = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
