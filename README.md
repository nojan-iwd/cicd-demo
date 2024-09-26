This project uses Spring Cloud Function to provide Lambda Handlers for Enterprise API

Documentation: https://spring.io/projects/spring-cloud-function

## Setting up Dev Environment

1. Install docker on your local machine. On MacOS the easiest way to do it is via Homebrew `brew install --cask docker`
2. Run `docker-compose up`
   3. edb will start on port 5433
   4. mainframedb will start on port 5434
   5. They will be seeded automatically from the files located at `./common/src/testFixtures/resources/seed_mf_postgres.sql` and `./common/src/testFixtures/resources/seed_edb_postgres.sql`
3. Install DBeaver or some other DB Client tool: `brew install --cask dbeaver-community` and connect to Postgres:
    - Select Database -> New Database Connection -> Postgres
      - Host: localhost 
      - Port: 5433 for edb, 5434 for mainframedb
      - Database: edb 
      - **IMPORTANT!** Show all databases: ON
      - Username: postgres
      - Password: password1


## Build

`./gradlew build` to build the application (runs tests + creates shaded jar tagged with `-aws`)  
`./gradlew bootBuildImage` to build a docker image (you need docker running)

## Build Native

1. Install SDKMAN: https://sdkman.io/install
2. `sdk install java 23.1.3.r21-nik` -> Install GraalVM that can used to compile Java 21
3. `export GRAALVM_HOME=~/.sdkman/candidates/java/23.1.3.r21-nik/` -> set the GRAALVM to use
4 `./gradlew nativeCompile` -> Compile native


## Running locally

1. Environment Variables: `AWS_REGION=us-east-1`
2. Spring active profile: `local`

## Deployment

See `Actions` tab on Github repository to deploy this to Dev or QA environments.