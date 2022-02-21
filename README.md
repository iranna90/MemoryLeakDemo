# Description
This repo can be used as demo repo for finding memory leaks.

Example spring-boot project to show how to find and fix
1. JVM HEAP memory leak
2. Native memory leak from the `.so` shared library file written in C++ 

***More explanation about the how to find memory leaks is explained in the blog post: BLOG_POST_URL***
### Application details
The Application contains 2 rest end points
1. To demonstrate how JVM HEAP memory leak looks like
```
Method: POST
URL: http://localhost:8080/users/heap
Example Body:

{
    "first_name" : "Iranna",
    "last_name" : "Patil",
    "email" : "iranna@mail.com"
}
```
2. To demonstrate Native shared library memory leak
 ```
Method: POST
URL: http://localhost:8080/users/native
Example Body:

{
    "first_name" : "Iranna",
    "last_name" : "Patil",
    "email" : "iranna@mail.com"
}
```

## Prerequisites

1. Ubuntu(.so) and mac-os(.dylib)
2. Java-15: OpenJDK-15
3. Maven - 3.6.3
4. g++ compiler for C++ code
5. `hey` load generator: https://github.com/rakyll/hey
6. `jconsol` from jdk
7. Yourkit profiles for JVM application profiling: https://www.yourkit.com/
8. `jemalloc` for process memory profiling: https://www.yourkit.com/


## Running the Application.
### Below are the steps to run the application.
1. As the application makes JNI call, it needs shared library in the class path, so we need to build `.so` for the code written in C++.
All the C++ code present in the folder "cpp", I have already generated header file for the JAVA native method code    and also implemented corresponding logic in C++.
Below are the steps to generate `.so` library file from C++ code using `g++` compiler.
   1. Compile the CPP file
   ```
    g++ -c -fPIC -I{path}/jdk-15.0.2/include/ -I{path}/jdk-15.0.2/include/linux -I{path_to_cpp_files} {path_to_cpp_files}/datastore.cpp -o {output_directory_path}/datastore.o
   ```
   2. Convert the CPP compiled code to shared library`
   ```
   g++ -shared -fPIC -o {output_directory_path}/libdatastore.so {output_directory_path}/datastore.o -lc
   ```
   **In the repo you can also find the `.so` file in folder `cpp/lib/libdatastore`, and this can be used for the demo if you have any issues in generating this file from C++ code.**
2. Once we generated the `.so` we can build our application `jar` file.
```
mvn clean install
```
3. After generating JAR, now we can run the application and start sending the request.
As we want to profile the whole process we want to use `jemallo` instead of `glibc/libc` to allocate memory.
Please follow the steps to create `jemalloc` library and how to use https://github.com/jemalloc/jemalloc/wiki/
Once we have `jemalloc` built now we can start our application using the `jemalloc` and our own native code shared lib `libdatastore.so` as shown below.
Open new terminal as do below steps.
   1. Make jemalloc as memory allocator instead of glibc/lic malloc
   ```
   export LD_PRELOAD=/usr/local/lib/libjemalloc.so
   ```
   **Make sure you built `jemalloc` lib locally and placed it in above directory by following instructions from `jemalloc` documentation.**

   2. Configure jemalloc so that it will write heap details of the process at every fixed interval to a file.
   ```
   export MALLOC_CONF=prof:true,lg_prof_interval:31,lg_prof_sample:17,prof_prefix:/tmp/heap/jeprof
   ```
   3. Set library path to folder were our `.so` native code library present so that JVM can call native methods, or else we will get `Linking exceptions`
   ```
   export LD_LIBRARY_PATH={output_directory_path}
   ```
   4. Now run the java application from the jar.
   ```
   java -Xmx500m -Xms250m -jar target/MemoryLeakDemo-1.0-SNAPSHOT.jar
   ```
**We can check whether application is alive or not using alive endpoint http://localhost:8080/alive**

## Generating load
We can generate load using light weight async load generator following below steps.
1. Create a `user.json` file having json content which will be used as body for requests.
```
user.json

{
    "first_name" : "Iranna",
    "last_name" : "Patil",
    "email" : "iranna@mail.com"
}
```
2. Now using this file we can generate load on any of the `/users/heas` or `/users/native` endpoint as shown below
```
hey -c 5 -z 20m -m POST -T "application/json" -D user.json http://localhost:8080/users/native
```

## Checking memory leak
### Heap
We can check the heap leak by connecting profiler to the application(Yourkit or jconsol)

### Native
As we told the `jemalloc` to generate the heap details files in a directory, while starting the application.
Now we can use `jeprof`  to plot the graph or text file to show the memory details and also show the leak.
```
#!/bin/bash
jeprof --svg /tmp/heap/jeprof.$1.* >/tmp/heap/$1-report.svg 2>/dev/null
jeprof --text /tmp/heap/jeprof.$1.* >/tmp/heap/$1-report.txt 2>/dev/null
```
The parameter will be the `porcess_id`

Same script file can be found in this repo under folder `profile`

Once this `svg` and `txt` files generated, we can open them and see for memory leak details.

