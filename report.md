# Report

## Onboarding experience

## Cyclomatic complexity

### Refactor into smaller functions (decrease complexity)

## Ad-hoc coverage-tool

An ad-hoc coverage tool was implemented. Found here in src.

This tool is very simple, both in functionality and use.

### Building

Simply run in the `src` directory: 

```bash
javac *.java
```

### Running

```
java MethodModifier <classname>-<method>:<method> <classname>-<method>:<method>
```

### Functionality

The java program automatically injects code at places where the code may branch. This is for example ´if´, ´else´ and ´switch´ statements. But also places like ´for´ and ´while´. Each of these places will get an unique ´ID´ (for this run). If the relevant branch is ran in execution, then the injected code will also add that ´ID´ to a ´txt´ file.

After all tests are done, the program can simply go through the entire file and check if there is any ID that does not appear in the ´txt´ file. If this is the case then that branch has not been tested. This then prints the relevant branching instruction. 

### Where does it miss

This does not check all possible branches. For example if there is a ´if´ statement that returns, it is not checked if the code after this statement is also reached. Which can be good. This is a simple fix, we simply have to tell where the ´if´ statement ends and add another check there.

It also does not check for ternary operations or for thrown `errors`.

## Self assessment