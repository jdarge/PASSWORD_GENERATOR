# PASSWORD_GENERATOR

**Version 0.0.1**

#### Password Generator Tool

### WIP

---
# USAGE
* Given a list of phrases, for example, the user will be able to generate passwords from them.
* The idea is to minimize reliance on saving ENTIRE passwords.
> For example:
> The user has the phrase "password123", a special set of numbers "555", and a personalized set of rules created by the user (example: vowels -> 1337).
> If the json file "rules.json" does not exist, you will be asked to create a rule set for each individual latin alphabet character.
> Afterwards, punch in all your data and you can generate your password. 
> If you ever forget your password run the program w/ your specialized rules set, the base phrase, and your unique ID token.

# COMMANDS
* -h / --h / -help / --help : Displays help information. 
  * ```$ java Main -h```
* -f / --f / -file / --file : Allows the user to specify where their base "password" list is. 
  * ```$ java Main -f ~/Desktop/text.txt```
* -i / --i / -input / --input : Allows the user to specify a single base phrase w/o a file.
  * ```$ java Main -i iknownothing```

# NOTES
* When creating a personalized rules.json file, feel free to kit ```ENTER``` for letters you do not wish to manipulate.
* The rules.json file must be in the directory path.
* If you do not wish to create an ID token, you can enter a non-integer value i.e.```skip``` and it will create a value based on your home path.

# REQUIRMENTS
* GRADLE : makes it easier to use Google's simple-json
* JDK-11 (probably)
