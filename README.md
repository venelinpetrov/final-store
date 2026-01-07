## 🚧 Under construction 🚧

# The "Final store" Project

This project is a comprehensive Java Spring Boot exercise for an e-commerce store API.

Find the roadmap [here](https://github.com/users/venelinpetrov/projects/2/views/1)


## Setup

### Prerequisites 

1. Java 24
2. MySQL 8
3. Python 3

### Run

1. Create a table `my_store`

```bash
mysql

create database my_store
```

2. User setup

```mysql
ALTER USER 'root'@'localhost' IDENTIFIED BY '0000';
FLUSH PRIVILEGES;
EXIT;
```

3. Run the migrations by going to the plugins in IntelliJ and clicking on `flyway:migrate`

4. Run the seed script from here: https://github.com/venelinpetrov/my-store-seed-script