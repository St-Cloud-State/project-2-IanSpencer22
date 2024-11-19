import java.util.*;
import java.io.*;
public class ClientList implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Client> clients = new LinkedList<>();
  private static ClientList clientList;

  private ClientList() {}

  public static ClientList instance() {
    if (clientList == null) {
      return (clientList = new ClientList());
    } else {
      return clientList;
    }
  }

  public boolean insertClient(Client client) {
    clients.add(client);
    return true;
  }

  public Client search(String clientId) {
    for (Client client : clients) {
      if (client.getId().equals(clientId)) {
        return client;
      }
    }
    return null;
  }

  public Iterator<Client> getAllClients() {
    return clients.iterator();
  }

  private void writeObject(ObjectOutputStream output) throws IOException {
    output.defaultWriteObject();
    output.writeObject(clientList);
  }

  private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
    input.defaultReadObject();
    if (clientList == null) {
      clientList = (ClientList) input.readObject();
    } else {
      input.readObject();
    }
  }

  public String toString() {
    return clients.toString();
  }
}
