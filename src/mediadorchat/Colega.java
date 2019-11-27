package mediadorchat;

import TransferenciaArchivos.ClienteFichero;
import Ventanas.VentanaChat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class Colega {

    public DataOutputStream buffSalida;
    public DataInputStream buffEntrada;
    public String nombre;
    String ip;
    int puerto;
    Socket cliente;

    public void RecibirDatos() {
        String mesgIn = null;
        Thread hilo = new Thread(() -> {
            try {
                while (true) {
                    String mesgIn1 = buffEntrada.readUTF();
                    String[] arraymensaje = mesgIn1.split(" ");

                    switch (arraymensaje[0]) {
                        case "lista":
                            VentanaChat.cbTopics.addItem(arraymensaje[1]);
                            break;
                        case "usuario":
                            VentanaChat.txtUsuarios.setText(VentanaChat.txtUsuarios.getText() + "\n"
                                    + arraymensaje[1]);
                            break;
                        default:
                            System.out.println("Mensaje nuevo: " + mesgIn1);
                            if (mesgIn1.contains("El usuario")) {
                                VentanaChat.actualizaTopicsUsuarios("topics");
                                VentanaChat.actualizaTopicsUsuarios("usuarios");
                            } else if (mesgIn1.contains("Topic creado")) {
                                VentanaChat.actualizaTopicsUsuarios("topics");
                                VentanaChat.actualizaTopicsUsuarios("usuarios");
                            } else if (mesgIn1.contains("Se ha eliminado")) {
                                VentanaChat.actualizaTopicsUsuarios("topics");
                                VentanaChat.actualizaTopicsUsuarios("usuarios");
                            } else if (mesgIn1.contains("TransferenciaArchivo: ")) {
                                System.out.println("Entro al metodo");
                                ClienteFichero cf = new ClienteFichero(
                                        arraymensaje[2], arraymensaje[3], arraymensaje[4]);
                                if (cf.pide()) {
                                    VentanaChat.txtChat.append("\n"
                                            + "Archivo recibido");
                                } else {
                                    VentanaChat.txtChat.append("\n"
                                            + "Error al recibir el archivo");
                                }
                            }
                            VentanaChat.txtChat.append("\n" + mesgIn1);
                            break;
                    }
                }
            } catch (IOException ex) {
                System.out.print(ex);
            }
        });
        hilo.start();
    }

    public void EnviaMensaje(String msg) {
        try {
            buffSalida.writeUTF(msg);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
