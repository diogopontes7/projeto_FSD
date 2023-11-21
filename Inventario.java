
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Inventario extends UnicastRemoteObject implements Stock_Server {
    private List<Produto> listaProdutos = new ArrayList<>();
    private List<DirectNotification> clients = new ArrayList<>();

    public Inventario() throws RemoteException {
    }

    // Percorre a lista de clientes e manda um stock_updated a cada clienteRMI conectado
    public void notifyClients(String message) {
        for (DirectNotification client : clients) {
            try {
                client.Stock_updated(message);
            } catch (RemoteException e) {
            }
        }
    }

    public void subscribe(DirectNotification ClientRMI){
        clients.add(ClientRMI);
        System.out.println("Cliente Adicionado");
    }

    public void unsubscribe(DirectNotification ClientRMI){
        clients.remove(ClientRMI);
        System.out.println("Cliente Removido");
    }

    public synchronized String addProduto(Produto produto) throws RemoteException{
        for (Produto produto1 : listaProdutos) {
            if (produto.getId().equals(produto1.getId())
                    || produto.getNome().equalsIgnoreCase(produto1.getNome())) {
                return "Id já existente ou Nome já existente";
            }
        }
        listaProdutos.add(produto);
        return "Produto Criado";
    }

    public synchronized String remProduto(String id) throws RemoteException{
        for (Produto produto : listaProdutos) {
            if (id.equals(produto.getId())) {
                listaProdutos.remove(produto);
                return "Produto removido";
            }
        }
        return "Produto não existe";
    }

    public synchronized String stock_update(String id, int quant) throws RemoteException{
        for (Produto produto : listaProdutos) {
            if (produto.getId().equals(id)) {
                if ((quant * -1) <= produto.getQuantidade()) {
                    produto.setQuantidade(produto.getQuantidade() + quant);
                    notifyClients("Stock updated: ID=" + id + ", Nome: " + produto.getNome() + ", Quantidade=" + quant);
                    return "Alteracao_aprovada";
                } else {
                    return ("Quantidade_Invalida");
                }
            }
        }
        return "Id_invalido";
    }

    public synchronized void guardarDados() {
        try (FileOutputStream ficheiro = new FileOutputStream("invent.dat");
                ObjectOutputStream obj = new ObjectOutputStream(ficheiro)) {
            obj.writeObject(listaProdutos);
            System.out.println("List of products saved to " + "invent.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarDados() {
        File file = new File("invent.dat");

        if (file.exists()) {
            try (
                FileInputStream ficheiro = new FileInputStream(file);
                ObjectInputStream obj = new ObjectInputStream(ficheiro)) {
                List<Produto> loadedListaProdutos = (List<Produto>) obj.readObject();
                listaProdutos.clear();
                listaProdutos.addAll(loadedListaProdutos);
                System.out.println("List of products loaded from invent.dat");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // Se o ficheiro invent.dat não existir, é criado
            try {
                if (file.createNewFile()) {
                    System.out.println("invent.dat criado.");
                } else {
                    System.out.println("Falhou ao criar o invent.dat.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String stock_request() throws RemoteException{
        String texto = "";
        if (listaProdutos.isEmpty()) {
            return "Lista de Produtos vazia";
        } else {
            for (Produto produto : listaProdutos) {
                texto += produto.toString();
            }
            return texto;
        }
    }

}