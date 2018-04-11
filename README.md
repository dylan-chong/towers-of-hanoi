# Assignment

Run tests:

    ./gradlew clean test

and view test results in `build/reports/tests/test/index.html`

Run program:

    ./gradlew clean submit
    java -jar submit/app.jar <YOUR_ARGS>
    # Run the -jar line without any arguments to see the help instructions

Code is in `src/main/`. Almost all of the code is in `src/main/kotlin/`, and
`src/main/kotlin/main/Main.kt` is a good starting point for looking at the code.

Tests are in `src/test/`.