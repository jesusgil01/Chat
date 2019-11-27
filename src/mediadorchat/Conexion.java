package mediadorchat;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;



public class Conexion extends Thread {

    Socket cliente = null;
    DataInputStream buffEntrada;
    DataOutputStream buffSalida;
    public static List<Conexion> clientesConectados = new ArrayList();
    public static List<Topic> topics = new ArrayList<Topic>();
    Comandos comandos = new Comandos();
    public String username;


    public Conexion(Socket cliente, DataInputStream buffEntrada, DataOutputStream buffSalida, String nombreusuario) {
        this.cliente = cliente;
        username = nombreusuario;
        System.out.println(nombreusuario);
        this.buffEntrada = buffEntrada;
        this.buffSalida = buffSalida;
        clientesConectados.add(this);
        Topic topic = topics.stream().
                filter(current -> "BroadCast".equals(current.getTopicTitle()))
                .findAny()
                .orElse(null);

        if (topic != null) {
            System.out.println("Agrego el usuario");
            topic.getSuscriptores().add(this);
        } else {
            System.out.println("Va a crear el topic broadcast");
            Topic Broad = new Topic();
            Broad.setTopicTitle("BroadCast");
            Broad.getSuscriptores().add(this);
            topics.add(Broad);
        }

        Topic topico = topics.stream().
                filter(current -> username.equals(current.getTopicTitle()))
                .findAny()
                .orElse(null);
        if (topico != null) {
            topico.getSuscriptores().add(this);
        } else {
            Topic User = new Topic();
            User.setAdminName(username);
            User.setTopicTitle(username);
            User.getSuscriptores().add(this);
            topics.add(User);
        }
    }

