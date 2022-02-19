#include <jni.h>
#include <iostream>
#include <list>
#include "example_data_CppDataStore.h"
#include "cpp_datastore.h"

using namespace std;

JNIEXPORT jlong JNICALL Java_example_data_CppDataStore_storeData (JNIEnv * env, jobject thisObject, jstring data) {
    const char *char_string = env->GetStringUTFChars(data, NULL);
    std::string cpp_string = std::string(char_string);
    env->ReleaseStringUTFChars(data, char_string);
    MyDataStore *d = new MyDataStore(cpp_string);
    return 1;
}
