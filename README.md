## 🚧 Under construction 🚧

# The "Final store" Project

This project is a comprehensive Java Spring Boot exercise for an e-commerce store API.

Find the roadmap [here](https://github.com/users/venelinpetrov/projects/2/views/1)


## Setup

### Prerequisites 

1. Java
    - Install via Homebrew
    ```bash 
    brew install openjdk@21
    ```
2. MySQL
    - Install via Homebrew
   ```bash
   brew install mysql
   ```
   
    - Start the service
    ```bash
    brew services start mysql
    ```
3. Python 3
   - Should be included if you use Mac or Ubuntu

Optionally, install [DBeaver](https://dbeaver.io/download/) as well.

### Setup

1. User setup
    ```bash 
   mysql
    ```
    ```mysql
    ALTER USER 'root'@'localhost' IDENTIFIED BY '0000';
    FLUSH PRIVILEGES;
    EXIT;
    ```
2. Create a table `my_store`

    ```bash
    mysql
    
    CREATE DATABASE my_store;
    ```

3. Run the migrations by going to the plugins in IntelliJ and clicking on `flyway:migrate`

4. Run the seed script from here: [link](https://github.com/venelinpetrov/my-store-seed-script)
5. Configure the JDK in IntelliJ: [link](https://www.jetbrains.com/help/idea/sdk.html#change-module-sdk)
    ```bash
    # Typical JDK locations MacOS
    /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
    /Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home
   
    # Typical JDK locations Ubuntu
    /usr/lib/jvm/java-17-openjdk-amd64/
    /usr/lib/jvm/java-21-openjdk-amd64/
    ```
   
    Or you can find the location using:
    ```bash
    # MacOS
    /usr/libexec/java_home -V
   
    # Ubuntu
    ls /usr/lib/jvm
    ```
6. Add Spring Boot configuration and select the Main class