    public void run() {

        try {
            Boolean done = true;
            System.out.println("Num: " + clientesConectados.size());
            while (done) {

                String mensaje = buffEntrada.readUTF();
                //String fileName = "";
                String filePath = "";

                Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
                Matcher regexMatcher = regex.matcher(mensaje);

                ArrayList<String> args = new ArrayList<String>();
                int index = 0;
                while (regexMatcher.find()) {
                    if (regexMatcher.group(1) != null) {
                        args.add(regexMatcher.group(1));
                    } else if (regexMatcher.group(2) != null) {
                        args.add(regexMatcher.group(2));
                    } else {
                        args.add(regexMatcher.group());
                    }
                }
                if (args.get(0).equals("")) {
                    EnviarMensaje("Comando vacio");
                }  else if (args.get(0).equals("enviar")) {

                    CommandLine commandLine = comandos.parse(args.toArray(new String[args.size()]));
                    if (commandLine != null) {
                        if (commandLine.hasOption("m")) {
                            String messageBody = commandLine.getOptionValue("m").trim();
                            if (commandLine.hasOption("t")) {
                                {
                                    String topicName = commandLine.getOptionValue("t").trim();
                                    Topic topic = topics.stream().
                                            filter(current -> topicName.equals(current.getTopicTitle()))
                                            .findAny()
                                            .orElse(null);
                                    if (topic != null) {
                                        topic.Publish(username + ": " + messageBody);
                                    }
                                }
                            }
                        }
                    }
                } else if (args.get(0).equals("crear")) {
                    CommandLine commandLine = comandos.parse(args.toArray(new String[args.size()]));
                    if (commandLine != null) {
                        if (commandLine.hasOption("t")) {
                            String topicName = commandLine.getOptionValue("t").trim();
                            Topic topic = topics.stream()
                                    .filter(current -> topicName.equals(current.getTopicTitle()))
                                    .findAny()
                                    .orElse(null);
                            if (topic == null) {
                                Topic temp = new Topic();
                                temp.setTopicTitle(topicName);
                                temp.setAdminName(username);
                                temp.getSuscriptores().add(this);
                                topics.add(temp);
                                EnviarMensaje("123Topic creado: " + topicName);
                            } else {
                                EnviarMensaje("El topic ya existe");
                            }
                        }
                    }
                } else if (args.get(0).equals("topics")) {

                    for (int i = 0; i < topics.size(); i++) {
                        Topic topicactual = topics.get(i);
                        System.out.println(topicactual.topicTitle);
                        EnviarMensaje("lista " + topicactual.topicTitle);
                    }
                } else if (args.get(0).equals("usuarios")) {
                    for (int i = 0; i < clientesConectados.size(); i++) {
                        Conexion topicactual = clientesConectados.get(i);
                        System.out.println(topicactual.username);
                        EnviarMensaje("usuario " + topicactual.username);
                    }
                } else if (args.get(0).equals("unsuscribe")) {

                    CommandLine commandLine = comandos.parse(args.toArray(new String[args.size()]));
                    if (commandLine != null) {
                        if (commandLine.hasOption("t")) {
                            String topicName = commandLine.getOptionValue("t").trim();
                            Topic topic = topics.stream().
                                    filter(current -> topicName.equals(current.getTopicTitle()))
                                    .findAny()
                                    .orElse(null);
                            if (topic != null) {
                                if (topicName.equals(username) || topicName.equals("BroadCast")) {
                                    EnviarMensaje("Usted no se puede desuscribir de este canal");
                                } else {
                                    topic.getSuscriptores().remove(this);
                                    EnviarMensaje("Ha sido removido del topic: " + topicName);
                                }
                            }
                        }
                    }
                } else if (args.get(0).equals("suscribe")) {
                    CommandLine commandLine = comandos.parse(args.toArray(new String[args.size()]));
                    if (commandLine != null) {
                        if (commandLine.hasOption("t")) {
                            String topicName = commandLine.getOptionValue("t").trim();
                            Topic topic = topics.stream().
                                    filter(current -> topicName.equals(current.getTopicTitle()))
                                    .findAny()
                                    .orElse(null);
                            if (topic != null) {
                                ArrayList privados = new ArrayList();
                                for (int i = 0; i < clientesConectados.size(); i++) {
                                    if (clientesConectados.get(i).username.equals(topicName)) {
                                        privados.add(clientesConectados.get(i).username);
                                    }
                                }
                                if (privados.contains(topicName)) {
                                    EnviarMensaje("No se puede suscribir a este canal");
                                } else {
                                    boolean suscrito = false;
                                    do {
                                        for (int i = 0; i < topic.getSuscriptores().size(); i++) {
                                            if (topic.getSuscriptores().get(i).username.equals(username)) {
                                                System.out.println("Dentro del for ");
                                                suscrito = true;
                                                break;
                                            }
                                        }
                                        if (suscrito == true) {
                                            System.out.println("Dentro del suscrito if");
                                            EnviarMensaje("Usted ya se encuentra suscrito a este topic.");
                                        } else {
                                            topic.getSuscriptores().add(this);
                                            EnviarMensaje("Se ha suscrito al topic: " + topicName);
                                            suscrito = true;
                                        }
                                    } while (suscrito != true);
                                }
                            }
                        }
                    }
                } else if (args.get(0).equals("remove")) {
                    CommandLine commandLine = comandos.parse(args.toArray(new String[args.size()]));
                    if (commandLine != null) {
                        if (commandLine.hasOption("t")) {
                            String topicName = commandLine.getOptionValue("t").trim();
                            if (topicName.endsWith("BroadCast")) {
                                EnviarMensaje("Imposible eliminar el topic BroadCast");
                            } else {
                                Topic topic = topics.stream().
                                        filter(current -> topicName.equals(current.getTopicTitle()))
                                        .findAny()
                                        .orElse(null);
                                if (topic != null) {
                                    ArrayList noremovibles = new ArrayList();
                                    for (int i = 0; i < clientesConectados.size(); i++) {
                                        if (clientesConectados.get(i).username.equals(topicName)) {
                                            noremovibles.add(clientesConectados.get(i).username);
                                        }
                                    }
                                    if (noremovibles.contains(topicName)) {
                                        EnviarMensaje("No se puede remover a este canal");
                                    } else {
                                        for (int i = 0; i < topic.suscriptores.size(); i++) {
                                            if (topic.getName().equals(username)) {
                                                topic.Publish("Se ha eliminado el topic:" + topicName);
                                                topics.remove(topic);
                                            } else {
                                                EnviarMensaje("El usuario no es el dueno");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (args.get(0).equals("GoodBye")) {
                    CommandLine commandLine = comandos.parse(args.toArray(new String[args.size()]));
                    if (commandLine != null) {
                        if (commandLine.hasOption("t")) {
                            Topic topicod = topics.stream().
                                    filter(current -> username.equals(current.getTopicTitle()))
                                    .findAny()
                                    .orElse(null);
                            String topicName = commandLine.getOptionValue("t").trim();
                            Topic topic = topics.stream().
                                    filter(current -> "BroadCast".equals(current.getTopicTitle()))
                                    .findAny()
                                    .orElse(null);
                            if (topic != null) {
                                topics.remove(topicod);
                                clientesConectados.remove(this);
                                Mediator.conexiones.remove(this);
                                topic.Publish("El usuario " + username + " se ha ido");
                            }
                        }
                    }
                } else {

                }
                done = !mensaje.equals("exit");

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        };
    }

    public void EnviarMensaje(String mensaje) {
        try {
            buffSalida.writeUTF(mensaje);
        } catch (IOException e) {
        }
    }
}
