import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) throws RemoteException {
        Inventario sharedInventario = new Inventario(); // Cria uma instancia partilhada do inventario
        sharedInventario.carregarDados();

        // Começa o RMI Server numa nova thread
        new Thread(() -> {
            try {
                // Cria um registro na porta 1099
                Registry registry = LocateRegistry.createRegistry(1099);
                // Associa o objeto do servidor RMI a um nome no registro
                registry.rebind("ServerRMI", sharedInventario);
                System.out.println("RMI server is running...");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();

        // Inicializa o stock Server
        try (ServerSocket socketServer = new ServerSocket(1234)) {
            socketServer.setReuseAddress(true);

            // Loop Infinito á espera de clients do tipo Socket
            while (true) {
                // Estabelece a conexão
                Socket client = socketServer.accept();

                // Mostra que a nova conexão foi feita
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());

                // Instancia uma thread que recebe o client e o inventario como parametros
                ClientHandler clientSock = new ClientHandler(client, sharedInventario);

                // Esta thread é inicializada
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}