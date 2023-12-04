## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Grammar for the Scanner

expression → equality ;

equality → comparison ( ( "!=" | "==" ) comparison )* ;

comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

term → factor ( ( "-" | "+" ) factor )* ;

factor → unary ( ( "/" | "*" ) unary )* ;

unary → ( "!" | "-" ) unary | primary ;

primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;

## Overview
### String and build-in functions
![String and build-in functions](https://github.com/Low-Level-Code/jlang/blob/main/media/1.jpeg)

### Array and String Sorting
![Array and String Sorting](https://github.com/Low-Level-Code/jlang/blob/main/media/2.jpeg)

### Defining nested functions with managed scopes
![Defining nested functions with managed scopes](https://github.com/Low-Level-Code/jlang/blob/main/media/3.jpeg)

### Benchmarking with python
![Benchmarking with python](https://github.com/Low-Level-Code/jlang/blob/main/media/4.jpeg)

Not that Python is made completely in C, and JLang is made in Java, although Java is relatively slower than C, the speed is explaned by the lack of features from JLang that Python has.

### Mixed Array Items' types
![Mixed Array Items' types](https://github.com/Low-Level-Code/jlang/blob/main/media/5.jpeg)

### Class Definition, Init fucntion , Super Keyword , This keyword and multiple-class inheritance
![Class Definition, Init fucntion , Super Keyword , This keyword and multiple-class inheritance](https://github.com/Low-Level-Code/jlang/blob/main/media/6.jpeg)

### Python-like syntax for class inheritance
![Python-like syntax for class inheritance](https://github.com/Low-Level-Code/jlang/blob/main/media/7.jpeg)

### Json Objects
![Json Objects](https://github.com/Low-Level-Code/jlang/blob/main/media/8.jpeg)

### Array Built-ins
![Array Built-ins](https://github.com/Low-Level-Code/jlang/blob/main/media/8.jpeg)



# Notes :
The project is not finished yet, although it may seem like it's perfect, there I'm still planning to add file imports, I/O, threading and more...


