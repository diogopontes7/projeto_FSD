
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Notify extends UnicastRemoteObject implements DirectNotification {

	public Notify() throws RemoteException {
		super();
	}
	
	//Implementacao do metodo remoto do DirectNotify
	@Override
	public String Stock_updated(String message) throws RemoteException {
		System.out.println(message);
		return message;
	}

}

