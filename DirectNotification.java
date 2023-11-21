
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface DirectNotification extends Remote
{
    String Stock_updated(String message, PrivateKey key) throws RemoteException;
}

