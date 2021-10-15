![juptiar](https://i.ibb.co/ZSyTLsK/juptiar.png)
# Juptiar Project
## Index
 1. [Introduction](#introduction)
 2. [How to use](#tutorial)
## Introduction
**Juptiar Project** consist of 2 part : **Juptiar Script**  and **.jur File**. I made mostly for random text genratoring

(pretty useful generating slightly different textbook program)

**Juptiar Script**

The language is designed for easily assign random value within range. In the given example, `price` was given a value of ##000, where ## is a value between 15 ~ 25

Example :
```
> price = ( 15 ~ 25 ) * 1000
17000
```
**.jur File**

The file describle where the value should be inserted to. Which can later be used on mass production

Example :
```
{
	name = mary
	cost = ( 100 * ( 8 ~ 12 ) )
	marked_rate = ( 3 ~ 9 ) * 5
	marked_price = ( cost f* ( 1 f+ ( marked_rate f/ 100 ) ) )
}
"
	Q  :  {name} sold a pair of sport shoes at ${marked_price}.
	the sport shoes was original cost ${cost}.
	How much does she earned?

	A  :  She earned ${marked_price f- cost}
"
```

## Tutorial
Run the .jar file in command prompt
```
java -jar Juptiar.jar <Target File> <Time> <Output File>
```
There are 3 mode for the program :
 - **Interpreter Mode**
 - **Simulation Mode**
 - **Output Mode**

### Interpreter Mode
When there is no target file or the target file done outputing, the program enter **Interpreter Mode**
```
> <input>
```
```
> 3
3
```
Entering a 3 will gives you back a 3!
Here you can test all types of combination of code
