package mediadorchat;

public class MediadorChat extends Mediator {

    public MediadorChat(int puerto) {

        this.puerto = puerto;
    }

    public static void main(String[] args) {

        MediadorChat servidor = new MediadorChat(9000);
//        Cliente cliente = null;
        servidor.init();

    }

}
