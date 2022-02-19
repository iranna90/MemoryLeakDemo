package example.data;

import java.util.ArrayList;
import java.util.List;

public class CppDataStore {

  private final List<User> users = new ArrayList<>();

  public void store(User user) {
    users.add(user);
  }

  public void storeNative(final String json) {
    storeData(json);
  }

  public native long storeData(String json);
}
