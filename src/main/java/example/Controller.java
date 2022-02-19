package example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.data.CppDataStore;
import example.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  private final CppDataStore dataStore;
  private final ObjectMapper objectMapper;

  @Autowired
  public Controller(final ObjectMapper objectMapper) {
    this.dataStore = new CppDataStore();
    this.objectMapper = objectMapper;
  }

  @RequestMapping(
      method = RequestMethod.GET,
      path = "/alive"
  )
  public ResponseEntity<Object> health() {
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
      method = RequestMethod.POST,
      path = "/users/native",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<User> create(@RequestBody User user) throws JsonProcessingException {

    if (isInValidUser(user)) {
      return ResponseEntity.badRequest().build();
    }

    dataStore.storeNative(objectMapper.writeValueAsString(user));
    return ResponseEntity.ok(user);
  }

  @RequestMapping(
      method = RequestMethod.POST,
      path = "/users/heap",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<User> createInHeap(@RequestBody User user) {

    if (isInValidUser(user)) {
      return ResponseEntity.badRequest().build();
    }

    dataStore.store(user);
    return ResponseEntity.ok(user);
  }

  private boolean isInValidUser(final User user) {
    return isInvalid(user.getFirstName()) || isInvalid(user.getLastName()) || isInvalid(user.getEmail());
  }

  private boolean isInvalid(final String data) {
    return data == null || data.isEmpty();
  }
}
