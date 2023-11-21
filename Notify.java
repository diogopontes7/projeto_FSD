import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class Notify extends UnicastRemoteObject implements DirectNotification {
	private Cipher decryptCipher;

	public Notify() throws RemoteException {
	}

	@Override
	public String Stock_updated(String encryptedMessage, PrivateKey key) throws RemoteException {
		try {
			try {
				decryptCipher = Cipher.getInstance("RSA");
				decryptCipher.init(Cipher.DECRYPT_MODE, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Decrypt the received message
			byte[] decryptedMessage = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
			String message = new String(decryptedMessage);
			System.out.println(decryptedMessage);
			System.out.println(message);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
