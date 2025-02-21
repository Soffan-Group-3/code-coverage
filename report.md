# Report

## Onboarding experience

### Did you have to install a lot of additional tools to build the software?

The only other software that was needed was ´maven´ (´mvn´). Most java devlopers should already have this though.

### Were those tools well documented?


Most tools are pretty well documented. There is a lot of javadoc about how to use the tools that GSON provides. 

However, most other things like settings, ´pom´ files and structure of files have been much harder to work with. For example, the ´pom´ files make it so that all ´warnings´ are treated as ´errors´. This made the manual DIY-ing very hard.

### Were other components installed automatically by the build script?

Yes since maven is used a lot of other dependencies are downloaded.

### Did the build conclude automatically without errors?

If you ran the project with `mvn clean verify` it did, however ´mvn clean test´ did not work. 

### How well do examples and tests run on your system(s)?

It can be quite hard to get everything to work. A lot of things had to be removed from different `pom` files or ´config´ files, however, when this is done it runs pretty good.

However, since there are ´122´ tests files (and over 4000 tests) compiling goes pretty slow.

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


## Coverage improvement

# Hanna
I have analyzed the function parse(), found @./gson/src/main/java/com/google/gson/internal/bind/util/ISO8601Utils. This is one of the functions with highest code complexity and the Lizard tool shows a CCN of 30 and a length of 185.

Analyzed with my own coverage tool the test cases that visits 11 out of 22 possible branches. This corresponds to a branch coverage of 50%. Three tests were added to the unit tests for the function, testDateParseInvalidTimezone(), testDateParseMismatched Timezone() and testDateParseLeapSecondWithMilliseconds(). With the new tests a total of 17 was visited branches, which gives a coverage of 77%.

## Self assessment
We have been working together for a few weeks and have established a solid way of working, meeting all the criteria for the In Place stage. The entire team actively uses our agreed practices and tools. We primarily use Slack for communication and a GitHub repository to store and collaborate on code, as agreed from the start. We use GitHub Issues and continuous commits to coordinate smoothly.

Our meeting structure supports both independent work and team discussions when needed. We have a mix of scheduled meetings and short notice check-ins, which has worked well. As a result, our way of working meets most of the Working Well criteria. Overall, we are very satisfied with our workflow. Moving forward, we aim to further integrate and streamline our processes. In previous tasks, we successfully used our tools and continuously integrated our work into GitHub. However, due to the short time frame, this task was more challenging, and we aim to furter improve our usage of some tools, such as github issues, for the next assignment.