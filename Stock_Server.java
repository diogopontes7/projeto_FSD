
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface Stock_Server extends Remote{
     //Metodos remotos que são definidos
     String stock_request() throws RemoteException;
     String stock_update(String id, int quant) throws RemoteException;
     String addProduto(Produto produto) throws RemoteException;
     String remProduto(String id) throws RemoteException;
     void guardarDados() throws RemoteException;
     void carregarDados() throws RemoteException;
     void subscribe(DirectNotification clientRMI) throws RemoteException;
     void unsubscribe(DirectNotification ClientRMI) throws RemoteException;
     PublicKey get_pubkey() throws RemoteException;
}
