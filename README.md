![juptiar](https://i.ibb.co/ZSyTLsK/juptiar.png)
# Project Juptiar
## Index
 1. [Introduction](#introduction)
 2. [How to use](#tutorial)
 3. [Document](#operator)
## Introduction
**Project Juptiar** consist of 2 part : **Juptiar Script**  and **.jur File**. I made mostly for random text genratoring

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
`test.jur`
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

### Execution
Run the follow command that runs .jar file in command prompt
```
java -jar Juptiar.jar <Target File> <Time> <Output File>
```
___
### Modes

There are 2 modes for the program :
 - **Interpreter Mode**
 - **Simulation Mode**

#### Interpreter Mode
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
```
> /
Juptiar Halted
```
use a single `/` to halt the program

#### Simulation Mode
When there is a target file is selected, the program enter **Simulation Mode** and simulate the targeted file. It will enter **Interpreter Mode** afterwards
```
java -jar Juptiar.jar test.jur
```
```
	Q  :  mary sold a pair of sport shoes at $1260.0.
	the sport shoes was original cost $900.
	How much does she earned?

	A  :  She earned $360.0

> 
```
## Operator
### Basic
Here are the major operator that can be used in **Juptiar Script**
#### "=" (Assign)
```
v = 3
```
*\*assign the value 3 to `v`*
#### "|" (Or)
```
v = l | r
```
*\*randomly pick one between `l` and `r` value and assign it to `v`*
#### "->" (Unstable Assign)
```
v -> l | r
```
*\*everytime `v` is called, it will randomly return `l` or `r`*
#### "~" (Random Between)
```
v = n1 ~ n2
```
*\*randomly pick a `Integer` between `n1` and `n2` value and assign it to `v`*
#### "+" , "-" , "\*" , "/" , "%" (Basic Maths)
#### "f+" , "f-" , "f*" , "f/" (Float Maths)
#### "==" , "!=" , ">" , "<" , ">=" , "<=" (Logic Operation)
*\*return 0 as `false`, 1 as `true`*
#### "=>" , ":" (If-statement)
```
(c) => ( true : false )
```
*\*if `c` is 0, return true. otherwise, false*
#### "\[^\]" (Accumulator)
```
t [^] ( n1 + n2 )
```
*\*the right value(`n2`) will stack(`+`) to the left value(`n1`), multiple time(`t`), which is equivalent to `n1 + (n2 * t)`*
___
### Other Example
#### Multiple Assignment
```
a = b = c = 5
```
*\*multiple assignment could be done as `=` will returns right value, which in this case : 5*
#### "|" Capability
```
name = John | Maggie
```
*\*`|` can be used other than numbers*
```
a | b = c | d
```
*\*`|` can be used for left value*
___
### Debug Command
#### "?" (Express)
```
> n -> ( 0 ~ 10 )
4
> n
7
> ? n
( 0 ~ 10 )
```
*\*`?` can express the right value in express form*
