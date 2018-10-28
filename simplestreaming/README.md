# Build

`gradle clean build`

- Run all checks (jxr, pmd findbugs, spotless)
- Build fat jars in build/libs/

# Test

- Run all unit tests:
`gradle clean test`

- Run integration tests
`gradle clean integTest`

- Run all acceptance tests:
`gradle clean acceptTest`

- Run all checks (jxr, pmd findbugs, spotless)
`gradle clean check`

- Reformat code (google-java-format)
`gradle spotlessApply`

# Definition of Done

- Pass all tests
`gradle clean integTest`
