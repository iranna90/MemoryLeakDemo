#include <iostream>

using std::string;

class MyDataStore {
private:
       string data;

public:
      MyDataStore(string data) {
          this->data = data;
      };
};