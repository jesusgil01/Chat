package mediadorchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente extends Colega {

    public Cliente(String nombre, String ip, int puerto) {

        this.puerto = puerto;
        this.nombre = nombre;
        this.ip = ip;
    }

    public void init() {

        try {
            cliente = new Socket(ip, puerto);
            buffSalida = new DataOutputStream(cliente.getOutputStream());
            buffEntrada = new DataInputStream(cliente.getInputStream());       
            
        } catch (IOException e) {
            System.out.println("No funciono :(");
            System.exit(0);
        }
    }

}
